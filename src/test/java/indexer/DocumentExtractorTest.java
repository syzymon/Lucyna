package indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.mimuw.kd209238.indexer.DocumentExtractor;
import pl.edu.mimuw.kd209238.indexer.FilesManager;
import pl.edu.mimuw.kd209238.indexer.IndexManager;
import pl.edu.mimuw.kd209238.indexer.Watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.mimuw.kd209238.indexer.DocumentExtractor.*;

public class DocumentExtractorTest {
    /*@Test
    void testLangs() throws IOException, TikaException {
        DocumentExtractor ext = new DocumentExtractor();
        Path blep = Paths.get("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/rblarba-pl.pdf");
        Document doc = ext.extract(blep);

        System.out.println(doc.get("path"));
        System.out.println(doc.get("filename"));
        System.out.println(doc.get("pl").substring(0, 1000));
    }

    private DocumentExtractor ext;
    private IndexManager im;
    private FilesManager fm;

    @BeforeEach
    void init() throws IOException {
        ext = new DocumentExtractor();
        im = new IndexManager(Paths.get("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/index"));
        fm = new FilesManager(ext, im);
        im.clean();
    }

    @AfterEach
    protected void finalize() throws IOException {
        im.clean();
        im.close();
    }

    @Test
    void testDeletion() throws IOException {
        final String pathToAdd = "/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/easy_tests";
        fm.performOperation("--add", pathToAdd);

        fm.performOperation("--rm", pathToAdd);
        assertEquals(im.documentsCount(), 0);
    }

    @Test
    void testSearch() throws IOException, ParseException {
        final String pathToAdd = "/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/easy_tests";
        fm.performOperation("--add", pathToAdd);

        assertEquals(im.searchIndex("pl", "zrobić"), "1 hits");
        assertEquals(im.searchIndex("en", "language"), "1 hits");
    }

    @Test
    void testDirectories() throws IOException {
        final String pathToAdd = "/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/easy_tests";
        fm.performOperation("--add", pathToAdd);

        im.dbg();

        System.out.println(fm.performOperation("--list", null));

        fm.performOperation("--rm", pathToAdd);

        assertEquals(im.documentsCount(), 0);
        assertEquals(fm.performOperation("--list", null), "");
    }

    @Test
    void testWatching() throws IOException {
        final String pathToAdd = "/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/easy_tests";
        fm.performOperation("--add", pathToAdd);

        Watcher w = new Watcher(fm);
        w.initialize();
        w.processEvents();
    }*/
}
