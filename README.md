# DIrBackup
## Differential and Incremental Backup Library for Java.

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

### Differential Backup
```
Path target = Paths.get("test\\A");
Path backup = Paths.get("test\\B");
DBackup bkup = new DBackup(target, backup);
bkup.dBackup();
```

```
A
│   new_file.bmp
│
├───A_A
│   │   created.txt
│   │
│   └───A_A_A
│           test.bmp
│
└───new_directory
B
├───2023_05_23_02_51_41
│   │   deleted
│   │
│   └───A
│       │   A_1.txt
│       │
│       ├───A_A
│       │   │   created.txt
│       │   │
│       │   └───A_A_A
│       │           test.bmp
│       │
│       ├───A_B
│       └───B_A
├───2023_05_23_02_52_46
│   │   deleted
│   │
│   └───A
│       │   new_file.bmp
│       │
│       ├───A_A
│       │   │   created.txt
│       │   │
│       │   └───A_A_A
│       │           test.bmp
│       │
│       ├───A_B
│       ├───B_A
│       └───new_directory
└───full
    │   deleted.bmp
    │
    ├───A_A
    │       deleted.bmp
    │       deleted.txt
    │
    ├───A_B
    │       deleted.bmp
    │
    └───B_A
            deleted.txt
```
