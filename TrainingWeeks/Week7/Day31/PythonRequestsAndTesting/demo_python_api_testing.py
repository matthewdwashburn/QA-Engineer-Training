"""
Demo: Python API Testing with pytest

1. pytest is the most popular Python testing framework
2. Fixtures provide setup/teardown functionality
3. Parametrize enables data-driven testing
4. Assertions are simple and readable

INSTALLATION:
pip install pytest requests pytest-html

RUN TESTS:
pytest demo_python_api_testing.py -v
pytest demo_python_api_testing.py -v --html=report.html
pytest demo_python_api_testing.py -v -s --cov=. --cov-report=html
(last if install pytest-cov)
"""

import pytest
import requests

# ==========================================================
# FIXTURES
# ==========================================================

@pytest.fixture(scope="module")
def base_url():
    """
    Fixtures provide reusable test data/configuration.
    scope="module" means it's created once per test file.
    """
    return "https://jsonplaceholder.typicode.com"


@pytest.fixture(scope="module")
def session():
    """
    Session fixture maintains connection pool.
    More efficient for multiple requests.
    """
    sess = requests.Session()
    sess.headers.update({
        "Accept": "application/json",
        "Content-Type": "application/json"
    })
    yield sess
    sess.close()


@pytest.fixture
def sample_post():
    """
    Fixture for test data.
    Can be overridden or parameterized.
    """
    return {
        "title": "Test Post",
        "body": "Test Body Content",
        "userId": 1
    }


# ==========================================================
# BASIC TEST FUNCTIONS
# ==========================================================

class TestBasicRequests:
    """
    Group related tests in classes.
    pytest discovers tests by name (test_* or *_test).
    """
    
    def test_get_single_post(self, base_url, session):
        """Test GET request for single post"""
        response = session.get(f"{base_url}/posts/1")
        
        assert response.status_code == 200
        assert response.headers["Content-Type"] == "application/json; charset=utf-8"
        
        data = response.json()
        assert data["id"] == 1
        assert data["userId"] == 1
        assert "title" in data
        assert "body" in data
    
    def test_get_all_posts(self, base_url, session):
        """Test GET request for all posts"""
        response = session.get(f"{base_url}/posts")
        
        assert response.status_code == 200
        
        posts = response.json()
        assert isinstance(posts, list)
        assert len(posts) == 100
    
    def test_get_nonexistent_post(self, base_url, session):
        """Test GET for non-existent resource returns 404"""
        response = session.get(f"{base_url}/posts/99999")
        
        assert response.status_code == 404
    
    def test_create_post(self, base_url, session, sample_post):
        """Test POST request to create resource"""
        response = session.post(f"{base_url}/posts", json=sample_post)
        
        assert response.status_code == 201
        
        data = response.json()
        assert data["title"] == sample_post["title"]
        assert data["body"] == sample_post["body"]
        assert data["userId"] == sample_post["userId"]
        assert "id" in data  # Server assigns ID
    
    def test_update_post(self, base_url, session):
        """Test PUT request to update resource"""
        update_data = {
            "id": 1,
            "title": "Updated Title",
            "body": "Updated Body",
            "userId": 1
        }
        
        response = session.put(f"{base_url}/posts/1", json=update_data)
        
        assert response.status_code == 200
        assert response.json()["title"] == "Updated Title"
    
    def test_delete_post(self, base_url, session):
        """Test DELETE request"""
        response = session.delete(f"{base_url}/posts/1")
        
        assert response.status_code == 200


# ==========================================================
# PARAMETERIZED TESTS
# ==========================================================

class TestParameterized:
    """
    @pytest.mark.parametrize runs test multiple times with different data.
    Similar to JUnit5's @ParameterizedTest.
    """
    
    @pytest.mark.parametrize("post_id", [1, 2, 3, 4, 5])
    def test_get_posts_by_id(self, base_url, session, post_id):
        """Test GET for multiple post IDs"""
        response = session.get(f"{base_url}/posts/{post_id}")
        
        assert response.status_code == 200
        assert response.json()["id"] == post_id
    
    @pytest.mark.parametrize("user_id,expected_name", [
        (1, "Leanne Graham"),
        (2, "Ervin Howell"),
        (3, "Clementine Bauch"),
        (4, "Patricia Lebsack"),
        (5, "Chelsey Dietrich")
    ])
    def test_user_names(self, base_url, session, user_id, expected_name):
        """Test user names match expected values"""
        response = session.get(f"{base_url}/users/{user_id}")
        
        assert response.status_code == 200
        assert response.json()["name"] == expected_name
    
    @pytest.mark.parametrize("endpoint,expected_count", [
        ("/posts", 100),
        ("/users", 10),
        ("/comments", 500),
        ("/albums", 100),
        ("/photos", 5000),
        ("/todos", 200)
    ])
    def test_resource_counts(self, base_url, session, endpoint, expected_count):
        """Test that each endpoint returns expected number of items"""
        response = session.get(f"{base_url}{endpoint}")
        
        assert response.status_code == 200
        assert len(response.json()) == expected_count


