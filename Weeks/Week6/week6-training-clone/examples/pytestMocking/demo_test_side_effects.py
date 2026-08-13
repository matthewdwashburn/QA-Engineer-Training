#side_effect - Dynamic Mock Behavior

# side_effect allows dynamic responses based on calls
# can return different values on successive calls
#can raise exceptions
#can be a callable for complex logic
# Essential for testing retry logic, error handling, etc.

import pytest
from unittest.mock import Mock, MagicMock

#side_effect for raising exceptions
def test_side_effect_raises_exception():
    """
    Use side_effect to make mock raise an exception.
    """
    mock_func = Mock()
    mock_func.side_effect = ValueError("Invalid Input")

    with pytest.raises(ValueError) as exc_info:
        mock_func()

    assert "Invalid Input" in str(exc_info.value)

def test_side_effect_different_exceptions():
    """
    Different exceptions for different calls
    """
    mock = Mock()
    mock.side_effect = [
        ConnectionError("Network Failed"),
        TimeoutError("Request timed out"),
        {"result": "success"} #finally succeeds
    ]

    with pytest.raises(ConnectionError):
        mock()

    with pytest.raises(TimeoutError):
        mock()

    result = mock()
    assert result == {"result":"success"}

# ==========================================================
# SECTION 2: side_effect with Iterable - Sequential Returns
# ==========================================================

def test_side_effect_sequence():
    """
    Return different values on successive calls.
    """
    mock = Mock()
    mock.side_effect = [10, 20, 30]
    
    assert mock() == 10  # First call
    assert mock() == 20  # Second call
    assert mock() == 30  # Third call
    
    # Fourth call raises StopIteration
    with pytest.raises(StopIteration):
        mock()


def test_retry_logic():
    """
    Test retry logic: fail twice, then succeed.
    """
    mock_api = Mock()
    mock_api.call.side_effect = [
        ConnectionError("Failed"),  # 1st attempt fails
        ConnectionError("Failed"),  # 2nd attempt fails
        {"status": "ok"}            # 3rd attempt succeeds
    ]
    
    # Simulate retry logic
    result = None
    for attempt in range(3):
        try:
            result = mock_api.call()
            break
        except ConnectionError:
            continue
    
    assert result == {"status": "ok"}
    assert mock_api.call.call_count == 3


def test_database_pagination():
    """
    Simulate paginated database results.
    """
    mock_query = Mock()
    mock_query.fetch_page.side_effect = [
        [{"id": 1}, {"id": 2}],  # Page 1
        [{"id": 3}, {"id": 4}],  # Page 2
        []                       # No more pages
    ]
    
    all_results = []
    page = 0
    while True:
        results = mock_query.fetch_page(page)
        if not results:
            break
        all_results.extend(results)
        page += 1
    
    assert len(all_results) == 4
    assert mock_query.fetch_page.call_count == 3


# ==========================================================
# SECTION 3: side_effect with Callable - Dynamic Logic
# ==========================================================

def test_side_effect_callable():
    """
    Use a function for complex side effect logic.
    """
    def dynamic_response(x):
        if x < 0:
            raise ValueError("Negative not allowed")
        return x * 2
    
    mock = Mock(side_effect=dynamic_response)
    
    assert mock(5) == 10
    assert mock(0) == 0
    
    with pytest.raises(ValueError):
        mock(-1)


def test_side_effect_uses_arguments():
    """
    side_effect receives the same arguments as the mock call.
    """
    def echo_args(*args, **kwargs):
        return {"args": args, "kwargs": kwargs}
    
    mock = Mock(side_effect=echo_args)
    
    result = mock(1, 2, 3, key="value")
    
    assert result["args"] == (1, 2, 3)
    assert result["kwargs"] == {"key": "value"}


def test_side_effect_validates_input():
    """
    Use side_effect for input validation in tests.
    """
    def validate_and_save(user):
        if not user.get("email"):
            raise ValueError("Email required")
        if not user.get("name"):
            raise ValueError("Name required")
        user["id"] = 1  # Simulate ID assignment
        return user
    
    mock_repo = Mock()
    mock_repo.save.side_effect = validate_and_save
    
    # Valid user
    result = mock_repo.save({"name": "John", "email": "john@test.com"})
    assert result["id"] == 1
    
    # Invalid user - missing email
    with pytest.raises(ValueError) as exc_info:
        mock_repo.save({"name": "Jane"})
    assert "Email required" in str(exc_info.value)


# ==========================================================
# SECTION 4: Combining side_effect with return_value
# ==========================================================

def test_side_effect_overrides_return_value():
    """
    side_effect takes precedence over return_value.
    """
    mock = Mock(return_value=100)
    mock.side_effect = lambda: 200
    
    # side_effect wins
    assert mock() == 200


def test_clearing_side_effect():
    """
    Set side_effect to None to use return_value again.
    """
    mock = Mock(return_value=100)
    mock.side_effect = ValueError("Error")
    
    # side_effect is active
    with pytest.raises(ValueError):
        mock()
    
    # Clear side_effect
    mock.side_effect = None
    
    # Now return_value is used
    assert mock() == 100


# ==========================================================
# SECTION 5: Real-World Examples
# ==========================================================

def test_api_with_rate_limiting():
    """
    Simulate API rate limiting.
    """
    call_count = 0
    
    def rate_limited_response():
        nonlocal call_count
        call_count += 1
        if call_count <= 2:
            raise Exception("Rate limited - try again later")
        return {"data": "success"}
    
    mock_api = Mock(side_effect=rate_limited_response)
    
    # First two calls are rate limited
    for _ in range(2):
        with pytest.raises(Exception):
            mock_api()
    
    # Third call succeeds
    result = mock_api()
    assert result["data"] == "success"


def test_file_read_simulation():
    """
    Simulate reading file line by line.
    """
    lines = ["line1\n", "line2\n", "line3\n", ""]  # Empty string = EOF
    
    mock_file = MagicMock()
    mock_file.readline.side_effect = lines
    
    all_lines = []
    while True:
        line = mock_file.readline()
        if not line:
            break
        all_lines.append(line.strip())
    
    assert all_lines == ["line1", "line2", "line3"]


def test_conditional_behavior_by_arg():
    """
    Different behavior based on argument value.
    """
    def lookup_user(user_id):
        users = {
            1: {"name": "Alice", "active": True},
            2: {"name": "Bob", "active": True},
            3: {"name": "Charlie", "active": False},
        }
        if user_id not in users:
            return None
        return users[user_id]
    
    mock_repo = Mock()
    mock_repo.find_by_id.side_effect = lookup_user
    
    assert mock_repo.find_by_id(1)["name"] == "Alice"
    assert mock_repo.find_by_id(3)["active"] is False
    assert mock_repo.find_by_id(999) is None

    