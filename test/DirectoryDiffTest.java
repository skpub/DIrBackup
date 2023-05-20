import org.junit.jupiter.api.Test;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryDiffTest {
    @Test
    public void test() {
        DirectoryDiff diff = new DirectoryDiff(
            Paths.get("C:\\Users\\kaito\\IdeaProjects\\JDIrCopy\\src\\test\\resources\\A"),
            Paths.get("C:\\Users\\kaito\\IdeaProjects\\JDIrCopy\\src\\test\\resources\\B")
        );
        diff.print();
    }
}