# ==========================================================
# RESPONSE VALIDATION
# ==========================================================

class TestResponseValidation:
    """
    Comprehensive response validation patterns.
    """
    
    def test_response_time(self, base_url, session):
        """Test response time is acceptable"""
        response = session.get(f"{base_url}/posts/1")
        
        assert response.status_code == 200
        # Response time in seconds
        assert response.elapsed.total_seconds() < 2.0
    
    def test_response_headers(self, base_url, session):
        """Test response headers"""
        response = session.get(f"{base_url}/posts/1")
        
        assert "Content-Type" in response.headers
        assert "application/json" in response.headers["Content-Type"]
    
    def test_json_structure(self, base_url, session):
        """Test JSON response structure"""
        response = session.get(f"{base_url}/users/1")
        data = response.json()
        
        # Required fields exist
        required_fields = ["id", "name", "username", "email", "address", "phone", "website", "company"]
        for field in required_fields:
            assert field in data, f"Missing required field: {field}"
        
        # Nested structure
        assert "street" in data["address"]
        assert "city" in data["address"]
        assert "geo" in data["address"]
        assert "lat" in data["address"]["geo"]
        assert "lng" in data["address"]["geo"]
    
    def test_email_format(self, base_url, session):
        """Test email format validation"""
        response = session.get(f"{base_url}/users/1")
        email = response.json()["email"]
        
        assert "@" in email
        assert "." in email.split("@")[1]
    
    def test_all_posts_have_required_fields(self, base_url, session):
        """Test all posts have required fields"""
        response = session.get(f"{base_url}/posts")
        posts = response.json()
        
        for post in posts:
            assert "id" in post
            assert "userId" in post
            assert "title" in post
            assert "body" in post
            assert isinstance(post["id"], int)
            assert isinstance(post["userId"], int)


# ==========================================================
# ERROR HANDLING TESTS
# ==========================================================

class TestErrorHandling:
    """
    Test negative scenarios and error responses.
    """
    
    def test_invalid_endpoint(self, base_url, session):
        """Test 404 for invalid endpoint"""
        response = session.get(f"{base_url}/invalid-endpoint")
        
        # JSONPlaceholder returns empty object for invalid routes
        assert response.status_code == 404 or response.json() == {}
    
    def test_invalid_method(self, base_url, session):
        """Test invalid HTTP method handling"""
        # Try to POST to a single resource endpoint
        response = session.post(f"{base_url}/posts/1", json={"title": "test"})
        
        # JSONPlaceholder might accept this, real API would reject
        # This demonstrates testing API behavior
        assert response.status_code in [200, 201, 404, 405]


# ==========================================================
# CRUD FLOW TEST
# ==========================================================

class TestCrudFlow:
    """
    Test complete CRUD flow in sequence.
    """
    
    def test_complete_crud_flow(self, base_url, session):
        """Test Create, Read, Update, Delete flow"""
        # CREATE
        new_post = {
            "title": "CRUD Test Post",
            "body": "Testing CRUD operations",
            "userId": 1
        }
        create_response = session.post(f"{base_url}/posts", json=new_post)
        assert create_response.status_code == 201
        
        created_id = create_response.json()["id"]
        print(f"\nCreated post with ID: {created_id}")
        
        # READ
        read_response = session.get(f"{base_url}/posts/{created_id}")
        # Note: JSONPlaceholder doesn't actually persist, so this might 404
        # In a real API, this would return 200
        assert read_response.status_code in [200, 404]
        
        # UPDATE
        update_data = {
            "id": created_id,
            "title": "Updated CRUD Test",
            "body": "Updated content",
            "userId": 1
        }
        update_response = session.put(f"{base_url}/posts/{created_id}", json=update_data)
        # assert update_response.status_code == 200
        # would work with your api, but not with JSONPlaceholder
        print(update_response.status_code)
        
        # DELETE
        delete_response = session.delete(f"{base_url}/posts/{created_id}")
        assert delete_response.status_code == 200
        
        print("CRUD flow completed successfully!")



