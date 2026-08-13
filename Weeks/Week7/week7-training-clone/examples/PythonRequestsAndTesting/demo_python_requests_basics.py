"""
1. requests is THE library for HTTP in Python
2. Simple, intuitive API - great for quick API testing

pip install requests
"""

import requests
import json

#BASE URL

BASE_URL="https://jsonplaceholder.typicode.com"

def section_header(title):
    """Helper to print section headers"""
    print(f"\n{'='*60}")
    print(f" {title}")
    print(f"{'='*60}")


# ==========================================================
# SECTION 1: Basic GET Request
# ==========================================================

def demo_basic_get():
    """
    Show how simple a GET request is in Python.
    Compare to Postman's click-and-send approach.
    """
    section_header("Basic GET Request")
    
    # Simple GET request
    response = requests.get(f"{BASE_URL}/posts/1")
    
    # Response properties
    print(f"Status Code: {response.status_code}")
    print(f"Content-Type: {response.headers['Content-Type']}")
    print(f"Response Time: {response.elapsed.total_seconds():.3f} seconds")
    
    # Parse JSON response
    data = response.json()
    print(f"\nPost Title: {data['title'][:50]}...")
    print(f"User ID: {data['userId']}")


# ==========================================================
# SECTION 2: GET with Query Parameters
# ==========================================================

def demo_get_with_params():
    """
    Query parameters can be passed as dict.
    Much cleaner than string concatenation.
    """
    section_header("GET with Query Parameters")
    
    # Method 1: Query params as dict (RECOMMENDED)
    params = {
        "userId": 1,
        "_limit": 5
    }
    response = requests.get(f"{BASE_URL}/posts", params=params)
    
    print(f"Request URL: {response.url}")  # Shows params in URL
    print(f"Status Code: {response.status_code}")
    
    posts = response.json()
    print(f"Number of posts returned: {len(posts)}")
    
    for post in posts:
        print(f"  - Post {post['id']}: {post['title'][:30]}...")


# ==========================================================
# SECTION 3: POST Request
# ==========================================================

def demo_post_request():
    """
    POST sends data to create resources.
    JSON body is common - show both ways to send it.
    """
    section_header("POST Request")
    
    # Request body as dictionary
    new_post = {
        "title": "Python Requests Demo Post",
        "body": "This post was created using Python's requests library",
        "userId": 1
    }
    
    # POST with JSON body
    response = requests.post(
        f"{BASE_URL}/posts",
        json=new_post  # Automatically sets Content-Type and serializes
    )
    
    print(f"Status Code: {response.status_code}")  # Should be 201
    print(f"Created Post:")
    print(json.dumps(response.json(), indent=2))


# ==========================================================
# SECTION 4: PUT and PATCH Requests
# ==========================================================

def demo_put_patch():
    """
    PUT = Replace entire resource
    PATCH = Update specific fields
    """
    section_header("PUT and PATCH Requests")
    
    # PUT - Full update
    print("PUT Request (Full Update):")
    full_update = {
        "id": 1,
        "title": "Completely New Title",
        "body": "Completely new body content",
        "userId": 1
    }
    
    response = requests.put(f"{BASE_URL}/posts/1", json=full_update)
    print(f"  Status: {response.status_code}")
    print(f"  Updated title: {response.json()['title']}")
    
    # PATCH - Partial update
    print("\nPATCH Request (Partial Update):")
    partial_update = {
        "title": "Only Title Changed"
    }
    
    response = requests.patch(f"{BASE_URL}/posts/1", json=partial_update)
    print(f"  Status: {response.status_code}")
    print(f"  Updated title: {response.json()['title']}")


# ==========================================================
# SECTION 5: DELETE Request
# ==========================================================

def demo_delete():
    """
    DELETE removes resources.
    Usually returns 200 or 204 (No Content).
    """
    section_header("DELETE Request")
    
    response = requests.delete(f"{BASE_URL}/posts/1")
    
    print(f"Status Code: {response.status_code}")
    print(f"Response Body: {response.text}")  # Usually empty or {}


# ==========================================================
# SECTION 6: Headers and Authentication
# ==========================================================

def demo_headers_auth():
    """
    Custom headers are common for API authentication.
    Show different auth methods.
    """
    section_header("Headers and Authentication")
    
    # Custom headers
    headers = {
        "Accept": "application/json",
        "X-Custom-Header": "PythonDemo",
        "Authorization": "Bearer fake-token-12345"
    }
    
    response = requests.get(
        f"{BASE_URL}/posts/1",
        headers=headers
    )
    
    print(f"Request Headers: {dict(response.request.headers)}")
    print(f"Status Code: {response.status_code}")
    
    # Basic Auth (username/password)
    print("\nBasic Auth Example:")
    # response = requests.get(url, auth=('username', 'password'))
    print("  Use: requests.get(url, auth=('username', 'password'))")
    
    # Bearer Token Auth
    print("\nBearer Token Example:")
    print("  Use: headers={'Authorization': 'Bearer <token>'}")


