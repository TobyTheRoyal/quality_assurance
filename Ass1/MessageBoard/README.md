# MessageBoard
## Test coverage
1. Run ``./gradlew test`` to execute the tests
1. Run ``./gradlew jacocoTestReport`` to check test coverage

Then you can find a report in the directory _build/reports/jacoco_, i.e. [here](.build/reports/jacoco/test/html/index.html).

## PIT Mutation Testing
1. Run ``./gradlew pitest`` to execute create mutants and execute the tests

Then you can find a report in the directory _build/reports/pitest_, i.e. [here](./build/reports/pitest/index.html).
