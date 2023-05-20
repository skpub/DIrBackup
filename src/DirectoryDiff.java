import javax.management.RuntimeErrorException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.KeyPair;
import java.sql.Array;
import java.util.*;
import java.util.stream.Stream;

public class DirectoryDiff {
    public Path name;
    private Boolean deleted;
    public HashSet<Path> deleted_files;
    private HashSet<Path> created_files;
    public HashMap<Path, DirectoryDiff> dirs;

    DirectoryDiff(Path name) {
        this.name = name;
        this.deleted = false;
        this.deleted_files = new HashSet<>();
        this.created_files = new HashSet<>();
        this.dirs = new HashMap<>();
    }

    DirectoryDiff(Path dir_a, Path dir_b) {
        DirectoryDiff temp = diff(dir_a, dir_b);
        this.name = temp.name;
        this.deleted = false;
        this.deleted_files = temp.deleted_files;
        this.created_files = temp.created_files;
        this.dirs = temp.dirs;
    }

    public DirectoryDiff diff(Path dir_a, Path dir_b) {
        DirectoryDiff diff = new DirectoryDiff(dir_a.getFileName());
        if (!this.exists(dir_a) && !this.exists(dir_b)) return diff;
        if (!this.exists(dir_a)) { // dir_b is exist;
            diff.deleted = true;
            return diff;
        }

        HashSet<Path> a_files = new HashSet<>();
        HashSet<Path> a_dirs = new HashSet<>();
        try (Stream<Path> f_or_dirs = Files.list(dir_a)) {
            f_or_dirs.forEach( file_or_dir -> {
                if (Files.isDirectory(file_or_dir))
                    a_dirs.add(file_or_dir.getFileName());
                else
                    a_files.add(file_or_dir.getFileName());
            });
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

        if (!this.exists(dir_b)) {
            diff.created_files.addAll(a_files);
            a_dirs.forEach( a_dir -> {
                DirectoryDiff child = diff(
                    Paths.get(dir_a.toString() + File.separator + a_dir.getFileName().toString()),
                    null
                );
                diff.dirs.put(a_dir.getFileName(), child);
            });
            return diff;
        }

        //////////////////////////////  _|  |_
        // dir_a:EXIST, dir_b:EXIST //  \   /
        //////////////////////////////   \/
        HashSet<Path> b_files = new HashSet<>();
        HashSet<Path> b_dirs = new HashSet<>();
        try (Stream<Path> f_or_dirs = Files.list(dir_b)) {
            f_or_dirs.forEach( file_or_dir -> {
                if (Files.isDirectory(file_or_dir))
                    b_dirs.add(file_or_dir.getFileName());
                else
                    b_files.add(file_or_dir.getFileName());
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        for (Path a_file: a_files) {
            if (b_files.contains(a_file)) {
                FileTime time_a;
                FileTime time_b;
                try {
                    time_a = Files.getLastModifiedTime(Paths.get(
                        dir_a.toString() + File.separator + a_file.toString()));
                    time_b = Files.getLastModifiedTime(Paths.get(
                        dir_b.toString() + File.separator + a_file.toString()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                if (time_a.compareTo(time_b) > 0) {
                    diff.created_files.add(a_file);
                }
            }
            diff.created_files.add(a_file);
        }
        for (Path b_file: b_files) {
            if (!a_files.contains(b_file)) {
                diff.deleted_files.add(b_file);
            }
        }

        for (Path a_dir: a_dirs) {
            if (b_dirs.contains(a_dir)) {
                DirectoryDiff temp = this.diff(
                    Paths.get(dir_a.toString() + File.separator + a_dir.toString()),
                    Paths.get(dir_b.toString() + File.separator + a_dir.toString())
                );
                diff.dirs.put(a_dir, temp);
            }
            else {
                diff.dirs.put(a_dir, this.diff(
                    Paths.get(dir_a.toString() + File.separator + a_dir.toString()),
                    null
                ));
            }
        }

        for (Path b_dir: b_dirs) {
            if (!a_dirs.contains(b_dir)) {
                DirectoryDiff temp = new DirectoryDiff(b_dir);
                temp.deleted = true;
                diff.dirs.put(b_dir, temp);
            }
        }
        return diff;
    }

    public boolean exists(Path f) {
        if (f == null) return false;
        return Files.exists(f);
    }

    public void println_with_indent(String str, int indent) {
        StringBuilder temp = new StringBuilder();
        temp.append(" ".repeat(Math.max(0, indent)));
        System.out.print(temp);
        System.out.println(str);
    }
    public void print() {
        print_(this, 0);
    }
    public void print_(DirectoryDiff ptr, int indent) {
        println_with_indent((ptr.deleted? "deleted: ": "") + ptr.name.toString(), indent);
        indent += 4;
        for (Path file: ptr.deleted_files) {
            println_with_indent("deleted: " + file.toString(), indent);
        }
        for (Path file: ptr.created_files)
            println_with_indent("created: " + file.toString(), indent);
        for (Map.Entry<Path, DirectoryDiff> dir: ptr.dirs.entrySet())
            print_(dir.getValue(), indent);
    }
}
