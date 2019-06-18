package pl.edu.mimuw.kd209238.indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;

public class IndexManager {

    private Directory indexDir;
    private Analyzer analyzer;
    private IndexWriterConfig iwc;
    private IndexWriter writer;

    public IndexManager(Path indexPath) throws IOException {
        indexDir = FSDirectory.open(indexPath);
        analyzer = new StandardAnalyzer();
        iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(indexDir, iwc);
    }

    public void addRawDocument(Document doc) throws IOException {
        writer.updateDocument(new Term("path", doc.get("path")), doc);
    }

    public void close() throws IOException {
        writer.close();
    }
}
