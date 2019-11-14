package indexer;

import java.io.IOException;

import static common.ConfigConstants.INDEX_PATH;

public class IndexerMain {

    public static void main(String[] args) {
        try (IndexManager index = new IndexManager(INDEX_PATH)) {
            DocumentExtractor extractor = new DocumentExtractor();
            FilesManager files = new FilesManager(extractor, index);

            if (args.length > 0) {
                String cmd_name = args[0];
                String dir_name = args.length > 1 ? args[1] : "";
                String result = files.performOperation(cmd_name, dir_name);

                if (!result.isEmpty())
                    System.out.println(result);
            } else {
                Watcher w = new Watcher(files);
                w.initialize();
                w.processEvents();
            }
        } catch (IOException e) {
            System.err.println("Couldn't load index.");
            System.exit(1);
        }
    }
}
