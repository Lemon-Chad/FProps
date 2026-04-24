# FProp

[DOCUMENTATION](DOCS.md)

## Basic Functionality
```
let 'x = True;
let 'y = False;

form psi<'x, 'y, 'z> = ('x and 'y) or (not 'z);

print psi<'x, 'y, False>;
```

## Models
```
model [L] {
    symbols { 0, a, b, c }
    
    bin ord {
        # 0 < a
        if (('left is [L].0) and ('right is [L].a)) {
            return True;
        }
        return False;
    }
    
    un shift {
        # unary operator that shifts all symbols left
        if ('val is a)
            return 0;
        if ('val is b)
            return a;
        if ('val is c)
            return b;
        return 0;
    }
}

# The element 'a' has a parent
form tree_proof<> = forall 'x (('x is [L].a) implies (exists 'y ('y ord 'x)));

print using [L] ( tree_proof<> )
```
