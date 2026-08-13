```
@ParameterizedTest(name = "{0} + {1} = {2}") @ValueSource(ints = {1, 2, 3, 4, 5})
@ParameterizedTest @CsvSource({"1,2,3", "4, 5, 9", "3, 6, 9"})
@ParameterizedTest @MethodSource({"provideDivisionTestCases"})
static Stream<Arguments> provideDivisionTestCases() {
	return Stream.of(
		Arguments.of(10, 2, 5),
		Arguments.of(12, 6, 2),
		Arguments.of(15, 3, 5),
		Arguments.of(12, 3, 4)
	)
}
```

Verify validates that something was called during this test

@Mock

@InjectMock - Dependency injects the mocks that are necessary for

When then answer allows you to access runtime arguments, modify parameters based on method invocation.