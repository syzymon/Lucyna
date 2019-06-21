package pl.edu.mimuw.kd209238.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Path;

import static pl.edu.mimuw.kd209238.common.IndexUtils.analyzerFactory;
import static pl.edu.mimuw.kd209238.common.IndexUtils.openIndexDirectory;

public class IndexManager implements AutoCloseable {

    private static Logger logger = LoggerFactory.getLogger(pl.edu.mimuw.kd209238.indexer.IndexManager.class);

    private Directory indexDir;
    private Analyzer analyzer;
    private IndexWriterConfig iwc;
    private IndexWriter writer;

    public IndexManager(Path indexPath) throws IOException {
        indexDir = openIndexDirectory(indexPath);
        analyzer = analyzerFactory();

        iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(indexDir, iwc);
    }

    public void addDocument(Document doc, String indexField) {
        try {
            writer.updateDocument(new Term(indexField, doc.get(indexField)), doc);
            writer.commit();

            logger.info("Indexed file/dir with path: {}", doc.get(doc.get("path") == null ? "indexed_path" : "path"));
        } catch (IOException e) {
            System.err.println("Failed to index file.");
        }
    }

    public void deleteByField(String fld, String value) throws IOException {
        writer.deleteDocuments(new Term(fld, value));
        writer.commit();

        logger.info("Deleted docs with field: {} ==> {}", fld, value);
    }

    public void deleteAllInDirectory(String dirPath) throws IOException {
        Term term = new Term("path", dirPath);
        writer.deleteDocuments(new PrefixQuery(term));
        writer.commit();

        logger.info("Directory removed: {}", dirPath);
    }

    public String[] getWatchedDirectories() throws IOException {
        String[] result;

        try (IndexReader reader = DirectoryReader.open(writer)) {

            Query query = new NormsFieldExistsQuery("indexed_path");

            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

            result = new String[docs.scoreDocs.length];
            int idx = 0;

            for (ScoreDoc doc : docs.scoreDocs) {
                Document pathDoc = searcher.doc(doc.doc);

                result[idx++] = pathDoc.get("indexed_path");
            }
        }

        return result;
    }

    public void clean() {
        try {
            writer.deleteAll();
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("The index hasn't closed properly.");
        }
    }
}
