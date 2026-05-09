# conditions
- Syntax
```
switch {
  case <expr>: codeblock
  case <expr>: codeblock
  ...
  default: codeblock
}
```
- Example
```
let name = input();
switch {
  case name == "jenin": {
    println(value:"ME!");
  }
  case name == "salah": {
    println(value:"MY CREATOR!");
  }
  default: {
    println(value:name);
  }
}
```
