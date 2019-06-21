package pl.edu.mimuw.kd209238.common;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class IndexUtils {
    public static Directory openIndexDirectory(Path indexPath) throws IOException {
        return FSDirectory.open(indexPath);
    }

    public static Analyzer analyzerFactory() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();

        analyzerMap.put("path", new KeywordAnalyzer());
        analyzerMap.put("filename", new KeywordAnalyzer());
        analyzerMap.put("pl", new PolishAnalyzer());
        analyzerMap.put("en", new EnglishAnalyzer());
        analyzerMap.put("no_lang", new StandardAnalyzer());

        analyzerMap.put("indexed_path", new KeywordAnalyzer());

        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
    }

    public static HashMap<String, Analyzer> generateQueryAnalyzers() {
        HashMap<String, Analyzer> queryAnalyzerMap = new HashMap<>();

        queryAnalyzerMap.put("pl", new PolishAnalyzer());
        queryAnalyzerMap.put("en", new EnglishAnalyzer());
        return queryAnalyzerMap;
    }
}
