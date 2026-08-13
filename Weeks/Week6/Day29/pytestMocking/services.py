"""
Service classes for Thursday mocking demos.

These classes simulate real-world services with external dependencies
that need to be mocked during testing.
"""

import os
import json
from typing import Optional, Dict, Any, List
from dataclasses import dataclass
from datetime import datetime


@dataclass
class User:
    """User data class."""
    id: int
    name: str
    email: str
    active: bool = True
    created_at: datetime = None

    def __post_init__(self):
        if self.created_at is None:
            self.created_at = datetime.now()


class UserRepository:
    """Repository interface for user data access."""

    def find_by_id(self, user_id: int) -> Optional[User]:
        """Find user by ID - in real app, would query database."""
        raise NotImplementedError("Use mock in tests")

    def find_by_email(self, email: str) -> Optional[User]:
        """Find user by email."""
        raise NotImplementedError("Use mock in tests")

    def save(self, user: User) -> User:
        """Save user - in real app, would insert/update database."""
        raise NotImplementedError("Use mock in tests")

    def delete(self, user_id: int) -> bool:
        """Delete user by ID."""
        raise NotImplementedError("Use mock in tests")

    def find_all_active(self) -> List[User]:
        """Find all active users."""
        raise NotImplementedError("Use mock in tests")


class EmailClient:
    """Email sending service."""

    def send(self, to: str, subject: str, body: str) -> bool:
        """Send email - in real app, would use SMTP."""
        raise NotImplementedError("Use mock in tests")

    def send_template(self, to: str, template: str, context: Dict) -> bool:
        """Send templated email."""
        raise NotImplementedError("Use mock in tests")


class ExternalAPIClient:
    """Client for external API calls."""

    def __init__(self, base_url: str, api_key: str = None):
        self.base_url = base_url
        self.api_key = api_key or os.environ.get("API_KEY")

    def get(self, endpoint: str) -> Dict[str, Any]:
        """Make GET request - in real app, would use requests."""
        raise NotImplementedError("Use mock in tests")

    def post(self, endpoint: str, data: Dict) -> Dict[str, Any]:
        """Make POST request."""
        raise NotImplementedError("Use mock in tests")


class UserService:
    """
    User service - The System Under Test (SUT).
    
    This service depends on:
    - UserRepository (data access)
    - EmailClient (notifications)
    """

    def __init__(self, repository: UserRepository, email_client: EmailClient = None):
        self.repository = repository
        self.email_client = email_client

    def get_user(self, user_id: int) -> User:
        """Get user by ID, raise if not found."""
        user = self.repository.find_by_id(user_id)
        if user is None:
            raise ValueError(f"User not found: {user_id}")
        return user

    def create_user(self, name: str, email: str) -> User:
        """Create a new user with email notification."""
        # Validate
        if not name or not name.strip():
            raise ValueError("Name is required")
        if not email or "@" not in email:
            raise ValueError("Valid email is required")

        # Check duplicate
        existing = self.repository.find_by_email(email)
        if existing:
            raise ValueError(f"Email already registered: {email}")

        # Create and save
        user = User(id=0, name=name, email=email)
        saved_user = self.repository.save(user)

        # Send welcome email
        if self.email_client:
            self.email_client.send(
                to=email,
                subject="Welcome!",
                body=f"Welcome to our platform, {name}!"
            )

        return saved_user

    def deactivate_user(self, user_id: int) -> User:
        """Deactivate a user and send notification."""
        user = self.get_user(user_id)
        user.active = False
        saved_user = self.repository.save(user)

        if self.email_client:
            self.email_client.send(
                to=user.email,
                subject="Account Deactivated",
                body="Your account has been deactivated."
            )

        return saved_user

    def get_active_users(self) -> List[User]:
        """Get all active users."""
        return self.repository.find_all_active()


class WeatherService:
    """Service that fetches weather data from external API."""

    def __init__(self, api_client: ExternalAPIClient = None):
        self.api_client = api_client or ExternalAPIClient(
            base_url="https://api.weather.example.com"
        )

    def get_temperature(self, city: str) -> float:
        """Get current temperature for a city."""
        response = self.api_client.get(f"/weather/{city}")
        return response.get("temperature", 0.0)

    def get_forecast(self, city: str, days: int = 5) -> List[Dict]:
        """Get weather forecast."""
        response = self.api_client.get(f"/forecast/{city}?days={days}")
        return response.get("forecast", [])


class FileProcessor:
    """Process files - demonstrates patching file operations."""

    def read_config(self, path: str) -> Dict:
        """Read JSON configuration file."""
        with open(path, 'r') as f:
            return json.load(f)

    def write_output(self, path: str, data: Dict) -> bool:
        """Write output to JSON file."""
        with open(path, 'w') as f:
            json.dump(data, f)
        return True

    def process(self, config_path: str, output_path: str) -> Dict:
        """Read config, process data, write output."""
        config = self.read_config(config_path)
        result = {
            "processed": True,
            "input_keys": list(config.keys()),
            "timestamp": datetime.now().isoformat()
        }
        self.write_output(output_path, result)
        return result

