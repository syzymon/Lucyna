package pl.edu.mimuw.kd209238.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

public class Watcher {

    private static Logger logger = LoggerFactory.getLogger(pl.edu.mimuw.kd209238.indexer.Watcher.class);

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private FilesManager filesManager;

    /**
     * Creates a WatchService and registers the given directory
     */
    public Watcher(FilesManager fm) throws IOException {
        this.watcher = FileSystems.getDefault()
                .newWatchService();
        this.keys = new HashMap<>();
        this.filesManager = fm;
    }

    public void initialize() throws IOException {
        String[] watchList = filesManager.getWatchedList();
        for (String dir : watchList) {
            registerAll(Paths.get(dir));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        Path prev = keys.get(key);
        if (prev == null) {
            logger.info("register: {}", dir);
        } else {
            if (!dir.equals(prev)) {
                logger.info("update: {} -> {}", prev, dir);
            }
        }

        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        while (true) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                logger.warn("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);


                // print out event
                logger.info("{}: {}", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories

                if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }

                        if (Files.isRegularFile(child)) {

                            filesManager.addFile(child);

                        }
                    } catch (IOException x) {
                        // ignore to keep sample readable
                    }
                } else if (kind == ENTRY_DELETE) {
                    filesManager.deleteFileFromIndex(child);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}