# MooseLang

## [WIP] Functional programming language implemented with call-by-push-value semantics.
This is a personal exploration into creating a functional programming language.
Namely, a language which compiles into a simpler call-by-push-value (CBPV) representation,
which is then evaluated.

CBPV subsumes call-by-name and call-by-value,
allowing for both semantics to be represented by one language.
This yields a fine-grain control of program semantics and execution.

As development of this language continues, more quality of life and higher-level language features will be included.
During the early stages, more focus will be spent solidifying the lower-level CBPV representation.


## Roadmap
### Iteration 1 (Complete)
- Frontend parser combinator.
  - Parsing complex (arithmetic/application) expressions via shunting-yard algorithm.
- AST representation of syntactic components.

### Iteration 2 (Complete)
- Typing of expressions (according to CBPV typing rules).
- Intermediate Representation (IR) implementation of CBPV lambda calculus.
  - let bindings, force, thunk, lambda, push, produce

### Iteration 3 (Current)
- Interpreter to execute CBPV IR directly.
  - Correctly execute canonical CBPV example.
  - Operand stack supporting frames (let-computation bindings)

### Iteration 4 (Future)
- Refine front-end syntax
  - Global definitions
  - Operator precedence and associativity
- Implement REPL with interpreter.

### Iteration 5 (Future)
- Support for product types with pattern matching.
- Support for sum types with pattern matching.
- More robust user data type capabilities
  - Records?
  - Explicit typing?
  - Nested value deconstruction
    - Pattern match and function argument
