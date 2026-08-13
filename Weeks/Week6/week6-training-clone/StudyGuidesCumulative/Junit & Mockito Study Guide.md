# Junit & Mockito Study Guide

## Unit Testing Overview

### Unit Testing Introduction
**Unit Testing** is a process in software development where individual components of a program are tested in isolation to ensure they function correctly. Testers focus on the smallest units of code, like individual methods, to identify and fix defects. Ideally, this process would start early in the development cycle

### Test Driven Development
A use for **Unit Tests** is the development strategy called **Test Driven Development**. Using this strategy, the tests for the software being created are made first, and then work on the implementation begins. The benefit of this approach is the guidance the initially created tests provide the development team. Through the tests, the developers have a clear goal for their initial work: make their code pass the test cases. This design flow has three primary steps:
- create tests
- write code to pass tests
- refactor

**Test Driven Development** helps keep the testing and development team on the same page: the testers make sure the core requirements of the application are covered by test cases, and the development team writes the code to accommodate those requirements

### Regression Testing
Another use for **Unit Tests** is  **Regression Testing**. As development of an application continues new features will interact with old features, and sometime they will require the old features be augmented. With a robust test suite it is a simple thing to run the tests to quickly check the new code has not broken old functionality, thus validating all requirements are still being met. If previously passing tests fail it means some new functionality has introduced a defect into the system and needs to be addressed

### Retesting
Similar to **Regression Testing**, **Retesting** is easy to accomplish with **Unit Tests**. Where **Regression testing** is the process of running tests to check new code has not broken old code, **Retesting** is the process of re-running previously failed tests to check if the changes made fixed the previously discovered defects. The key difference between **Regression Testing** and **Retesting** is context: 
- **Regression Testing** is re-running tests after new code is added to check it does not break previously working code
- **Retesting** is re-running tests after fixes are made for previously discovered defects to validate those defects have been fixed

Distinguishing the context between the two types of tests helps testers determine how to approach any defects revealed by the tests. Defects discovered by **Regression Testing** are likely to be novel, and will usually require some investigating to determine their severity and priority. **Retesting**, on the other hand, will usually detect previously discovered defects, which should have already been categorized

Keep in mind **Unit Tests** are not the only types of testing that can be done for **Regression Testing** and **Retesting**, but they provide a higher level of precision in defect detection when they are included than the other levels of testing

### Typical Defects
Because **Unit Tests** are typically executed directly on the code in question, they are particularly effective at uncovering defects that stem from common developer errors. These errors can include issues such as flow control failures, where the sequence of operations does not proceed as intended, and improper logic, which can lead to incorrect outcomes. Additionally, **Unit Tests** can reveal the use of wrong variables, which may cause unexpected behavior, and malformed environment variables, which can disrupt the application's configuration. Furthermore, properties that are incorrectly configured can also be identified through unit testing

## Junit

### JUnit 4 Overview
**JUnit** is a widely-used testing framework for enterprise applications, offering support for creating tests at all levels for your back-end systems. It leverages annotations to manage testing processes seamlessly, enhancing efficiency and readability. JUnit also integrates well with Maven, facilitating smooth build and test management. Additionally, it works harmoniously with other specialized testing frameworks, such as **Cucumber** for **Behavior Driven Development**, **REST Assured** for testing RESTful web services, and **Mockito** for mocking in unit tests

### Validating Functionality
In its simplest form, JUnit considers a method annotated with `@Test` to be a test case. 
```java
@Test
public void myTest(){
    // testable code goes here
}
```
By default, a JUnit test is considered to pass if the test method does not throw an unhandled exception, so the example test above, that does nothing, would be reported as a passing test by JUnit. Test methods should wrap the actual method that is being tested, where the test object can be called under a variety of situations, depending on the type of testing being done
```java
public static String greeting(String name){
    return "Hello " + name;
}
```
```java
@Test
public void myTest(){
    greeting("Horus"); // no exception will be thrown, so the test is considered to pass
}
```
No exception being thrown does not in itself validate a method is working as intended: typically the returned value of a method will be checked to validate if a method is working correctly. **JUnit** provides a class called **Assert** that can be used to validate a returned value
```java
@Test
public void myTest(){
    String result = greeting("Horus");
    // the expected value is the first argument, the actual value the second
    Assert.assertEqual("Hello Horus", result); // if these values are not equal an AssertionError exception will be thrown, failing the test
}
```
When creating tests it is usually best to validate a single result, but for more complex applications/methods that is not always feasible. Still, it is a good goal to try and focus the actual content being tested in test methods

