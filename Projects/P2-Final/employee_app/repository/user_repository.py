# Contains the UserRepository class for interacting with the users table in the database.
from .database import ConnectToDB
from .user_model import User
from typing import Optional

class UserRepository:
    def __init__(self, dsn: Optional[str] = None):
        self.db = ConnectToDB(dsn)

    def find_by_username(self, username: str) -> Optional[User]:
        """Retrieve a single user by their username. Returns None if the user does not exist."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, username, password, role FROM users WHERE username = %s", (username,))
            row = cursor.fetchone()
            if row:
                return User(id=row["id"], username=row["username"], password=row["password"], role=row["role"])
            return None


    def find_by_id(self, user_id: int) -> Optional[User]:
        """Retrieve a single user by their ID. Returns None if the user does not exist."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, username, password, role FROM users WHERE id = %s", (user_id,))
            row = cursor.fetchone()
            if row:
                return User(id=row["id"], username=row["username"], password=row["password"], role=row["role"])
            return None

    def create_user(self, user: User) -> User:
        """Inserts a new user into the database and returns the user with their new ID."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            # RETURNING is how Postgres reports the generated id, in place of lastrowid
            cursor.execute(
                "INSERT INTO users (username, password, role) VALUES (%s, %s, %s) RETURNING id",
                (user.username, user.password, user.role)
            )
            user.id = cursor.fetchone()["id"]
            conn.commit()
            return user
