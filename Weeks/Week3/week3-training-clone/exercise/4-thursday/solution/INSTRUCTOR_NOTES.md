# Instructor notes — Thursday pair lab

- **Text blocks** in `WordFrequencyApp` require **Java 15+**; for older JDKs, replace with string concatenation.
- **Top N:** Accept sort of `ArrayList<Map.Entry<...>>` with `Comparator.comparingInt(Map.Entry::getValue).reversed()`.
- **Logback:** If pairs struggle with JARs, allow a **single** Maven parent POM you provide; grading focuses on concepts.
- **Task compareTo:** Watch for **tie-break** when priorities equal (compare description) to avoid `compareTo` contract surprises in trees/queues.