### Testing Exceptions
Sometimes for a method to work correctly it needs to throw an unhandled exception: **JUnit** can handle these kinds of tests. A try/catch block can be used to validate the content of the exception; if used in this way, the **Assert** method **fail()** should be included at the end of the try block to ensure an **AssertionError** exception is thrown if the desired exception is not thrown
```java
@Test
public void exceptionTesting(){
    try{
        new ArrayList<Integer>().add(1,1);
        fail("this should not happen");
    } catch(IndexOutOfBoundsException e){
        assertEquals("Index: 1, Size: 0", e.getMessage());
    }
}
```
a less cumbersome way of testing exceptions is to make use of the **assertThrows** method provided by the **Assert** class. This method takes two arguments, the expected exception, and a lambda that is expected to trigger the exception. Assuming the expected exception is thrown, it is returned, can be saved in a variable, and further validation can be performed
```java
@Test
public void exceptionTestingWithLambda(){
    IndexOutOfBoundsException e = assertThrows(IndexOutOfBoundsException.class, ()->{
        new ArrayList<Integer>().add(1,1);
    });
    assertEquals("Index: 1, Size: 0", e.getMessage());
}
```

### Setup & Tear Down Methods
Resources being tested and the data they use need to be initialized before any tests can run and provide meaningful results. **JUnit** has a variety of options for performing setup and tear down operations to facilitate test execution:
- **@BeforeClass**
- **@Before**
- **@After**
- **@AfterClass**

**@BeforeClass** and **@AfterClass** can be used to tell **Junit** what methods you want to execute a single time before/after any tests are executed. These methods are expected to perform complex, one time operations to prepare the test environment for the methods that will be executed, but it can also be used to set up static resources that are shared between tests. That being so, when possible it is recommended to keep tests as independent of each other as possible
```java
// Note the method is static
@BeforeClass
public static void oneTimeSetup(){
    // one time setup goes here
}

@AfterClass
public static void oneTimeTearDown(){
    // one time tear down goes here
}
```
For repeat setup and/or tear down actions the **@Before** and **@After** annotations can be used. The methods with these annotations will run before each test case is executed, and are useful for setting up test objects and test data
```java
@Before
public void setup(){
    // setup actions go in here
}

@After
public void tearDown(){
    // tear down actions go in here
}
```
When using these setup and tear down options, keep in mind that classes will inherit any of these methods from parent classes. Unless they are overridden, the parent methods will be executed first by **JUnit**, and then those defined in the child class will be executed

### Ignoring Tests
There are a multitude of reasons a testing team might need for test cases to exist but not be executed alongside the rest of a the test collection: refactoring is not finished, tests are broken, code refactoring has made the test cases obsolete, etc. Individual test methods can be ignored by adding **@Ignore** as an annotation to the test method. **@Ignore** also allows for providing a message in the form of a String explaining why the test is being ignored. If all test methods in a class need to be ignored the class can be annotated instead, and all test methods contained within will be ignored

### Parameterized Tests
When a test method needs to be executed multiple times but with different test data the **Parameterized** class can be used to facilitate the process
```java
@RunWith(Parameterized.class)
public class TestClass{

}
```
Running the test class with the **Parameterized** class allows for the use of the **@Parameter** and **@Parameters** annotations. The former annotation tells **JUnit** what fields it should initialize with test data, the latter tells **JUnit** how to acquire the test data
```java
@RunWith(Parameterized.class)
public class TestClass {

    /*
        this method tells Junit what data sets to use when running any tests in this class. In this case each test in this class will be
        executed twice, first time using the first collection of data, second time using the second set of data
    */
    @Parameters
    public static Collection<Object[]> inputs(){
        return Arrays.asList(
                new Object[][]{
                    {10,true},
                    {9,false}
                }
        );
    }

    @Parameter // defaults to the first value in a collection
    public int number;
    @Parameter(1) // this tells Junit to set the value of this field to the 1st index position value
    public boolean isEven;

    @Test // because there are two data sets this test will be run twice, first with the even number, second time with the odd number
    public void isEvenPositiveAndNegative(){
        Assert.assertEquals(isEven, number%2==0);
    }
}
```
This setup is particularly useful for data-driven testing: the test data can be stored in a remote location, such as a CSV, and **JUnit** at run time can load the data to determine how many times to run the test case. Note that a **Parameterized** test class will execute each test method within the class as many times as data collections are provided

