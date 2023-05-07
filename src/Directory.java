import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.*;

public class Directory {
    private String name;
    private Set<Path> files = new HashSet<Path>();
    private Set<Directory> dirs = new HashSet<Directory>();

    Directory(String name) {
        this.name = name;
    }
    Directory(Path path) {
        this.name = path.getFileName().toString();
        if (!Files.isDirectory(path)) {
            return;
        }
//        Directory_(path, this);
        Directory_(path, this);
    }
    public void Directory_(Path path, Directory ptr) {
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                if (Files.isDirectory(p)) {
                    Directory temp = new Directory(p.getFileName().toString());
                    ptr.dirs.add(temp);
                    Directory_(p, temp);
                } else {
                    ptr.files.add(p.getFileName());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void print_(Directory ptr, int[] indent) {
        printWithIndent(ptr.name, indent);
        indent[0] += 4;
        ptr.files.forEach(a -> printWithIndent(a.toString(), indent));
        ptr.dirs.forEach(a -> print_(a, indent));
        indent[0] -= 4;
    }

    public static void df_function(
        Directory new_dir,
        Directory latest_dir
    ) {
         if (new_dir.dirs == null) return;

//         new_dir.files.entrySet().stream()
    }
}
