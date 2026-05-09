# loops
- Syntax
```
loop (condition) {
  codeblock
}
```
- Example
```
let i = 0;
loop (i < 10) {
  i = i + 1;
  println(value:i);
}
```
- Break/Continue
```
let i = 0;
let stopAt = 15;
let skippedNumber = 8;
loop (i < 20) {
  i = i + 1;
  switch {
    case (i == stopAt): {
      println(value:i);
      break;
    }
    case (i == skippedNumber): {
      continue; # dont print
    }
    default: {
      println(value:i);
    }
  }
}
```