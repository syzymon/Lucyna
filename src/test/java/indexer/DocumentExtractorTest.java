package indexer;

import org.apache.lucene.document.Document;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import pl.edu.mimuw.kd209238.indexer.DocumentExtractor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.mimuw.kd209238.indexer.DocumentExtractor.*;

public class DocumentExtractorTest {
    @Test
    void testLangs() throws IOException, TikaException {
        DocumentExtractor ext = new DocumentExtractor();
        Path blep = Paths.get("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/rblarba-pl.pdf");
        Document doc = ext.extract(blep);

        System.out.println(doc.get("lang"));
        System.out.println(doc.get("path"));
        System.out.println(doc.get("filename"));
        System.out.println(doc.get("content").substring(0, 1000));

    }
}
