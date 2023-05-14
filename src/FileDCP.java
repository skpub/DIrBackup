import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

public class FileDCP {
    Path path;
    public Boolean is_older(FileDCP b) {
        FileTime time_a;
        FileTime time_b;
        try {
            time_a = Files.getLastModifiedTime(this.path);
            time_b = Files.getLastModifiedTime(b.path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return time_a.compareTo(time_b) < 0;
    }
}
