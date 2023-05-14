import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            sub_(
                this.path.toString(),
                b.path.toString(),
                "",
                this.structure,
                Optional.ofNullable(b.structure),
                temp.structure).get()
        );
    }

    public static Optional<DirectoryDCP> sub_(
        String a_root,
        String b_root,
        String rel_current,
        DirectoryDCP a_ptr,
        Optional<DirectoryDCP> b_ptr_opt,
        DirectoryDCP parent
    ) {
//        Optional<DirectoryDCP> current = Optional.empty();
        DirectoryDCP current = new DirectoryDCP(Paths.get(a_root + File.separator + rel_current), false);
        a_ptr.dirs.ifPresent(
            a_dirs -> {
                a_dirs.forEach(
                    (a_dir_path, a_dir) -> {
                        Optional<DirectoryDCP> new_b
                            = b_ptr_opt.flatMap(
                                b_ptr -> b_ptr.dirs.flatMap(
                                    b_dir -> Optional.ofNullable(b_dir.get(a_dir_path))));
                        sub_(
                            a_root,
                            b_root,
                            rel_current + File.separator + a_dir_path,
                            a_dir,
                            new_b,
                            current);
                    }
                );
            }
        );
        // currentにファイルを突っ込んだ上でparentに入れる
        b_ptr_opt.ifPresentOrElse(
            b_ptr -> {
                b_ptr.files.ifPresentOrElse(
                    b_files -> {
                        a_ptr.files.ifPresent(
                            a_files -> {
                                a_files.forEach(
                                    a_file -> {
                                        if (b_files.contains(a_file)) {
                                            Path a_abs = Paths.get(a_root + File.separator + rel_current + File.separator + a_file);
                                            Path b_abs = Paths.get(b_root + File.separator + rel_current + File.separator + a_file);
                                            FileTime time_a;
                                            FileTime time_b;
                                            try {
                                                time_a = Files.getLastModifiedTime(a_abs);
                                                time_b = Files.getLastModifiedTime(b_abs);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if (time_a.compareTo(time_b) > 0) {
                                                current.addFile(a_file);
                                            }
                                        }
                                    }
                                );
                            }
                        );
                    },
                    () -> {
                        a_ptr.files.ifPresent(
                            a_files -> {
                                a_files.forEach(current::addFile);
                            }
                        );
                    }
                );
            },
            () -> {
                a_ptr.files.ifPresent(
                    a_files -> {
                        a_files.forEach(current::addFile);
                    }
                );
            }
        );
        if (current.files.isEmpty() && current.dirs.isEmpty()) {
            return Optional.empty();
        } else {
            parent.addDirectory(current);
            return Optional.of(current);
        }
    }

    public void print() {
        this.structure.print();
    }
}
