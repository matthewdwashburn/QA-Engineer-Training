# Instructor notes — Tuesday

- **Vehicle:** Ensure abstract method name matches across all subclasses; watch package declaration when compiling (`javac vehicle/*.java` from `starter_code`).
- **Money:** `Objects.hash(currency, amountMinor)` and `Objects.equals` on fields is the expected solution; stress **no mutable fields** in equals/hashCode contract.
- **GasCar + AutonomousCapable:** Only one implementor is enough to satisfy the `instanceof` exercise.
