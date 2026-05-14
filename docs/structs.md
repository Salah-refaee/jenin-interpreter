# structs
- defintion:
```
struct <alphanum> {
  <alphanum>: ?,
  <alphanum>: ?,
  ...
};
```
- example:
```
struct ID {
  name: "placeholder", # undefined values are coming soon
  idno: 000000000000,
  phone: 0500000000
};

let someone = ID; # feels like `let someone = new ID;`, but it isnt
someone.name = "Daniel";
someone.idno = 0719692691792;
someone.phone = 0567891011;
```