### Test Suite Classes
Test classes can be organized by **Suite** classes to facilitate executing similar test cases in groups
```java
@RunWith(Suite.class)
public class TestSuite {

}
```
These classes are used as containers for **JUnit**, logically organizing related test classes together. The **@SuiteClasses** annotation is used to tell **JUnit** what test classes are associated with the suite
```java
@RunWith(Suite.class)
// Note the test classes are stored in an array
@Suite.SuiteClasses({
        TestSuiteOne.class,
        TestSuiteTwo.class
})
public class TestSuite {
    /*
        SuiteClasses above stores the test classes you want associated with this suite. When this class is referenced for testing, whether by
        Junit through your IDE or by Maven during a testing phase, the classes in the SuiteClasses annotation will have their tests executed
    */
}
```
Organizing and targeting tests through the use of **Suite** classes can help keep testing abd build scripts leaner and easier to read

## Mockito

### Mockito Overview
There will be times when testing applications that the structure of the code will not allow for easy unit testing. Consider a web application that has an API, service layer, and repository layer. Each layer of the application will in some way require data that has been operated on in the other layers. This is not conducive for unit testing (but it is great for integration testing). In situations where unit testing needs to be performed on these integrated resources **Mock** objects and **Stub** methods can be utilized
- **Mock**: an object with the resources to call **Stubs** and verify interactions with the object being mocked
- **Stub**: code (usually methods) that return predefined results

The testing framework **Mockito** provides the capability for creating **Mock** objects and **Stub** capabilities for those mock objects
### Mocking Classes
**Mock** objects created by **Mockito**  provide testers with fine-tuned control over how the object will behave during testing. This allows for things like pre-determine return values, checking
the flow of code execution, and even changing the expected results of the methods in the mock object. To create a **mock** object **Mockito's** **mock** method is used to initialize the object, then it can be provided to the resource that needs it
```java
private Dependency dependency;
private Testable testable;

@Before
public void setup(){
    dependency = Mockito.mock(Dependency.class);
    testable = new Testable(dependency);
}
```
**Mockito** also supports using annotations to initialize resources, both **mocks** and the actual testable. **Mockito** will first attempt **constructor injection**, then **property setter injection**, and finally **field injection** if the other options do not work
```java
@Mock
private Dependency dependency;
@InjectMocks
private Testable testable;

@Before
public void setup(){
    MockitoAnnotations.openMocks(this);
}
```
Note that **Mockito** also provides a way to partially **mock** objects by creating a **spy**: these objects support path of execution and stubbing operations, but a **spy** can also call its real methods

### Stubbing Methods
**Mockito** follows a when/then pattern for creating **stubs** from **mock** objects
```java
Mockito.when(mockObject.method(args)).thenReturn(value);
```
The **when** method takes in a **mock** object and the method/arguments that need to be stubbed. Note that the arguments provided to the method are tracked: if different arguments are provided then you will not get your expected return value
```java
Mockito.when(mockObject.method(args)).thenReturn(something);
mockObject.method(other) // this will not return "something", it will return the default value for the return type of the method
```
**thenReturn** tells Mockito what value should be returned when the **stub** is called. The actual method being called is never called: Mockito instead returns whatever value you determine without the actual method needing to even be implemented. It is good practice to return actually expected values from **stubs** in order to best simulate the expected behavior of the application
```java
Mockito.when(greeter.greet("Ted")).thenReturn("Hello Ted"); // good use of a stub

Mockito.when(greeter.greet("Ted")).thenReturn("Hello world"); // poor use of a stub
```
Once **stubbing** has been done for all methods that will be called by a dependency the actual method being tested can be called in the test method, and any results of the test can be attributed to the test object itself, not its dependencies

### Stubbing Exceptions
Very similar to **stubbing** return values, exceptions can be stubbed by using **thenThrow** instead of **thenReturn**
```java
Mockito.when(greeter.greet("Horus")).thenThrow(new RuntimeException("No greeting for traitors!"));
```

### Verifying Method Calls
Validating a method works correctly isn't just about checking the end result: the path to the result can sometimes be just as important to verify. For instance, just because a user provides valid input doesn't mean the data should not be validated: it is unreasonable to assume users will always provide data that follows all requirements for an application. To check that methods are performing the proper checks **Mockito** provides a **verify** method that can check the path of execution for a method
```java
    private Main main;

    @Before
    public void setup(){
        main = Mockito.mock(Main.class);
    }

    @Test
    public void checkMultipleExecutions(){
        Mockito.when(main.addContent(5,5)).thenReturn(10);
        main.addContent(5,5);
        main.addContent(5,5);
        // this checks the main object called "addContent" with 5 and 5 as the arguments two times
        Mockito.verify(main, Mockito.times(2)).addContent(5,5);
        // this checks the main object called "addContent" with 5 and 5 as the arguments at least one time
        Mockito.verify(main, Mockito.atLeast(1)).addContent(5,5);
        // this checks the main object called "addContent" with 5 and 5 as the arguments no more than two times
        Mockito.verify(main, Mockito.atMost(2)).addContent(5,5);
    }
```