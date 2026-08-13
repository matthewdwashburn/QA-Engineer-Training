import os

from api.auth_controller import auth_bp
from api.expense_controller import expense_bp
from config.app_config import AppConfig
from flask import Flask
from flask_cors import CORS
from repository.expense_repository import ExpenseRepository
from repository.user_repository import UserRepository
from service.authentication_service import AuthenticationService
from service.expense_service import ExpenseService

config = AppConfig()

# 1. Initialize the Flask Application
app = Flask(__name__)

# Origins allowed to call this API from a browser. Configurable because the
# origin depends on where the frontend is served from: 127.0.0.1 locally, the
# instance's public address when hosted. A hardcoded origin silently blocks
# every request from anywhere else.
#
# Defaults to the Vite dev server. Both spellings of the host are listed
# because the browser treats localhost and 127.0.0.1 as separate origins.
DEFAULT_CORS_ORIGINS = "http://localhost:5173,http://127.0.0.1:5173"

cors_origins = [
    origin.strip()
    for origin in os.getenv("CORS_ORIGINS", DEFAULT_CORS_ORIGINS).split(",")
    if origin.strip()
]

if not cors_origins:
    raise ValueError("CORS_ORIGINS was set but contained no origins.")

CORS(
    app,
    origins=cors_origins,
    supports_credentials=True
)

# 2. Set up the dependency injection for the UserRepository and AuthenticationService
# Create the database repository object
user_repo = UserRepository()

# Pass the repository, JWT secret, and JWT expiration to auth service
auth_service = AuthenticationService(
    user_repository=user_repo,
    jwt_secret=config.jwt_secret,
    token_expiration_hours=config.jwt_expiration_hours
)

# Similarly, set up the dependency injection for the ExpenseRepository and ExpenseService
expense_repo = ExpenseRepository()
expense_service = ExpenseService(expense_repository=expense_repo)

# 3. Attach the live service to the app (The Power Strip)
# This is what makes `current_app.auth_service` work in auth.py and auth_controller.py
app.auth_service = auth_service
app.expense_service = expense_service

# 4. Register your web routes (Blueprints)
app.register_blueprint(auth_bp)
app.register_blueprint(expense_bp)


# 5. Turn the server on!
# pragma: no cover
if __name__ == "__main__":
    # Read the server settings from the environment so the same code runs
    # locally and in a container. The defaults keep local behavior unchanged:
    # a developer running `python main.py` still gets 127.0.0.1:5000.
    #
    # Inside Docker, 127.0.0.1 means "this container only", so the container
    # sets FLASK_HOST=0.0.0.0 to accept traffic from the published port.
    host = os.getenv("FLASK_HOST", "127.0.0.1")

    # Environment variables are always strings, so the port needs converting.
    port = int(os.getenv("FLASK_PORT", "5000"))

    # Compared as a string because bool("false") is True in Python.
    # Debug defaults to off: the Werkzeug debugger exposes an interactive
    # Python console to anyone who can reach the port.
    debug = os.getenv("FLASK_DEBUG", "false").lower() == "true"

    print(f"🚀 Starting the Employee Portal Backend on http://{host}:{port}")
    app.run(host=host, port=port, debug=debug)
