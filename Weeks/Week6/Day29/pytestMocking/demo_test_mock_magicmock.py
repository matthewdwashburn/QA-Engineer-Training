#Mock is the basic mock class
# MagicMock extends Mock with magig method support (__str__,__len__,etc.)
# Use Mock for simple mocking, MagicMock when magic methods are needed
#spec= ensures mock has same interface as real object
#autospec creates mocks that validate argument signatures

import pytest
from unittest.mock import Mock, MagicMock, create_autospec

# MagicMock - mock with magic methods
def test_magic_mock_supports_magic_methods():
    """MagicMock pre-configures magic methods (__str__,__len__, etc.)"""
    magic = MagicMock()
    # These work out of the box with MagicMock
    str(magic)
    len(magic)
    iter(magic)
    bool(magic)
    magic[0] #__getitem__
    magic["key"] #__getitem___

    #with regular Mock, these would fail without configuration
    regular = Mock()
    #len(regular) #TypeError: object of type 'Mock' has no len()

def test_magic_mock_configure_magic_methods():
    """
    Configure magic method return values.
    """
    magic = MagicMock()

    #configure __len__ to return 5
    magic.__len__.return_value = 5
    assert len(magic) == 5

    #Configure __getitem__ for subscript access
    magic.__getitem__.return_value = "item"
    assert magic[0] =="item"
    assert magic["any_key"] == "item"

def test_magic_mock_iteration():
    """MagicMock can be configured for iteration"""
    magic = MagicMock()
    magic.__iter__.return_value = iter([1,2,3])

    result = list(magic)
    assert result == [1,2,3]

def test_magic_mock_context_manager():
    """
    MagicMock works as a context manager.
    """
    magic = MagicMock()
    magic.__enter__.return_value = "context value"

    with magic as value:
        assert value == "context value"

    magic.__enter__.assert_called_once()
    magic.__exit__.assert_called_once()

#create_autospec

class Calculator:
    """Sample class to demonstrate spec."""
    
    def add(self, a: int, b: int) -> int:
        return a + b
    
    def multiply(self, a: int, b: int) -> int:
        return a * b

def test_autospec_with_mocker(mocker):
    """
    Using autospec with pytest-mock
    """
    mock_calc = mocker.create_autospec(Calculator)
    #mock_calc = mocker.Mock(spec=Calculator)
    mock_calc.add.return_value = 100

    #result = mock_calc.add(1) #would pass tests with spec but not autospec
    result = mock_calc.add(50,50) 
    assert result == 100


