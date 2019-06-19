package pl.edu.mimuw.kd209238.indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class IndexManager {

    private Directory indexDir;
    private Analyzer analyzer;
    private IndexWriterConfig iwc;
    private IndexWriter writer;

    private Analyzer generateAnalyzer() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();

        analyzerMap.put("path", new KeywordAnalyzer());
        analyzerMap.put("filename", new KeywordAnalyzer());
        analyzerMap.put("pl", new PolishAnalyzer());
        analyzerMap.put("en", new EnglishAnalyzer());
        analyzerMap.put("no_lang", new StandardAnalyzer());

        analyzerMap.put("indexed_path", new KeywordAnalyzer());

        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
    }

    public IndexManager(Path indexPath) throws IOException {
        indexDir = FSDirectory.open(indexPath);
        analyzer = generateAnalyzer();
        iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(indexDir, iwc);
    }

    public void addDocument(Document doc, String indexField) {
        try {
            writer.updateDocument(new Term(indexField, doc.get(indexField)), doc);
            writer.commit();
        } catch (IOException e) {
            System.err.println("Failed to index file.");
        }
//        if(indexField.equals("indexed_path"))
//            System.out.println(doc.get("indexed_path"));
    }

    public void deleteByField(String fld, String value) throws IOException {
        writer.deleteDocuments(new Term(fld, value));
        writer.commit();
    }

    public void deleteAllInDirectory(String dirPath) throws IOException {
        Term term = new Term("path", dirPath);
        writer.deleteDocuments(new PrefixQuery(term));
        writer.commit();
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
//            e.printStackTrace();
            System.err.println("The index hasn't closed properly.");
        }
    }

    public void clean() {
        try {
            writer.deleteAll();
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String searchIndex(String inField, String queryString) {
        try (IndexReader reader = DirectoryReader.open(writer)) {
            Query query = new QueryParser(inField, analyzer).parse(queryString);

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);

            System.out.println(topDocs.totalHits);
            return topDocs.totalHits.toString();
        } catch (ParseException e) {
        } catch (IOException e) {

        }
        return "";
    }

    public int documentsCount() {
        int result = -1;
        try (IndexReader reader = DirectoryReader.open(writer)) {
            result = reader.numDocs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void dbg() throws IOException {
        try (IndexReader reader = DirectoryReader.open(writer)) {

            Query query = new MatchAllDocsQuery();

            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

//            System.out.println(docs.totalHits);

            for (ScoreDoc doc : docs.scoreDocs) {
                Document debugDoc = searcher.doc(doc.doc);

                System.out.println(debugDoc);
            }
        }
    }

    public String getWatchedDirectories() throws IOException {
        StringBuilder result = new StringBuilder();

        try (IndexReader reader = DirectoryReader.open(writer)) {

            Query query = new NormsFieldExistsQuery("indexed_path");

            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

//            System.out.println(docs.totalHits);

            for (ScoreDoc doc : docs.scoreDocs) {
                Document pathDoc = searcher.doc(doc.doc);

                result.append(pathDoc.get("indexed_path"));

                result.append("\n");
            }

        }

        return result.toString();
    }
}
