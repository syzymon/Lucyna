package pl.edu.mimuw.kd209238.indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocumentExtractor {
    private Tika tika;
    private LanguageDetector detector;

    public DocumentExtractor() throws IOException {
        tika = new Tika();
        detector = new OptimaizeLangDetector();
        detector.loadModels();
    }

    /**
     * Returns a Document object with following properties:
     * path: full path to given file
     * filename: name of the document file without path;
     * content: tokenized content of file
     * lang: language of file
     */
    public Document extract(Path filePath) throws IOException, TikaException {
        String content;

        try (InputStream fileStream = Files.newInputStream(filePath)) {
            content = tika.parseToString(fileStream);
        }

        Document doc = new Document();

        doc.add(new TextField("content", content, Field.Store.YES));

        // TODO: to absolute path??
        Field pathField = new StringField("path", filePath.toString(), Field.Store.YES);
        doc.add(pathField);

        String filename = filePath.getFileName().toString();
        doc.add(new StringField("filename", filename, Field.Store.YES));

        doc.add(new StringField("lang", detector.detect(content).getLanguage(), Field.Store.YES));

        return doc;
    }
}
