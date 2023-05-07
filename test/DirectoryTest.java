import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryTest {
    @Test
    void test () {
        Path path = Paths.get("C:\\Users\\kaito\\IdeaProjects");
        Directory dir = new Directory(path);
        dir.print();
    }
}