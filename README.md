# DIrCopy
## Differential and Incremental Copy Library for Java.

### diff
```java
DirectoryDiff diff = new DirectoryDiff(
    Paths.get("Path/to/A"),
    Paths.get("Path/to/B")
);
diff.print();
```
```
A
    deleted: deleted.bmp
    created: A_1.txt
    A_A
        deleted: deleted.txt
        deleted: deleted.bmp
        created: created.txt
        A_A_A
            created: test.bmp
    A_B
        deleted: deleted.bmp
    deleted: B_A
```

### incr
```
NOT YET IMPLEMENTED.
```

### copy
```
IN PROGRESS...
```