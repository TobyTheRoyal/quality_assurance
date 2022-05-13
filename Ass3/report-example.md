# Quality Assurance Assignment 3
Group XX

This is just a empty example file to illustrate how a report could look like.
You do not need to use Markdown, use whatever you like, but in the end you must hand in a PDF file, and make sure that the oracle can be copy/pasted from your report to PathCrawler without problems.

## Theory Questions
**1. What is an oracle? Why don't we just use algorithm under test as an oracle?**

Your answer here.

**2. What is the difference between concolic and symbolic execution? Explain it using the example.**

Your answer here.

**3. What are the advantages of concolic execution over random testing?**

Your answer here.

**4. Explain why loops are problematic and how they are dealt with in concolic execution?**

Your answer here.

**5. Is concolic execution white-box or black-box testing and why?**

Your answer here.

**6. Which bugs cannot be caught with concolic execution? _(Bonus)_**

Your answer here.

## PathCrawler Online

### fibonacci()
**Array domains:**  
 * 0 ≤ array[INDEX_0] ≤ 1

**Variable domains:**  
 * 0 ≤ x ≤ 1

**Unquantified preconditions:**  
 * x == y

**Quantified preconditions:**  
 * ∀ A : A ≥ 0 ⇒ array[A] ≥ array[2 * A]

**Oracle:**  
```c
void oracle_fibonacci() {
}
```

**Correct**: Yes, correct.|No, mutant.

**Bug**:  
Description of the bug, and how you found it using PathCrawler.

### lcm()
**Array domains:**  
 * 0 ≤ array[INDEX_0] ≤ 1

**Variable domains:**  
 * 0 ≤ x ≤ 1

**Unquantified preconditions:**  
 * x == y

**Quantified preconditions:**  
 * ∀ A : A ≥ 0 ⇒ array[A] ≥ array[2 * A]

**Oracle:**  
```c
void oracle_lcm() {
}
```

**Correct**: Yes, correct.|No, mutant.

**Bug**:  
Description of the bug, and how you found it using PathCrawler.

### intersection()
**Array domains:**  
 * 0 ≤ array[INDEX_0] ≤ 1

**Variable domains:**  
 * 0 ≤ x ≤ 1

**Unquantified preconditions:**  
 * x == y

**Quantified preconditions:**  
 * ∀ A : A ≥ 0 ⇒ array[A] ≥ array[2 * A]

**Oracle:**  
```c
void oracle_intersection() {
}
```

**Correct**: Yes, correct.|No, mutant.

**Bug**:  
Description of the bug, and how you found it using PathCrawler.

### sort()
**Array domains:**  
 * 0 ≤ array[INDEX_0] ≤ 1

**Variable domains:**  
 * 0 ≤ x ≤ 1

**Unquantified preconditions:**  
 * x == y

**Quantified preconditions:**  
 * ∀ A : A ≥ 0 ⇒ array[A] ≥ array[2 * A]

**Oracle:**  
```c
void oracle_sort() {
}
```

**Correct**: Yes, correct.|No, mutant.

**Bug**:  
Description of the bug, and how you found it using PathCrawler.

### contains() (BONUS)
**Array domains:**  
 * 0 ≤ array[INDEX_0] ≤ 1

**Variable domains:**  
 * 0 ≤ x ≤ 1

**Unquantified preconditions:**  
 * x == y

**Quantified preconditions:**  
 * ∀ A : A ≥ 0 ⇒ array[A] ≥ array[2 * A]

**Oracle:**  
```c
void oracle_contains() {
}
```

**Correct**: Yes, correct.|No, mutant.

**Bug**:  
Description of the bug, and how you found it using PathCrawler.
