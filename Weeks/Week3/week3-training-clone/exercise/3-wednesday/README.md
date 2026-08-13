# Week 3 — Wednesday exercises

**Epic tie-in:** Exceptions, custom types, `List` implementations (`written/3-wednesday/`, `demos/3-wednesday/`).

**Time:** ~2.5–3.5 hours.

---

## Lab 1 — Banking & custom exceptions (`exercise_exceptions`)

**Mode:** Implementation.

Implement a tiny in-memory bank in `starter_code/banking/`:

### Types

1. **`InsufficientFundsException`** — **checked**, extends `Exception`. Include **shortfall** amount (double or `BigDecimal`—pick one and stay consistent).
2. **`InvalidAccountException`** — **checked**, for unknown account id.
3. **`Account`** — `id` (String), `balance`. Methods:
   - `void deposit(double amount)` — reject negative amounts with **`IllegalArgumentException`** (unchecked).
   - `void withdraw(double amount) throws InsufficientFundsException` — reject negative; throw if overdraft.
4. **`Bank`** — `Map<String, Account>` internally (or `HashMap`). Methods:
   - `void openAccount(String id, double initialDeposit)` throws **`InvalidAccountException`** if id exists or id blank.
   - `Account getAccount(String id)` throws **`InvalidAccountException`** if missing.
   - `void transfer(String fromId, String toId, double amount)` — propagates or wraps failures appropriately.

### `BankingDemo.main`

Demonstrate **try/catch** for checked exceptions and at least one **unchecked** path.

### Definition of done

- No silent catches; print or log meaningful messages.
- `javac banking/*.java && java banking.BankingDemo` from `starter_code`.

---

## Lab 2 — To-do list manager (`exercise_lists`)

**Mode:** Implementation + short analysis paragraph.

Complete `starter_code/todo/TodoListManager.java`:

1. Backing store **`ArrayList<String>`** for tasks (ordered).
2. Methods: `addTask`, `getTask(int index)`, `completeTask(int index)` (remove by index), `listTasks()`, `size()`.
3. Validate indices; throw **`IndexOutOfBoundsException`** or custom unchecked exception with clear message.

Write **`ListComparison.md`** (same folder as README or in `starter_code/todo/`) answering in **4–6 sentences**:

- For **this** API, when might **`LinkedList`** be preferable?
- When is **`ArrayList`** the better default?

### Definition of done

- `TodoDemo.main` runs a short scripted scenario (add → list → complete → list).

### References

- `written/3-wednesday/handling-exceptions.md`, `creating-custom-exceptions.md`, `list-interface.md`, `arraylist-and-linkedlist.md`
