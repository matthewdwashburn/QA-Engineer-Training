# Java OOP and Collections: Interview Preparation

## Learning Objectives

- List common **technical** themes for Java OOP and collections interviews.
- Apply **whiteboard** and **code walk-through** strategies under time pressure.
- Use the **STAR** method for behavioral questions.

## Why This Matters

This week’s epic maps directly to screening and onsite loops: encapsulation, inheritance vs composition, `equals`/`hashCode`, collection choice, and lambdas. Structured practice converts knowledge into calm, credible answers.

## The Concept

### How to answer technical questions (a simple structure)

For most “Explain X” questions, use this repeatable format:

1. **Definition** (1–2 sentences)
2. **Tiny example** (3–6 lines, or a quick scenario)
3. **Trade-off / when to choose** (performance, correctness, design)
4. **Common pitfall** (what candidates often get wrong)

Example for “List vs Set”:
- Definition: List keeps order/duplicates; Set enforces uniqueness.
- Example: show `add` duplicates in a `Set`.
- Trade-off: membership tests vs ordering.
- Pitfall: assuming `HashSet` order.

### Technical themes (sample)

- **OOP:** four pillars; when to prefer composition; abstract class vs interface; overload vs override; polymorphism and virtual dispatch.
- **Types:** access modifiers; immutability; generics at a high level (wildcards if advanced).
- **Core API:** `Object` methods; `equals`/`hashCode` contract; `String` immutability.
- **Collections:** `List` vs `Set` vs `Map`; `ArrayList` vs `LinkedList`; `HashMap` internals at concept level; iteration and concurrent modification.
- **Modern Java:** functional interfaces; lambdas; try-with-resources; exceptions checked vs unchecked.

Practice **out loud**: define the term, give a **tiny example**, then **trade-off** (“TreeSet sorts but costs log n”).

### “Top 10” fast-check questions (use for self-drill)

- What is the difference between `==` and `equals`?
- State the `equals`/`hashCode` contract and why `HashMap` depends on it.
- When would you use an interface vs abstract class?
- Explain overloading vs overriding and how dispatch works.
- When do you use `ArrayList` vs `LinkedList`?
- When do you use `HashSet` vs `TreeSet`?
- Explain `Map.get()` returning `null`: missing key vs null value.
- Checked vs unchecked exceptions: when to choose each.
- What is a functional interface? Why do lambdas need one?
- What is try-with-resources and why is it preferred?

### Whiteboard strategies

- **Clarify** inputs, outputs, constraints, and empty cases before coding.
- **Start with API** (method signature) and **test cases** verbally.
- Write **readable** names; narrate **complexity** when you finish.
- If stuck, state assumptions and propose a simpler version first.

#### Complexity language (what interviewers want to hear)

- Time: “This is \(O(n)\) because we scan once.”
- Space: “This uses \(O(k)\) extra space for the set of seen ids.”
- Mention trade-offs: “Using a `HashSet` makes membership checks average \(O(1)\) vs a list’s \(O(n)\).”

### Code walk-through

- Walk line-by-line: **state** changes, **loops** termination, **edge** cases.
- For take-home or PR review style: mention **tests**, **logging**, and **failure modes**.

### STAR for behavioral questions

- **Situation** — brief context.
- **Task** — your responsibility.
- **Action** — what **you** did (not only “we”).
- **Result** — outcome with metrics or learning.

Prepare 4–6 stories: conflict, deadline, mistake, leadership, learning a new stack, quality issue caught in test.

#### QA-flavored behavioral angles (easy to reuse)

- A bug you prevented with a boundary/edge case test
- A flaky test you stabilized (root cause + fix)
- A production issue you helped diagnose using logs/metrics
- A time you improved a test suite’s maintainability (refactor, naming, fixtures)

## Code Example

No code—interview skill focus. Revisit earlier week notes and **reproduce** small snippets from memory (`HashMap` put/get, `equals`/`hashCode` skeleton, lambda sort).

## Summary

- Align answers with this week’s topics: OOP, exceptions, collections, lambdas, logging awareness.
- Whiteboard: clarify → signature → examples → implement → complexity.
- STAR keeps behavioral answers concrete and credible.

## Additional Resources

- [Oracle Java Tutorials — full index for review](https://docs.oracle.com/javase/tutorial/)
- [STAR method (overview)](https://www.indeed.com/career-advice/interviewing/how-to-use-the-star-interview-response-technique)
