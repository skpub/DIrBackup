import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;

public class Directory {
    Path path;
    DirectoryDCP structure;

    Directory(Path path) {
        this.path = path.toAbsolutePath();
        this.structure = new DirectoryDCP(path, true);
    }

    Directory(Path path, DirectoryDCP dirDCP) {
        this.path = path.toAbsolutePath();
        this.structure = dirDCP;
    }

    public Directory sub(Directory b) {
        DirectoryDCP temp_structure = new DirectoryDCP(this.path, false);
        Directory temp = new Directory(this.path, temp_structure);
        return new Directory(
            this.path,
//            sub_(this.path, b.path, this.structure, Optional.ofNullable(b.structure), temp.structure)
            sub__(
                this.path,
                Optional.ofNullable(b.path),
                this.structure,
                Optional.ofNullable(b.structure),
                temp.structure).get()
        );
    }


    public static Optional<DirectoryDCP> sub__(
        Path a_root,
        Optional<Path> b_root_opt,
        DirectoryDCP a_ptr,
        Optional<DirectoryDCP> b_ptr_opt,
        DirectoryDCP parent
    ) {
        a_ptr.dirs.ifPresentOrElse(
            a_dirs -> {
                b_ptr_opt.ifPresentOrElse(
                    b_ptr -> {
                        b_root_opt.ifPresent( b_root ->
                            b_ptr.dirs.ifPresentOrElse(
                                b_dirs -> {
                                    a_dirs.forEach((path, a_dir) -> {
                                        System.out.println("来たよ");
                                        Optional.ofNullable(b_dirs.get(path)).ifPresentOrElse(
                                            b_dir -> {
                                                DirectoryDCP next_parent = new DirectoryDCP(path, false);
                                                Optional<DirectoryDCP> child_opt = sub__(
                                                    a_root.resolve(path),
                                                    Optional.of(b_root.resolve(path)),
                                                    a_dir,
                                                    Optional.of(b_dir),
                                                    next_parent
                                                );
                                                child_opt.ifPresent(parent::addDirectory);
                                            },
                                            () -> {
                                                parent.addDirectory(a_dir);
                                                DirectoryDCP next_parent = new DirectoryDCP(path, false);
//                                                Optional<DirectoryDCP> child_opt = sub__(
//
//                                                );
                                            }
                                        );
                                    });
                                },
                                () -> { // b_ptr.dirs is empty.
                                }
                            )
                        );
                    },
                    () -> { // b_ptr is null.
                    }
                );
            },
            () -> { // a_ptr.dirs is empty.
//                a_ptr.files.ifPresentOrElse(
//                    a_files -> {
//                        a_files.forEach(parent::addFile);
//                    },
//                    () -> {
//                    }
//                );
            }
        );
        System.out.println("parent: " + parent.name);
        a_ptr.files.ifPresent(
            a_files -> {
                b_ptr_opt.ifPresentOrElse(
                    b_ptr ->
                        b_root_opt.ifPresentOrElse(
                            b_root -> {
                                b_ptr.files.ifPresentOrElse(
                                    b_files -> {
                                        a_files.forEach(file -> {
                                            if (b_files.contains(file)) {
                                                Path a_abs = a_root.resolve(file);
                                                Path b_abs = b_root.resolve(file);
                                                FileTime time_a;
                                                FileTime time_b;
                                                try {
                                                    time_a = Files.getLastModifiedTime(a_abs);
                                                    time_b = Files.getLastModifiedTime(b_abs);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                if (time_a.compareTo(time_b) > 0) {
                                                    parent.addFile(file);
                                                }
                                            } else {
                                                parent.addFile(file);
                                            }
                                        });
                                    },
                                    () -> { // b_files is empty.
                                        a_files.forEach(parent::addFile);
                                    }
                                );
                            },
                            () -> {

                            }
                        ),
                    () -> {
                        a_files.forEach(parent::addFile);
                    }
                );
            }
        );
        return Optional.ofNullable(parent);
    }

    public static DirectoryDCP sub_(
        Path a_root,
        Path b_root,
        DirectoryDCP a_ptr,
        Optional<DirectoryDCP> b_ptr_opt,
        DirectoryDCP temp)
    {
        System.out.println("a_root: " + a_root.toString());
        System.out.println("b_root: " + b_root.toString());
        System.out.println("a_ptr: " + a_ptr.name.toString());
        b_ptr_opt.ifPresentOrElse(
            b_ptr -> System.out.println("b_ptr: " + b_ptr.name.toString()),
            () -> System.out.println("b_ptr: NULL")
        );
        a_ptr.dirs.ifPresentOrElse(
            a_dirs -> {
                b_ptr_opt.ifPresentOrElse(
                    b_ptr -> {
                        b_ptr.dirs.ifPresentOrElse(
                            b_dirs -> {
                                a_dirs.forEach((path, a_dir) -> {
                                    Optional.ofNullable(b_dirs.get(path)).ifPresentOrElse(
                                        b_dir -> {
                                            Path a_abs = a_root.resolve(a_dir.name);
                                            Path b_abs = b_root.resolve(b_dir.name);
                                            FileTime time_a;
                                            FileTime time_b;
                                            try {
                                                time_a = Files.getLastModifiedTime(a_abs);
                                                time_b = Files.getLastModifiedTime(b_abs);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if (time_a.compareTo(time_b) > 0) {
                                                temp.addDirectory(b_dir);
                                            }
                                        },
                                        () -> {

                                        }
                                    );

                                });
                            },
                            () -> {
                                a_dirs.forEach((path, dir) -> {
                                    temp.addDirectory(dir);
                                    sub_(
                                        a_root,
                                        b_root,
                                        dir,
                                        Optional.empty(),
                                        dir
                                    );
                                });
                            }
                        );

                    },
                    () -> {

                    }
                );
            },
            () -> {
            }
        );

        System.out.println("END: DIR");

        a_ptr.files.ifPresent(
            a_files -> {
                b_ptr_opt.ifPresentOrElse(
                    b_ptr ->
                        b_ptr.files.ifPresentOrElse(
                            b_files -> {
                                a_files.forEach(file -> {
                                    if (b_files.contains(file)) {
                                        Path a_abs = a_root.resolve(file);
                                        Path b_abs = b_root.resolve(file);
                                        FileTime time_a;
                                        FileTime time_b;
                                        try {
                                            time_a = Files.getLastModifiedTime(a_abs);
                                            time_b = Files.getLastModifiedTime(b_abs);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        if (time_a.compareTo(time_b) > 0) {
                                            temp.addFile(file);
                                        }
                                    } else {
                                        temp.addFile(file);
                                    }
                                });
                                },
                            () -> { // b_files is empty.
                                a_files.forEach(temp::addFile);
                            }
                        ),
                    () -> {
                        a_files.forEach(temp::addFile);
                    }
                );
            }
        );


//        if (a_ptr.dirs.isEmpty()) {
//            return temp;
//        } else {
//            Map<Path, DirectoryDCP> current_dirs = new HashMap<>();
//            a_ptr.dirs.forEach((path, dir) -> { // dir: Path (key)
//                Path a_abs = a_root.resolve(dir.name);
//                Path b_abs = b_root.resolve(dir.name);
//                DirectoryDCP b_dir;
//                if ((b_dir = b_ptr.dirs.get(path)) != null && b_ptr.dirs != null) {
//                    FileTime time_a;
//                    FileTime time_b;
//                    try {
//                        time_a = Files.getLastModifiedTime(a_abs);
//                        time_b = Files.getLastModifiedTime(b_abs);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    if (time_a.compareTo(time_b) > 0) {
//                        current_dirs.put(path, dir);
//                        sub_(
//                            a_abs,
//                            b_abs,
//                            a_ptr.dirs.get(path),
//                            b_ptr.dirs.get(path),
//                            current_dirs.get(path)
//                        );
//                    } else {
//                        return;
//                    }
//                } else {
//                    current_dirs.put(path, dir);
//                    sub_( a_abs,
//                        b_abs,
//                        a_ptr.dirs.get(path),
//                        b_ptr.dirs.get(path),
//                        current_dirs.get(path)
//                    );
//                }






//                if (b_ptr.dirs.get(dir) != null) {
//                    Path a_abs = a_root.resolve(a_ptr.dirs.get(dir).name);
//                    Path b_abs = b_root.resolve(b_ptr.dirs.get(dir).name);
//                    FileTime time_a;
//                    FileTime time_b;
//                    try {
//                        time_a = Files.getLastModifiedTime(a_abs);
//                        time_b = Files.getLastModifiedTime(b_abs);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    if (time_a.compareTo(time_b) > 0) {
//                        current_dirs.put(a_ptr.name, a_ptr.dirs.get(dir));
////                        sub_(
////                            a_abs.resolve(dir),
////                            b_abs.resolve(dir),
////                            a_ptr.dirs.get(dir),
////                            b_ptr.dirs.get(dir),
////
////                        )
//                    }
//                }
//            });
//        }
        return temp;
    }

    public void print() {
        this.structure.print();
    }
}
