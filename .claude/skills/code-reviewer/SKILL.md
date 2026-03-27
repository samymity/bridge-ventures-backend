---
name: code-reviewer
description: Review code for bugs, performance issues, and best practices. Use when the user asks to review, audit, or check their code quality.
---

Review the provided code or file and give feedback on:

1. **Bugs** — logic errors, null pointer risks, off-by-one errors
2. **Performance** — unnecessary loops, redundant calls, inefficient data structures
3. **Best practices** — naming, code duplication, separation of concerns

## Instructions

- Read the file(s) the user specifies (or ask which file to review if not specified)
- Group feedback by severity: **Critical**, **Warning**, **Suggestion**
- For each issue, include the line number, what the problem is, and a fix example
- Keep feedback concise — one issue per bullet point
- End with a brief overall summary (2-3 sentences max)

## Example output format

**Critical**
- `line 42`: Null check missing before accessing `user.profile` — add `if (user != null)` guard

**Warning**
- `line 78`: `getUserList()` called inside loop — move outside to avoid repeated DB hits

**Suggestion**
- `line 15`: Rename `x` to `itemCount` for clarity

**Summary:** The core logic is sound but has one critical null-safety gap and a performance issue in the loop. Addressing those two will make this production-ready.
