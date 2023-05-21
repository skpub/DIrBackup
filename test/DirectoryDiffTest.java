import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryDiffTest {
    @Test
    public void test() {
        DirectoryDiff diff = new DirectoryDiff(
            Paths.get("test\\A"),
            Paths.get("test\\B")
        );
        diff.print();

        List<Path> deleted = diff.deletedEntries();
        System.out.println("====# DELETED #====");
        deleted.forEach(System.out::println);
    }
}