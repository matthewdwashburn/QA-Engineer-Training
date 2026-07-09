# Day 26 - Mockito: Mocking Dependencies

---

Isolating a `UserService` (SUT) from its `UserRepository` + `EmailClient` dependencies so unit tests don't touch a real DB or email server. Project uses **JUnit 6.1.1 + Mockito 5.23.0** (`mockito-core`, `mockito-junit-jupiter`).

## Setup — creating mocks

```java
@ExtendWith(MockitoExtension.class)   // enables Mockito annotations in JUnit
class MyTest {
    @Mock private UserRepository repository;   // a fake dependency
    @Mock private EmailClient emailClient;
    @InjectMocks private UserService userService; // real SUT, mocks injected in
}
```

| Tool | What it does |
|---|---|
| `@ExtendWith(MockitoExtension.class)` | Wires Mockito into the JUnit lifecycle |
| `@Mock` | Creates a mock object |
| `@InjectMocks` | Creates the **real** SUT and injects the `@Mock` fields into it |
| `Mockito.mock(Class)` | Create a mock programmatically (alternative to `@Mock`) |
| `@Captor` | Declares an `ArgumentCaptor` field |

**Default mock behavior:** unstubbed methods return `null` / `0` / `false` / empty `Optional`.

## Stubbing — controlling return values

| Pattern | Use |
|---|---|
| `when(mock.method(arg)).thenReturn(value)` | Return a value for matching args |
| `when(...).thenThrow(new Ex(...))` | Simulate an exception |
| `when(...).thenAnswer(inv -> ...)` | Dynamic response; `inv.getArgument(0)` reads the input |
| `.thenReturn(a).thenReturn(b)` | Consecutive calls return different values (retry scenarios) |
| `doNothing().when(mock).voidMethod()` | Stub a **void** method as a no-op |
| `doThrow(ex).when(mock).voidMethod()` | Void method throws |
| `doAnswer(inv -> ...).when(mock).voidMethod()` | Custom behavior for a void method |

Void methods (and spies) **must** use the `doX().when(mock)` form — you can't pass a void call to `when()`.

## Argument matchers

```java
when(repository.findById(anyLong())).thenReturn(...);
when(repository.existsByEmail(eq("admin@test.com"))).thenReturn(true);
when(repository.save(argThat(u -> u.getName().startsWith("VIP_")))).thenReturn(...);
```
- `anyLong()`, `anyString()`, `any(User.class)`, `isNull()` — match any/typed value.
- `eq(x)` — exact match; required when mixing literals with other matchers.
- `argThat(predicate)` — custom matcher.

## Verification — confirming interactions

```java
verify(repository).findById(1L);                 // called exactly once (default)
verify(repository, times(3)).findById(anyLong());// exact count
verify(repository, never()).save(any());         // never called
verify(repository, atLeast(2)).find...();        // atLeast / atMost / atLeastOnce
verifyNoMoreInteractions(repository);            // strict: no other calls
verifyNoInteractions(emailClient);              // nothing touched at all
verify(repository, timeout(100)).find...();      // wait up to 100ms (async)
```

### ArgumentCaptor — inspect what was passed

```java
@Captor ArgumentCaptor<User> userCaptor;
verify(repository).save(userCaptor.capture());
User saved = userCaptor.getValue();          // single value
List<String> all = stringCaptor.getAllValues(); // all calls
// inline form: ArgumentCaptor.forClass(String.class)
```

### InOrder — verify call sequence

```java
InOrder inOrder = inOrder(repository, emailClient);
inOrder.verify(repository).existsByEmail(anyString()); // 1st
inOrder.verify(repository).save(any());                // 2nd
inOrder.verify(emailClient).send(...);                 // 3rd
```

## Mock vs Spy

| | **Mock** | **Spy** |
|---|---|---|
| Object | Completely fake | Wraps a **real** object |
| Unstubbed methods | Return defaults | Run the **real** method |
| Created with | `@Mock` / `mock()` | `@Spy` / `spy(realObj)` |
| Use for | Full isolation, testing interactions | Partial mocking, legacy code needing some real behavior |

```java
List<String> spy = spy(new ArrayList<>());
spy.add("real");                 // actually adds
doReturn("stubbed").when(spy).get(0);  // stub ONE method
```
> **Spy gotcha:** stub spies with `doReturn().when(spy)`, **not** `when(spy.get(0))` — the latter calls the real method during stubbing (can throw). Overusing spies often signals a design problem.

## Real-world pattern

Full service tests combine everything: stub the collaborators, act, then assert both the **result** and the **interactions** (AAA — Arrange, Act, Assert). Covers happy path, error scenarios (validation, duplicates → `verifyNoInteractions`), and edge cases (email fails but user still created; graceful degradation when a dependency is null).
