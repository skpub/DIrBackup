import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Directory {
    Path path;
    DirectoryDCP structure;

    Directory(Path path) {
        this.path = path.toAbsolutePath();
        this.structure = new DirectoryDCP(path);
    }

    Directory(Path path, DirectoryDCP dirDCP) {
        this.path = path.toAbsolutePath();
        this.structure = dirDCP;
    }

    public Directory sub(Directory b) {
        DirectoryDCP temp_str = new DirectoryDCP(this.path.toString());
        Directory temp = new Directory(this.path, temp_str);
        return new Directory(
            this.path,
            sub_(this.path, b.path, this.structure, b.structure, temp.structure)
        );
    }

    public static DirectoryDCP sub_(
        Path a_root,
        Path b_root,
        DirectoryDCP a_ptr,
        DirectoryDCP b_ptr,
        DirectoryDCP temp)
    {
        System.out.println("a_root: " + a_root.toString());
        System.out.println("b_root: " + b_root.toString());
        System.out.println("a_ptr: " + a_ptr.name.toString());
        System.out.println("b_ptr: " + b_ptr.name.toString());

        temp.files = new HashSet<>();
        a_ptr.files.forEach(file -> {
            if (b_ptr.files.contains(file)) {
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
                    temp.files.add(file);
                }
            } else {
                temp.files.add(file);
            }
        });
        if (a_ptr.dirs.isEmpty()) {
            return temp;
        } else {
            Map<Path, DirectoryDCP> current_dirs = new HashMap<>();
            a_ptr.dirs.forEach((path, dir) -> { // dir: Path (key)
                Path a_abs = a_root.resolve(dir.name);
                Path b_abs = b_root.resolve(dir.name);
                DirectoryDCP b_dir;
                if ((b_dir = b_ptr.dirs.get(path)) != null && b_ptr.dirs != null) {
                    FileTime time_a;
                    FileTime time_b;
                    try {
                        time_a = Files.getLastModifiedTime(a_abs);
                        time_b = Files.getLastModifiedTime(b_abs);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (time_a.compareTo(time_b) > 0) {
                        current_dirs.put(path, dir);
                        sub_(
                            a_abs,
                            b_abs,
                            a_ptr.dirs.get(path),
                            b_ptr.dirs.get(path),
                            current_dirs.get(path)
                        );
                    } else {
                        return;
                    }
                } else {
                    current_dirs.put(path, dir);
                    sub_( a_abs,
                        b_abs,
                        a_ptr.dirs.get(path),
                        b_ptr.dirs.get(path),
                        current_dirs.get(path)
                    );
                }






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
            });
        }
        return temp;
    }

    public void print() {
        this.structure.print();
    }
}
