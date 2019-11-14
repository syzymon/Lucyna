package indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.xml.sax.SAXException;

import common.ConfigConstants;

import java.io.*;
import java.nio.file.Path;

class DocumentExtractor {
    private Tika tika;
    private LanguageDetector detector;

    DocumentExtractor() throws IOException {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(ConfigConstants.TIKA_CONFIG);
            tika = new Tika(new TikaConfig(stream));
        } catch (TikaException | SAXException e) {
            tika = new Tika();
        }
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
    Document extract(Path filePath) throws IOException, TikaException {
        String content;

        content = tika.parseToString(filePath);

        Document doc = new Document();

        String lang = detector.detect(content).getLanguage();

        switch (lang) {
            case "pl":
                doc.add(new TextField("pl", content, Field.Store.YES));
                break;
            case "en":
                doc.add(new TextField("en", content, Field.Store.YES));
                break;
            default:
                doc.add(new TextField("no_lang", content, Field.Store.YES));
        }

        Field pathField = new TextField("path", filePath.toString(), Field.Store.YES);
        doc.add(pathField);

        String filename = filePath.getFileName().toString();
        doc.add(new TextField("filename", filename, Field.Store.YES));

        return doc;
    }

    Document generatePathDocument(String dirPath) {
        Document doc = new Document();

        doc.add(new TextField("indexed_path", dirPath, Field.Store.YES));

        return doc;
    }
}
