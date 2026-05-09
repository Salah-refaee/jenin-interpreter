# functions - defintion
- Syntax
``` 
fn <alphanum>(arg1, arg2, ...) { codeblock }
```
- Example
```
fn factorial(n) {
  switch {
    case n < 1: {
      return 1;
    }
    default: {
      return (n * factorial(n:(n - 1)));
    }
  }
}
```
# functions - call
- Syntax
```
<alphanum>(key:value, key:value, ...);
```
- Example
```
factorial(n:5);
```
