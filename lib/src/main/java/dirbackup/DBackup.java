package dirbackup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DBackup {
    private final Path target;
    private final Path backup;
    private final Path full;

    public DBackup(Path target, Path backup) {
        this.target = target.toAbsolutePath();
        this.backup = backup.toAbsolutePath();
        this.full = Paths.get(backup.toString() + File.separator + "full");
    }

    public void dBackup() {
        if (!Files.exists(full)) {
            try {
                Files.createDirectories(backup);
                this.fullBackup();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return;
        }
        DirectoryDiff diff = new DirectoryDiff(this.target, this.full);
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String str_date = date_format.format(date);

        this.dBackup_("", str_date, diff);
        try (BufferedWriter bw =
             Files.newBufferedWriter(
                 Paths.get(backup + File.separator + str_date + File.separator + "deleted"),
                 StandardCharsets.UTF_8))
        {
            List<Path> deleted_entries = diff.deletedEntries();
            for (Path deleted_entry: deleted_entries) {
                bw.write(deleted_entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    private void dBackup_(String current, String date, DirectoryDiff diff) {
        String target_current = target + File.separator + current;
        String backup_current = backup + File.separator + date + File.separator + target.getFileName() + File.separator + current;
        try {
            Files.createDirectories(Paths.get(backup_current));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        diff.created_files().forEach(file -> {
            try {
                Files.copy(
                    Paths.get(target_current + File.separator + file),
                    Paths.get(backup_current + File.separator + file)
                );
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        diff.dirs().forEach((path, dir) -> {
            dBackup_(
                current + File.separator + dir.name(),
                date,
                dir
            );
        });
    }

    public void fullBackup() throws IOException {
        Files.walk(target).forEach(src -> {
            try {
                Files.copy(
                    src,
                    full.resolve(target.relativize(src)),
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
