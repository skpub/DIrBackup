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
    private Path name;
    private Boolean deleted;
    private List<Path> deleted_files;
    private List<Path> created_files;
    private HashMap<Path, DirectoryDiff> dirs;

    DirectoryDiff(Path name) {
        this.name = name;
        this.deleted = false;
        this.deleted_files = new ArrayList<>();
        this.created_files = new ArrayList<>();
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
        DirectoryDiff diff = new DirectoryDiff(dir_a);
        if (!this.exists(dir_a) && !this.exists(dir_b)) return diff;
        if (!this.exists(dir_a)) { // dir_b is exist;
            diff.deleted = true;
            return diff;
        }

        List<Path> a_files = new ArrayList<>();
        List<Path> a_dirs = new ArrayList<>();
        try (Stream<Path> f_or_dirs = Files.list(dir_a)) {
            f_or_dirs.forEach( file_or_dir -> {
                if (Files.isDirectory(file_or_dir))
                    a_dirs.add(file_or_dir);
                else
                    a_files.add(file_or_dir);
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
                diff.dirs.put(a_dir, child);
            });
            return diff;
        }

        //////////////////////////////  _|  |_
        // dir_a:EXIST, dir_b:EXIST //  \   /
        //////////////////////////////   \/
        List<Path> b_files = new ArrayList<>();
        List<Path> b_dirs = new ArrayList<>();
        try (Stream<Path> f_or_dirs = Files.list(dir_b)) {
            f_or_dirs.forEach( file_or_dir -> {
                if (Files.isDirectory(file_or_dir))
                    b_dirs.add(file_or_dir);
                else
                    b_files.add(file_or_dir);
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Collections.sort(a_files);
        Collections.sort(a_dirs);
        Collections.sort(b_files);
        Collections.sort(b_dirs);
        int index = 0;
        for (Path a_file: a_files) {
            for (; index < b_files.size()+1; index++) {
                if (index == b_files.size() || b_files.get(index).compareTo(a_file) > 0) {
                    diff.created_files.add(a_file);
                    break;
                }
                if (b_files.get(index) == a_file) {
                    FileTime time_a;
                    FileTime time_b;
                    try {
                        time_a = Files.getLastModifiedTime(a_file);
                        time_b = Files.getLastModifiedTime(b_files.get(index));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    if (time_a.compareTo(time_b) > 0)
                        diff.created_files.add(a_file);
                    break;
                }
            }
        }
        index = 0;
        for (Path b_file: b_files) {
            for (; index < a_files.size()+1; index++) {
                if (index == a_files.size() || a_files.get(index).compareTo(b_file) > 0) {
                    diff.deleted_files.add(b_file);
                    break;
                }
                if (a_files.get(index) == b_file) {
                    // ALREADY EXAMINED THIS PATTERN.
                    // a file_x in A and B means that a file_x in B and A
                    break;
                }
            }
        }

        index = 0;
        for (Path a_dir: a_dirs) {
            for (; index < b_dirs.size()+1; index++) {
                if (index == b_dirs.size() || b_dirs.get(index).compareTo(a_dir) > 0) {
                    // CREATED.
                    DirectoryDiff temp = diff(a_dir, null);
                    diff.dirs.put(a_dir, temp);
                    break;
                }
                if (b_dirs.get(index) == a_dir) {
                    DirectoryDiff temp = diff(a_dir, b_dirs.get(index));
                    diff.dirs.put(a_dir, temp);
                    break;
                }
            }
        }

        index = 0;
        for (Path b_dir: b_dirs) {
            for (; index < a_dirs.size()+1; index++) {
                if (index == a_dirs.size() || a_dirs.get(index).compareTo(b_dir) > 0) {
                    // DELETED.
                    DirectoryDiff temp = new DirectoryDiff(b_dir);
                    temp.deleted = true;
                    diff.dirs.put(b_dir, temp);
                    break;
                }
                if (a_dirs.get(index) == b_dir) {
                    // ALREADY EXAMINED THIS PATTERN.
                    // a dir_x in A and B means that a dir_x in B and A
                    break;
                }
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
        println_with_indent((ptr.deleted? "deleted: ": "created: ") + ptr.name.toString(), indent);
        indent += 4;
        for (Path file: ptr.deleted_files)
            println_with_indent("deleted: " + file.toString(), indent);
        for (Path file: ptr.created_files)
            println_with_indent("created: " + file.toString(), indent);
        for (Map.Entry<Path, DirectoryDiff> dir: ptr.dirs.entrySet())
            print_(dir.getValue(), indent);
    }
}
