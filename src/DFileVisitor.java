import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DFileVisitor implements FileVisitor<Path> {
    Directory dir;
    Directory ptr = dir;

    @Override
    public FileVisitResult preVisitDirectory (
        Path file,
        BasicFileAttributes attrs) throws IOException
    {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile (
        Path file,
        BasicFileAttributes attrs) throws IOException
    {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed (
        Path file,
        IOException exc) throws IOException
    {
        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult postVisitDirectory (
        Path dir,
        IOException exc) throws IOException
    {
        return FileVisitResult.CONTINUE;
    }
}
