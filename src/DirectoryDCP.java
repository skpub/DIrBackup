import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class DirectoryDCP implements Comparable<DirectoryDCP> {
    Path name;
    Set<Path> files = new HashSet<>();
    Map<Path, DirectoryDCP> dirs = new HashMap<>();

    DirectoryDCP() {}
    DirectoryDCP(String name) {
        this.name = Paths.get(name);
    }
    DirectoryDCP(Path path) {
        this.name = path.getFileName();
        if (!Files.isDirectory(path)) {
            return;
        }
//        Directory_(path, this);
        DirectoryDCP_(path, this);
    }
    public void DirectoryDCP_(Path path, DirectoryDCP ptr) {
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                if (Files.isDirectory(p)) {
                    DirectoryDCP temp = new DirectoryDCP(p.getFileName().toString());
                    ptr.dirs.put(p.getFileName(), temp);
                    DirectoryDCP_(p, temp);
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

    public void print_(DirectoryDCP ptr, int[] indent) {
        printWithIndent(ptr.name.toString(), indent);
        indent[0] += 4;
        ptr.files.forEach(a -> printWithIndent(a.toString(), indent));
        for (DirectoryDCP dir : ptr.dirs.values()) print_(dir, indent);
        indent[0] -= 4;
    }

    public static void df_function(
        DirectoryDCP new_dir,
        DirectoryDCP latest_dir
    ) {
         if (new_dir.dirs == null) return;

//         new_dir.files.entrySet().stream()
    }

    public DirectoryDCP copy_from(DirectoryDCP from) {
        DirectoryDCP temp = new DirectoryDCP();
        temp.name = from.name;
        temp.files = new HashSet<>(from.files);
        temp.dirs = new HashMap<>(from.dirs);
        return temp;
    }

    @Override
    public int compareTo (DirectoryDCP o) {
        return this.name.compareTo(o.name);
    }
}
