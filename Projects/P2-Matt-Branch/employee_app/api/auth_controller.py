# Authentication controller for handling API requests related to user authentication, such as login and token validation
from flask import Blueprint, request, jsonify, make_response, current_app
from service.authentication_service import AuthenticationService
from api.auth import require_employee_auth, get_current_user

# 1. Create the Blueprint (The routing map)
auth_bp = Blueprint('auth', __name__, url_prefix='/auth')

def get_auth_service() -> AuthenticationService:
    return current_app.auth_service

@auth_bp.route('/login', methods=['POST'])
def login():
    """Employee login endpoint. Expects JSON payload with 'username' and 'password'(raw)."""
    try:
        # Treat an unreadable JSON body as a client error, not a server failure.
        # `silent=True` keeps parsing errors out of the generic 500 handler.
        data = request.get_json(silent=True)
        if not isinstance(data, dict):
            # Covers unparseable bodies (None) and valid JSON that is not an
            # object, such as a bare string or array, which has no .get().
            return jsonify({"error": "Invalid login request body."}), 400
        if not data.get('username') or not data.get('password'):
            return jsonify({"error": "Username and password are required."}), 400

        username = data.get('username')
        password = data.get('password')

        auth_service = get_auth_service()
        user = auth_service.login(username, password)

        token = auth_service.generate_jwt_token(user)

        response_data = {
            'message': f'Welcome back, {user.username}!',
            'user' : {
                'id': user.id,
                'username': user.username,
                'role': user.role
            }
        }
        response = make_response(jsonify(response_data))

        response.set_cookie(
            'jwt_token', token, httponly=True, secure=False, samesite='Lax',max_age=int(auth_service.token_expiry.total_seconds())
        )

        return response
    
    except ValueError as ve:
        # Catches wrong password or username
        return jsonify({'error': str(ve)}), 401
    except PermissionError as pe:
        # Catches the Manager trying to log in
        return jsonify({'error': str(pe)}), 403
    except Exception:
        current_app.logger.exception("Employee login failed")
        return jsonify({'error': 'Login failed'}), 500

@auth_bp.route('/me', methods=['GET'])
@require_employee_auth
def me():
    """Returns the currently authenticated employee, based on the jwt_token cookie.
    Used by the frontend to check whether a session is still valid before rendering
    a protected page.
    """
    user = get_current_user()
    return jsonify({
        'id': user.id,
        'username': user.username,
        'role': user.role
    })

@auth_bp.route('/logout', methods=['POST'])
def logout():
    """Employee logout endpoint. Clears the JWT token cookie. """
    response = make_response(jsonify({'message': 'Logged out successfully'}))
    
    response.set_cookie('jwt_token', '', expires=0, httponly=True, samesite='lax')
    
    return response

