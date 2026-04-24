# FProps Documentation

- [FProps Documentation](#fprops-documentation)
  - [Comments](#comments)
  - [Models](#models)
  - [Standard Model](#standard-model)
  - [Variables](#variables)
  - [Operators](#operators)
  - [Symbol Access](#symbol-access)
  - [Formulas](#formulas)
  - [Switching Models](#switching-models)
  - [Bodys](#bodys)
  - [Branches](#branches)
  - [Quantifiers](#quantifiers)


FProps is an implementation of First-Order Logic in a programmatical form. This language allows you
to create models and express logical formulas and proofs in a simple programmatic fashion.

## Comments

Comments are indicated with a hash (`#`) symbol, similar to Python, and are terminated by newlines.

## Models

Models are indicated by being surrounded by brackets. Models are defined with a few different properties. 

First, the symbols, which are defined with the `symbols` keyword. Then, all the symbols defined under the model follow, comma-separated, in a set of curly braces.

Next, operators are defined, with either `bin` or `un`. 

If an operator is defined with `bin`, then it is a binary operator, with the operands being defined in the body as `'left` and `'right`. 

If an operator is defined with `un`, then it is a unary operator, with the operand being defined as `'val`.

An example model can be found below, implementing a tree with nodes **0**, **a**, **b**, and **c**, with **a** being a child of **0**.

```fpr
model [L] {
    symbols { 0, a, b, c }

    bin ord {
        # 0 < a
        if (('left is .0) and ('right is .a)) {
            return [Boolean].True;
        }
        return [Boolean].False;
    }

    un shift {
        # unary operator that shifts all symbols left
        if ('val is .a)
            return .0;
        if ('val is .b)
            return .a;
        if ('val is .c)
            return .b;
        return .0;
    }
}
```

## Standard Model

The default model that operations are interpreted under is the `[Boolean]` model. This includes the `True` and `False` symbols, alongside the `and`, `or`, `implies`, `iff`, and `not` operators. These follow standard propositional logic rules.

## Variables

Variables are indiciated with an apostrophe, to differentiate them from models, symbols, keywords, or operators. Variables can be set using the `let` keyword.

```fpr
let 'x = [Boolean].True;
let 'y = [Boolean].False;
print ('x and (not 'y)); # prints [Boolean].False
```

## Operators

Operators are defined simply with words, no symbols, as to let any custom operator be defined under any model. This means any plain-text other than certain keywords is designated as an operator. Operators are looked up based on the models of the symbols being operated upon, meaning that depending on the interpretation of the symbols under the model, operators' interpretations also may vary.

All models share a common operator, `is`, which simply returns a `[Boolean]` symbol returning whether the two operands are the same symbol.

Symbols of differing models cannot be operated upon.

## Symbol Access

Symbols can be accessed from models by calling the model, and then indexing it with a `.`, such as `[Boolean].True` or `[L].0`. 

However, you can also omit the model name to lookup a symbol from the current model that the expression is being interpreted under. Since the default model is `[Boolean]`, you can call `.True` or `.False` to get the corresponding symbols in the global context. 

If you are writing operators for another model, such as the earlier `[L]` example, you can call the symbols of the operated model similarly, with `.0` or `.a`, as the operator bodys default to the operated upon model as the default.

## Formulas

Formulas can be defined with a keyword and angle brackets to designate free variables. Then, followed by an equal sign, and an expression to return. For example:

```fpr
form bothImply<'x, 'y, 'z> = ('x or 'y) implies 'z;
```

Formulas can be evaluated similarly, by looking them up as a variable, and passing a valuation in angle brackets. The valuation maps directly onto the symbols. If enough symbols are provided for a clear result, a symbol will be returned, otherwise a curried version of the formula will be returned.

```fpr
let 'myImply = 'bothImply<.False>; # curries down to bothImply<.True, 'y, 'z>
print 'myImply<.True, .False>; # y implies z is False
```

## Switching Models

With the `using` keyword, we can switch the default model of the following expression. This means we can change, for example, the model that a formula is interpreted under.

```fpr
# Let L be some model such that [L].a has a parent
model [L] { ... }
# Let U be some model such that [U].a does not have a parent
model [U] { ... }

# The element 'a' has a parent
form tree_proof<> = forall 'x (('x is .a) implies (exists 'y ('y ord 'x)));

print using [L] ( 'tree_proof<> ); # True
print using [U] ( 'tree_proof<> ); # False
```

## Bodys

Bodys can be indicated with curly braces. They create a new context for variables to be defined in. They also can yield a value with the `return` keyword.

```
form complex_proof<'x, 'y, 'z> = {
    let 'both = 'x or 'y;
    return 'both implies 'z;
}
```

## Branches

If/Else statements exist and function the same as other languages.

```fpr
if ('x and 'y) {
    print [Boolean].True;
} else {
    print [Boolean].False;
}
```

## Quantifiers

There are quantifier statements that can be used, and are executed under the current model.
The two quantifiers are `forall` and `exists`. They take a variable name to assign each
element to, and an expression to evaluate for each element.

```fpr
# For every element x in [Boolean], 
# there exists an element y in [Boolean] 
# such that x and y is true
print forall 'x (exists 'y ('x and 'y) ); # False
```
