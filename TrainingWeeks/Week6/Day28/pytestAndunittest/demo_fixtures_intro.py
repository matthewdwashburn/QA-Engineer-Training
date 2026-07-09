#PyTest Fixtures - Setup, Teardown, and Dependency Injection
# Fixtures are pytests's anser to setup/teardown - but WAY more powerful
#Fixtures use dependency injection - just add parameter to test function
#use 'yield' for setup-teardown pattern (cleanup runs even if test fails)
#Scopes control fixture lifetime: function, class, module, session
#conftest.py shares fixtures across test files

#RUN THIS WITH:
    #pytest demo_fixtures_intro.py -v
    #pytest demo_fixtures_intro.py -v -s # -s shows print statements

import pytest
from calculator import Calculator, StringCalculator

#Basic Fixtures

@pytest.fixture
def calculator():
    """provide a calculator instance for tests
    Any test that has 'calculator' as a parameter gets this fixture injected"""
    return Calculator()
@pytest.fixture
def string_calculator():
    """Provide a StringCalculator instance."""
    return StringCalculator()

def test_add_with_fixture(calculator):
    """Calculator is automaticlly injected!"""
    result = calculator.add(2,3)
    assert result == 5

def test_subtract_with_fixture(calculator):
    """Each test gets a fresh calculator instance"""
    result = calculator.subtract(10,3)
    assert result == 7

def test_string_add(string_calculator):
    result = string_calculator.add("1,2,3")
    assert result == 6

# Fixtures with Setup and TearDown(yield)
@pytest.fixture
def temp_file(tmp_path):
    """Create a temp file, provide it, then clean it up
    'tmp_path is a built-in pytest fixture"""

    #SETUP
    file_path = tmp_path / "test_data.txt"
    file_path.write_text("test content")
    print(f"\n[SETUP] Created temo file: {file_path}")

    #PROVIDE TO TEST
    yield file_path

    #TEARDOWN (runs even if test fails!)
    print(f"[TEARDOWN] Cleaning up : {file_path}")
    if file_path.exists():
        file_path.unlink()

def test_temp_file_exists(temp_file):
    """Test receives the temp file path."""
    assert temp_file.exists()
    assert temp_file.read_text() == "test content"

def test_temp_file_can_be_modified(temp_file):
    """Each test gets its own temp file."""
    temp_file.write_text("modified content")
    assert temp_file.read_text() == "modified content"

#Fixture Scopes
@pytest.fixture(scope="function") #default - new instance per test
def function_scoped_calc():
    print("\n[FUNCTION FIXTURE] Creating calculator")
    calc = Calculator()
    yield calc
    
    print("[FUNCTION FIXTURE] cleaning up")

@pytest.fixture(scope = "class") #one instance for entire module
def class_scoped_calc():
    print("\n[CLASS FIXTURE] Creating calculator (once per class)")
    calc = Calculator()

    yield calc

    print("[CLASS FIXTURE] Cleaning up")

@pytest.fixture(scope = "module") #one instance for entire module
def module_scoped_calc():
    print("\n[MODULE FIXTURE] Creating calculator (once per module)")
    calc = Calculator()

    yield calc

    print("[MODULE FIXTURE] Cleaning up")

class TestModuleScoped:
    """Tests sharing module-scoped fixture"""
    def test_module_1(self, module_scoped_calc):
        """Uses shared module calculator"""
        assert module_scoped_calc.add(1,1)==2
    
    def test_module_2(self, module_scoped_calc):
        """Same calculator instance as test_module_1"""
        assert module_scoped_calc.add(2,2)==4

# ==========================================================
# SECTION 4: Fixture Dependencies (Fixtures using Fixtures)
# ==========================================================

@pytest.fixture
def base_config():
    """Base configuration."""
    return {
        "debug": True,
        "log_level": "INFO"
    }


@pytest.fixture
def extended_config(base_config):
    """
    Extended config that depends on base_config.
    
    Fixtures can depend on other fixtures!
    """
    config = base_config.copy()
    config["feature_flags"] = {"new_ui": True}
    return config


@pytest.fixture
def app(extended_config, calculator):
    """
    Application that depends on multiple fixtures.
    
    Pytest resolves the entire dependency tree automatically!
    """
    return {
        "config": extended_config,
        "calculator": calculator,
        "name": "TestApp"
    }


def test_app_has_config(app):
    """Test receives fully configured app."""
    assert app["config"]["debug"] is True
    assert "feature_flags" in app["config"]


def test_app_has_calculator(app):
    """App's calculator works."""
    result = app["calculator"].add(5, 5)
    assert result == 10


# ==========================================================
# SECTION 5: Parameterized Fixtures
# ==========================================================

@pytest.fixture(params=["add", "subtract", "multiply"])
def operation(request):
    """
    Fixture that yields multiple values.
    
    Tests using this fixture run once for each parameter!
    """
    return request.param


def test_calculator_has_operation(calculator, operation):
    """
    This test runs 3 times - once for each operation.
    
    Run with: pytest -v to see all three executions
    """
    assert hasattr(calculator, operation)
    assert callable(getattr(calculator, operation))


# ==========================================================
# SECTION 6: Built-in Fixtures
# ==========================================================

def test_with_tmp_path(tmp_path):
    """
    tmp_path is a built-in pytest fixture.
    
    Provides a temporary directory unique to each test.
    """
    test_file = tmp_path / "test.txt"
    test_file.write_text("hello")
    assert test_file.read_text() == "hello"


def test_with_capsys(capsys, calculator):
    """
    capsys captures stdout/stderr.
    
    Useful for testing code that prints output.
    """
    print(f"Result: {calculator.add(2, 3)}")
    
    captured = capsys.readouterr()
    assert "Result: 5" in captured.out


def test_with_monkeypatch(monkeypatch, calculator):
    """
    monkeypatch allows modifying objects during tests.
    
    Great for mocking environment variables, attributes, etc.
    """
    # Monkeypatch a method
    monkeypatch.setattr(calculator, "add", lambda a, b: 999)
    
    assert calculator.add(1, 1) == 999  # Patched!


# ==========================================================
# SECTION 7: autouse Fixtures
# ==========================================================

@pytest.fixture(autouse=True)
def log_test_start(request):
    """
    Runs automatically for every test in this module.
    
    autouse=True means tests don't need to request it.
    """
    print(f"\n>>> Starting test: {request.node.name}")
    yield
    print(f"<<< Finished test: {request.node.name}")


def test_auto_logged_1():
    """This test is automatically logged."""
    assert True


def test_auto_logged_2():
    """This test is also automatically logged."""
    assert True






