# Bug fixes 

## Bug 1
- **Symptom:** Calling `buildLabel(null)` crashed the program with a `NullPointerException` thrown from inside `user.trim()`, instead of failing in a controlled, descriptive way (or being handled like the empty-string case).
- **Root cause:** The method called `user.trim()` before checking whether `user` was `null`, so a `null` argument caused an unguarded NPE.
- **Fix:** Added a null check at the top of `buildLabel` (`if (user == null) { throw new NullPointerException("User cannot be null!"); }`) so a `null` input fails immediately with a clear message instead of an unexplained NPE later in the method.

## Bug 2
- **Symptom:** `allowAccess(5, 3)` returned `false` even though the caller's role level (5) was greater than or equal to the required level (3), incorrectly denying access.
- **Root cause:** The comparison operator was inverted, so the method effectively granted access only when `roleLevel` was less than `required`.
- **Fix:** Changed the comparison to `roleLevel >= required`, so access is granted whenever the role level meets or exceeds the required level.

## Bug 3
- **Symptom:** `average(scores)` returned a truncated integer-like value (e.g., `18` or `18.0`) instead of the expected ~`18.33`.
- **Root cause:** The sum was accumulated and/or divided using integer arithmetic (`int`), so `sum / values.length` performed integer division and discarded the fractional part.
- **Fix:** Changed `sum` to a floating-point type (`float`) before dividing by `values.length`, producing a floating-point result close to `18.333...`.

## Bug 4
- **Symptom:** `findFirst(scores, 20)` returned `-1` even though `20` exists at index `1` in the array.
- **Root cause:** The loop located the matching index but never returned it from inside the loop, so execution fell through to the final `return -1;`.
- **Fix:** Added `return found;` inside the `if (arr[i] == target)` block so the matching index is returned as soon as it is found.

## Bug 5
- **Symptom:** `countWords(cases)` either threw an `ArrayIndexOutOfBoundsException` or produced an incorrect count.
- **Root cause:** The loop condition used `i <= words.length`, allowing `i` to equal `words.length` and access an index one past the end of the array.
- **Fix:** Changed the loop condition to `i < words.length` so the loop only accesses valid array indices.
