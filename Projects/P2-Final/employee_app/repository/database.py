# Connection Factory
# Responsible for handing out pooled PostgreSQL connections.
# Uses environment variables to determine which database to connect to.

from contextlib import contextmanager
import os
import threading
from typing import Dict, Iterator, Optional

import psycopg
from psycopg.rows import dict_row
from psycopg_pool import ConnectionPool
from dotenv import load_dotenv

# Loads variables from the .env file into the application environment.
# This allows us to access values like DATABASE_URL and DATABASE_ENV.
load_dotenv()


# Used when nothing overrides it, and written for a developer running the
# stack's Postgres on the published port. In a container DATABASE_URL is set.
# 5433 matches compose.yaml, which avoids 5432 so a natively installed Postgres
# cannot shadow the container.
DEFAULT_DSN = "postgresql://expense:expense@localhost:5433/expense_manager"

# Database names each DATABASE_ENV selects, mirroring the old .db filenames.
DATABASE_NAMES = {
    "development": "expense_manager",
    "testing": "expense_manager_test",
}


def resolve_dsn() -> str:
    """
    Resolves the connection string. Precedence: DATABASE_URL, then the
    database named by DATABASE_ENV, then DEFAULT_DSN.

    DATABASE_URL deliberately wins over DATABASE_ENV. A developer who leaves
    DATABASE_ENV=testing set would otherwise redirect a containerised app onto
    the load-test database, which seed_load.py truncates.
    """

    url = os.getenv("DATABASE_URL")
    if url:
        return url

    database_environment = os.getenv("DATABASE_ENV", "development")

    # Prevents the application from starting with an invalid database setting.
    if database_environment not in DATABASE_NAMES:
        raise ValueError(
            f"Invalid DATABASE_ENV '{database_environment}'. "
            f"Choose from {list(DATABASE_NAMES.keys())}"
        )

    # Swap the database name on the end of the default DSN, keeping its
    # host, port and credentials.
    base, _, _ = DEFAULT_DSN.rpartition("/")
    return f"{base}/{DATABASE_NAMES[database_environment]}"


# One pool per DSN, shared by every repository pointed at that database.
# UserRepository and ExpenseRepository are separate objects built with the
# same DSN; without this cache each would open a pool of its own.
_POOLS: Dict[str, ConnectionPool] = {}
_POOLS_LOCK = threading.Lock()


def _get_pool(dsn: str) -> ConnectionPool:
    """Returns the pool for a DSN, opening it on first use."""

    pool = _POOLS.get(dsn)
    if pool is not None:
        return pool

    with _POOLS_LOCK:
        # Re-check inside the lock: two threads can miss above.
        pool = _POOLS.get(dsn)
        if pool is None:
            pool = ConnectionPool(
                dsn,
                min_size=int(os.getenv("DB_POOL_MIN_SIZE", "1")),
                max_size=int(os.getenv("DB_POOL_MAX_SIZE", "10")),
                kwargs={"row_factory": dict_row},
                # Checked on checkout: a connection killed while idle 500s otherwise.
                check=ConnectionPool.check_connection,
                # Opened explicitly rather than in the constructor, so importing
                # this module never depends on the database being reachable.
                open=False,
            )
            pool.open()
            _POOLS[dsn] = pool

    return pool


def close_pools() -> None:
    """Closes every open pool. Used by test teardown."""

    with _POOLS_LOCK:
        for pool in _POOLS.values():
            pool.close()
        _POOLS.clear()


# Hands out pooled, context-managed connections to the database
class ConnectToDB:
    """
    Provides pooled, context-managed database connections.
    The database used is selected by resolve_dsn().

    Examples:
        DATABASE_ENV=development -> expense_manager
        DATABASE_ENV=testing     -> expense_manager_test
    """

    def __init__(self, dsn: Optional[str] = None):

        # Allows tests to provide their own connection string.
        # Example:
        # ConnectToDB("postgresql://user:pass@localhost:5433/throwaway")
        #
        # This allows pytest tests to use isolated databases instead
        # of the application's normal database.
        self.dsn = dsn if dsn else resolve_dsn()

    @contextmanager
    def get_connection(self) -> Iterator[psycopg.Connection]:
        """
        Checks a connection out of the pool for the duration of the block.

        The context manager returns the connection to the pool afterwards,
        committing on success and rolling back if an error escapes the block.
        Rows are dicts, so they are accessed by column name:

        row["username"] -> username
        """

        with _get_pool(self.dsn).connection() as conn:
            yield conn