# ==========================================================
# SECTION 7: Response Object Properties
# ==========================================================

def demo_response_object():
    """
    Response object has many useful properties.
    Show the most commonly used ones.
    """
    section_header("Response Object Properties")
    
    response = requests.get(f"{BASE_URL}/users/1")
    
    print("Response Properties:")
    print(f"  .status_code    : {response.status_code}")
    print(f"  .ok             : {response.ok}")  # True if status < 400
    print(f"  .url            : {response.url}")
    print(f"  .elapsed        : {response.elapsed}")
    
    print("\nResponse Headers (selected):")
    print(f"  Content-Type    : {response.headers.get('Content-Type')}")
    print(f"  Content-Length  : {response.headers.get('Content-Length')}")
    
    print("\nResponse Body:")
    print(f"  .text[:100]     : {response.text[:100]}...")
    print(f"  .json()['name'] : {response.json()['name']}")
    
    # Status code checks
    print("\nStatus Code Methods:")
    print(f"  .ok             : {response.ok}")
    print(f"  .is_redirect    : {response.is_redirect}")
    print(f"  .is_permanent_redirect : {response.is_permanent_redirect}")


# ==========================================================
# SECTION 8: Error Handling
# ==========================================================

def demo_error_handling():
    """
    Always handle errors in production code!
    Show different error scenarios.
    """
    section_header("Error Handling")
    
    # Check status code manually
    print("Method 1: Check status_code")
    response = requests.get(f"{BASE_URL}/posts/99999")
    if response.status_code == 404:
        print("  Resource not found!")
    else:
        print(f"  Got: {response.json()}")
    
    # Use raise_for_status()
    print("\nMethod 2: raise_for_status()")
    try:
        response = requests.get(f"{BASE_URL}/posts/99999")
        response.raise_for_status()  # Raises HTTPError for 4xx/5xx
        print(f"  Success: {response.json()}")
    except requests.exceptions.HTTPError as e:
        print(f"  HTTP Error: {e}")
    
    # Handle connection errors
    print("\nMethod 3: Handle Connection Errors")
    try:
        response = requests.get("https://invalid-url.example.com", timeout=2)
    except requests.exceptions.ConnectionError:
        print("  Connection failed!")
    except requests.exceptions.Timeout:
        print("  Request timed out!")
    except requests.exceptions.RequestException as e:
        print(f"  Request error: {e}")


# ==========================================================
# SECTION 9: Sessions
# ==========================================================

def demo_sessions():
    """
    Sessions persist cookies and configuration.
    More efficient for multiple requests to same host.
    """
    section_header("Sessions")
    
    # Create a session
    session = requests.Session()
    
    # Set default headers for all requests
    session.headers.update({
        "Accept": "application/json",
        "User-Agent": "Python-Requests-Demo"
    })
    
    # Make multiple requests with same session
    print("Making requests with session...")
    
    response1 = session.get(f"{BASE_URL}/posts/1")
    print(f"  Request 1: {response1.status_code}")
    
    response2 = session.get(f"{BASE_URL}/posts/2")
    print(f"  Request 2: {response2.status_code}")
    
    response3 = session.get(f"{BASE_URL}/users/1")
    print(f"  Request 3: {response3.status_code}")
    
    # Close session when done
    session.close()
    print("Session closed.")


# ==========================================================
# SECTION 10: Timeout Configuration
# ==========================================================

def demo_timeouts():
    """
    Always set timeouts in production code!
    Prevents hanging requests.
    """
    section_header("Timeout Configuration")
    
    # Single timeout (connect + read combined)
    print("Single timeout (5 seconds):")
    response = requests.get(f"{BASE_URL}/posts/1", timeout=5)
    print(f"  Status: {response.status_code}")
    
    # Separate connect and read timeouts
    print("\nSeparate timeouts (connect=3, read=10):")
    response = requests.get(f"{BASE_URL}/posts/1", timeout=(3, 10))
    print(f"  Status: {response.status_code}")
    
    # Timeout error handling
    print("\nTimeout error handling:")
    try:
        response = requests.get(f"{BASE_URL}/posts", timeout=0.001)
    except requests.exceptions.Timeout:
        print("  Request timed out (as expected with 0.001s timeout)")


# ==========================================================
# MAIN EXECUTION
# ==========================================================

if __name__ == "__main__":
    """
    Run each section one at a time for demonstration.
    Comment out sections you want to skip.
    """
    
    print("\n" + "="*60)
    print(" PYTHON REQUESTS DEMO")
    print("="*60)
    
    demo_basic_get()
    demo_get_with_params()
    demo_post_request()
    demo_put_patch()
    demo_delete()
    demo_headers_auth()
    demo_response_object()
    demo_error_handling()
    demo_sessions()
    demo_timeouts()
    
    section_header("DEMO COMPLETE")
    print("All examples executed successfully!")
