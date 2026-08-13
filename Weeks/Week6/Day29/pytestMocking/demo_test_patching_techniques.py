"""
Demo: Patching Techniques - @patch, patch(), and Where to Patch

1. @patch decorator for test-level patching
2. patch() as context manager for scoped patches
3. CRITICAL: Patch where object is USED, not where it's DEFINED
4. patch.object for patching methods on objects
5. patch.dict for patching dictionaries (like os.environ)

RUN THIS WITH:
    pytest demo_test_patching_techniques.py -v
"""

import pytest
import os
import json
from unittest.mock import patch, MagicMock
from services import WeatherService, FileProcessor, ExternalAPIClient


# ==========================================================
# SECTION 1: @patch Decorator
# ==========================================================

@patch('os.path.exists')
def test_patch_decorator(mock_exists):
    """
    @patch decorator creates mock and passes it as argument.
    
    Mock is automatically cleaned up after test.
    """
    mock_exists.return_value = True
    
    assert os.path.exists('/any/path') is True
    mock_exists.assert_called_with('/any/path')


@patch('os.path.exists', return_value=True)
def test_patch_with_return_value(mock_exists):
    """
    Set return_value directly in decorator.
    """
    assert os.path.exists('/fake/path') is True


@patch('os.remove')
@patch('os.path.exists')
def test_multiple_patches(mock_exists, mock_remove):
    """
    Multiple @patch decorators - note argument order!
    
    IMPORTANT: Arguments are in REVERSE order of decorators
    (bottom decorator = first argument)
    """
    mock_exists.return_value = True
    
    if os.path.exists('/test/file'):
        os.remove('/test/file')
    
    mock_exists.assert_called_once_with('/test/file')
    mock_remove.assert_called_once_with('/test/file')


# ==========================================================
# SECTION 2: patch() as Context Manager
# ==========================================================

def test_patch_context_manager():
    """
    Use patch() as context manager for scoped patching.
    """
    with patch('os.getcwd') as mock_getcwd:
        mock_getcwd.return_value = '/mocked/directory'
        
        result = os.getcwd()
        
        assert result == '/mocked/directory'
    
    # Outside context, patch is removed
    # os.getcwd() now returns real value


def test_nested_patches():
    """
    Nest context managers for multiple patches.
    """
    with patch('os.path.exists') as mock_exists:
        with patch('os.path.isfile') as mock_isfile:
            mock_exists.return_value = True
            mock_isfile.return_value = True
            
            assert os.path.exists('/test') is True
            assert os.path.isfile('/test') is True


# ==========================================================
# SECTION 3: WHERE to Patch - The Golden Rule
# ==========================================================

# services.py does: from external_api import ExternalAPIClient
# So we patch where it's USED (services.ExternalAPIClient)
# NOT where it's DEFINED (external_api.ExternalAPIClient)

def test_patch_where_used(mocker):
    """
    CRITICAL: Patch at the import location, not definition.
    
    If services.py does:
        from external_api import ExternalAPIClient
    
    Then patch:
        'services.ExternalAPIClient' (where it's used)
    NOT:
        'external_api.ExternalAPIClient' (where it's defined)
    """
    # Patch where ExternalAPIClient is used (in services module)
    mock_client_class = mocker.patch('services.ExternalAPIClient')
    mock_instance = mock_client_class.return_value
    mock_instance.get.return_value = {"temperature": 72.0}
    
    # Now when WeatherService creates ExternalAPIClient, it gets our mock
    service = WeatherService()  # Uses patched ExternalAPIClient
    temp = service.get_temperature("NYC")
    
    assert temp == 72.0


# ==========================================================
# SECTION 4: patch.object - Patching Methods
# ==========================================================

def test_patch_object():
    """
    patch.object patches a specific attribute on an object.
    """
    processor = FileProcessor()
    
    with patch.object(processor, 'read_config') as mock_read:
        mock_read.return_value = {"key": "value"}
        
        result = processor.read_config('/any/path')
        
        assert result == {"key": "value"}
        mock_read.assert_called_once_with('/any/path')


@patch.object(FileProcessor, 'write_output')
@patch.object(FileProcessor, 'read_config')
def test_patch_object_decorator(mock_read, mock_write):
    """
    Use patch.object as decorator.
    """
    mock_read.return_value = {"setting": "value"}
    mock_write.return_value = True
    
    processor = FileProcessor()
    result = processor.process('/config.json', '/output.json')
    
    mock_read.assert_called_once()
    mock_write.assert_called_once()


# ==========================================================
# SECTION 5: patch.dict - Patching Dictionaries
# ==========================================================

@patch.dict(os.environ, {'API_KEY': 'test-key', 'DEBUG': 'true'})
def test_patch_dict_decorator():
    """
    patch.dict patches dictionary contents.
    """
    assert os.environ['API_KEY'] == 'test-key'
    assert os.environ['DEBUG'] == 'true'


def test_patch_dict_context():
    """
    patch.dict as context manager.
    """
    original_path = os.environ.get('PATH')
    
    with patch.dict(os.environ, {'PATH': '/mocked/path'}):
        assert os.environ['PATH'] == '/mocked/path'
    
    # Restored after context
    assert os.environ.get('PATH') == original_path


@patch.dict(os.environ, {}, clear=True)
def test_patch_dict_clear():
    """
    clear=True removes all existing items first.
    """
    # Environment is empty except what we add
    assert 'PATH' not in os.environ  # Cleared!


# ==========================================================
# SECTION 6: Patching Built-ins
# ==========================================================

def test_patch_builtin_open(mocker):
    """
    Patch built-in open() function.
    """
    mock_open = mocker.patch('builtins.open', mocker.mock_open(
        read_data='{"key": "value"}'
    ))
    
    with open('/fake/file.json') as f:
        data = json.load(f)
    
    assert data == {"key": "value"}
    mock_open.assert_called_once_with('/fake/file.json')


def test_patch_print(mocker, capsys):
    """
    Patch print to verify output.
    """
    mock_print = mocker.patch('builtins.print')
    
    print("Hello, World!")
    
    mock_print.assert_called_once_with("Hello, World!")


# ==========================================================
# SECTION 7: start() and stop() for Manual Control
# ==========================================================

class TestManualPatchControl:
    """
    Use start() and stop() for setup/teardown-based patching.
    """
    
    def setup_method(self):
        """Called before each test method."""
        self.patcher = patch('os.path.exists')
        self.mock_exists = self.patcher.start()
        self.mock_exists.return_value = True
    
    def teardown_method(self):
        """Called after each test method."""
        self.patcher.stop()
    
    def test_with_setup_patch(self):
        """Test uses patch from setup."""
        assert os.path.exists('/any/path') is True
    
    def test_another_with_setup_patch(self):
        """Another test uses same patch setup."""
        self.mock_exists.return_value = False
        assert os.path.exists('/another/path') is False

