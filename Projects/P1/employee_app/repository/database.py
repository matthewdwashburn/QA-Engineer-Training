# Connection Factory
# Responsible for creating database connections.
# Uses environment variables to determine which database file to connect to.

from contextlib import contextmanager
import sqlite3
import os
from typing import Iterator, Optional
from dotenv import load_dotenv

# Loads variables from the .env file into the application environment.
# This allows us to access values like DATABASE_ENV.
load_dotenv()


# Initiate a stateless and context-managed connection to the database
class ConnectToDB:
    """
    Creates stateless, context-managed database connections.
    The database used is selected based on the DATABASE_ENV value in .env.

    Examples:
        DATABASE_ENV=development -> expense_manager.db
        DATABASE_ENV=testing     -> expense_manager_test.db
    """

    def __init__(self, db_path: Optional[str] = None):

        # Allows tests to provide their own database path.
        # Example:
        # ConnectToDB("temporary_test_database.db")
        #
        # This allows pytest tests to use isolated databases instead
        # of the application's normal database.
        if db_path:
            self.db_path = db_path

        else:
            # If no database path is manually provided,
            # determine the database using DATABASE_ENV from .env.
            self.db_path = self._get_database_from_env()


    def _get_database_from_env(self):
        """
        Selects the database file based on the DATABASE_ENV environment variable.
        """

        # Reads DATABASE_ENV from .env.
        # Defaults to development if DATABASE_ENV is not provided.
        database_environment = os.getenv(
            "DATABASE_ENV",
            "development"
        )


        # Maps each environment to its corresponding database file.
        databases = {
            "development": "expense_manager.db",
            "testing": "expense_manager_test.db",
        }


        # Prevents the application from starting with an invalid database setting.
        if database_environment not in databases:
            raise ValueError(
                f"Invalid DATABASE_ENV '{database_environment}'. "
                f"Choose from {list(databases.keys())}"
            )


        # Gets the folder containing this file.
        # Example:
        # p1-group6/employee_app/repository/database.py
        current_dir = os.path.dirname(
            os.path.abspath(__file__)
        )


        # Moves up two folders to reach the project root.
        # repository -> employee_app -> p1-group6
        project_root = os.path.dirname(
            os.path.dirname(current_dir)
        )


        # Gets the database filename associated with the environment.
        database_file = databases[database_environment]


        # Creates the full path to the database file.
        # Example:
        # p1-group6/database/expense_manager_test.db
        return os.path.join(
            project_root,
            "database",
            database_file
        )


    @contextmanager
    def get_connection(self) -> Iterator[sqlite3.Connection]:
        """
        Creates and manages a SQLite database connection.

        The context manager automatically closes the connection
        after the operation completes, even if an error occurs.
        """

        # Opens a connection to the selected database.
        conn = sqlite3.connect(self.db_path)


        # Allows database rows to be accessed by column name.
        #
        # Without this:
        # row[0] -> username
        #
        # With this:
        # row["username"] -> username
        conn.row_factory = sqlite3.Row


        # Enables SQLite foreign key enforcement.
        conn.execute(
            "PRAGMA foreign_keys = ON"
        )


        try:
            yield conn

        finally:
            conn.close()