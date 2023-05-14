import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryDCPTest {
    @Test
    void test () {
        Directory dir_a = new Directory(
            Paths.get("C:\\Users\\kaito\\IdeaProjects\\DIrCopy\\test\\a")
        );
        Directory dir_b = new Directory(
            Paths.get("C:\\Users\\kaito\\IdeaProjects\\DIrCopy\\test\\b")
        );
        Directory sub = dir_a.sub(dir_b);
        sub.print();
    }
}