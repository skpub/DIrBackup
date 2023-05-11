import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
import java.util.Optional;

public class DirectoryDCP implements Comparable<DirectoryDCP> {
    Path name;
    Optional<Set<Path>> files;
    Optional<Map<Path, DirectoryDCP>> dirs;

    DirectoryDCP() {
        this.files = Optional.empty();
        this.dirs = Optional.empty();
    }
    DirectoryDCP(Path path, boolean recursively) {
        this.name = path;
        this.files = Optional.empty();
        this.dirs = Optional.empty();
        if (recursively) {
            if (!Files.isDirectory(path)) {
                return;
            } else {
                DirectoryDCP_(path, this);
            }
        } else {
            return;
        }
    }

//    DirectoryDCP(Path path) {
//        this.name = path.getFileName();
//        this.files = Optional.empty();
//        this.dirs = Optional.empty();
//        if (!Files.isDirectory(path)) {
//            return;
//        }
//        DirectoryDCP_(path, this);
//    }

    public void DirectoryDCP_(Path path, DirectoryDCP ptr) {
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                if (Files.isDirectory(p)) {
                    DirectoryDCP temp = new DirectoryDCP(p.getFileName(), false);
                    ptr.addDirectory(temp);
                    DirectoryDCP_(p, temp);
                } else {
                    ptr.addFile(p.getFileName());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addFile(Path file) {
        this.files.ifPresentOrElse(
            files -> files.add(file),
            () -> {
                Set<Path> files = new HashSet<Path>();
                this.files = Optional.of(files);
            }
        );
    }

    public void addDirectory(DirectoryDCP dir) {
        this.dirs.ifPresentOrElse(
            dirs -> dirs.put(dir.name, dir),
            () -> {
                Map<Path, DirectoryDCP> temp = new HashMap<>();
                temp.put(dir.name, dir);
                this.dirs = Optional.of(temp);
            }
        );
    }

    public void print() {
        int[] indent = {0};
        print_(this, indent);
    }

    public void printWithIndent(String str, int[] indent) {
        for (int i = 0; i < indent[0]; i++) {
            System.out.print(' ');
        }
        System.out.println(str);
    }

    public void print_(DirectoryDCP ptr, int[] indent) {
        printWithIndent(ptr.name.toString(), indent);
        indent[0] += 4;
        ptr.files.ifPresent(
            files -> files.forEach(a -> printWithIndent(a.toString(), indent))
        );
        ptr.dirs.ifPresent(
            dirs -> {
                for (DirectoryDCP dir : dirs.values()) print_(dir, indent);
                indent[0] -= 4;
            }
        );
    }

    @Override
    public int compareTo (DirectoryDCP o) {
        return this.name.compareTo(o.name);
    }
}
