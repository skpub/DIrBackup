import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

public class File {
    private Path file;
    public static Boolean first_is_older(File a, File b) {
        FileTime time_a;
        FileTime time_b;
        try {
            time_a = Files.getLastModifiedTime(a.file);
            time_b = Files.getLastModifiedTime(b.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return time_a.compareTo(time_b) < 0;
    }
}
