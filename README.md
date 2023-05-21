# DIrCopy
## Differential and Incremental Copy Library for Java.

### diff
```java
DirectoryDiff diff = new DirectoryDiff(
    Paths.get("test\\A"),
    Paths.get("test\\B")
);
diff.print();

List<Path> deleted = diff.deletedEntries();
System.out.println("====# DELETED #====");
deleted.forEach(System.out::println);
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
====# DELETED #====
deleted.bmp
A_A\deleted.txt
A_A\deleted.bmp
A_B\deleted.bmp
B_A
```

### incr
```
NOT YET IMPLEMENTED.
```

### copy
```
IN PROGRESS...
```