# Exercise: Text Analysis with `String`

**Mode:** Implementation (Code Lab)  
**Duration:** 60–90 minutes  
**Day:** 5-friday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Use **`String`** methods (**no** external libraries) to analyze text from a **file** or **literal**.
- Implement: **word count** (whitespace-separated tokens), **palindrome check** for a single token (ignore case, non-alphanumeric stripped optional stretch), and **count** of a given **substring** (overlapping optional stretch).
- Practice **`equals`**, **`substring`**, **`charAt`**, immutability.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| String basics | `written/5-friday/string-basics.md` |
| String pool / equals | `written/5-friday/string-pool.md` |
| Demo | `demos/5-friday/code/DemoStrings.java` |

---

## Scenario

You are validating **release notes** before publication. Implement **`TextAnalyzer`** in `starter_code/TextAnalyzer.java`:

1. **`wordCount(String text)`** — split on whitespace (`String.split("\\s+")` allowed); empty input → `0`.
2. **`isPalindrome(String token)`** — after **trim** and **toLowerCase**, read same forwards/backwards (ignore spaces inside for stretch only).
3. **`countOccurrences(String haystack, String needle)`** — non-overlapping matches (standard `String` scan is fine).

**`main`**: read **`starter_data/sample.txt`**, print all three stats for the file contents and for one **hard-coded** palindrome test.

---

## Definition of Done

- [ ] `javac TextAnalyzer.java && java TextAnalyzer` runs and prints sensible numbers for `sample.txt`.
- [ ] Uses **only** `String` / `char` / loops (or `split`) — no Apache Commons etc.
- [ ] Javadoc on the three public static methods.

---

## Stretch

- Strip punctuation before palindrome check.
- Overlapping occurrence count (e.g. `"aaaa"` count `"aa"` → 3).

---

## References

- Written: `content/Week2-Python-Java/written/5-friday/string-basics.md`
