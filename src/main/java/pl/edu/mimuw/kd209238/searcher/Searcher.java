package pl.edu.mimuw.kd209238.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.uhighlight.DefaultPassageFormatter;
import org.apache.lucene.search.uhighlight.Passage;
import org.apache.lucene.search.uhighlight.PassageFormatter;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Path;

import static pl.edu.mimuw.kd209238.common.IndexUtils.analyzerFactory;
import static pl.edu.mimuw.kd209238.common.IndexUtils.openIndexDirectory;

public class Searcher {

    private Directory indexDir;
    private Analyzer analyzer;

    private String lang;
    private boolean details;
    private int limit;
    private boolean color;
    private String queryMode;

    private final String ANSI_BOLD = "\u001b[1m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";
    private final String ELLIPSIS = "... ";

    public Searcher(Path indexPath) throws IOException {
        indexDir = openIndexDirectory(indexPath);
        analyzer = analyzerFactory();


        lang = "en";
        details = false;
        setLimit(0);
        color = false;
        queryMode = "term";
    }

    //    private String color(String s) {
//        return ANSI_RED + s + ANSI_RESET;
//    }
//
    private String bold(String s) {
        return ANSI_BOLD + s + ANSI_RESET;
    }

    public String query(String queryStr) throws IOException {
        Query q = queryFactory(queryStr);

//        String result = "";
        try (IndexReader reader = DirectoryReader.open(indexDir)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs hits = searcher.search(q, limit);

            return getResultwithContext(q, hits);
        }
    }

    private Query queryFactory(String queryStr) {
        Query q = null;
        switch (queryMode) {
            case "term": {
                q = new TermQuery(new Term(lang, queryStr));
            }
            default:
                break;
        }
        return q;
    }

    private PassageFormatter formatterFactory() {
        String preTag = ANSI_BOLD;
        if (color)
            preTag += ANSI_RED;
        return new DefaultPassageFormatter(preTag, ANSI_RESET, ELLIPSIS, false);
    }

    private String getResultwithContext(Query query, TopDocs hits) throws IOException {
        try (IndexReader reader = DirectoryReader.open(indexDir)) {
            IndexSearcher searcher = new IndexSearcher(reader);

/*            Formatter formatter = (s, group) -> {
                if (group.getTotalScore() == 0)
                    return s;
                String result = bold(s);
                if (color) result = color(result);
                return result;
            };*/

//            QueryScorer scorer = new QueryScorer(query);
            PassageFormatter formatter = formatterFactory();

            UnifiedHighlighter highlighter = new UnifiedHighlighter(searcher, analyzer);

            highlighter.setFormatter(formatter);
            highlighter.setMaxNoHighlightPassages(Integer.MAX_VALUE);

            StringBuilder result = new StringBuilder("File count: " + bold(Long.toString(hits.totalHits.value)));
            result.append('\n');

            for (ScoreDoc sd : hits.scoreDocs) {
                Document document = searcher.doc(sd.doc);

                result.append(bold(document.get("path")));
                result.append('\n');

//                String text = document.get(lang);
//                Analyzer analyzer = new StandardAnalyzer();
//                TokenStream stream =
//                        TokenSources.getTokenStream("c", null, text, analyzer, highlighter.getMaxDocCharsToAnalyze());
                TopDocs docHits = new TopDocs(new TotalHits(1, hits.totalHits.relation) , new ScoreDoc[] {sd});

                String[] frags = highlighter.highlight(lang, query, docHits, 10);
//                String[] frags = highlighter.getBestFragments(stream, text, 10);

                for (String frag : frags) {
                    result.append(frag);
                }
            }
//            System.err.println(result);
            return result.toString().trim();
        }
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setDetails(String details) {
        this.details = details.equals("on");
    }

    public void setLimit(int limit) {
        if (limit == 0)
            this.limit = Integer.MAX_VALUE;
        else
            this.limit = limit;
    }

    public void setColor(String color) {
        this.color = color.equals("on");
    }


    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }
}
