package pl.edu.mimuw.kd209238.indexer;

import java.io.IOException;
import java.nio.file.Paths;

public class CLI {

    public static void main(String[] args) throws IOException {
        IndexManager im = new IndexManager(Paths.get("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/index"));
        DocumentExtractor ext = new DocumentExtractor();
        FilesManager fm = new FilesManager(ext, im);

        fm.performOperation("/home/syzymon/Pulpit/java/PO_2/Lucyna/src/test/resources/pdfs", "add");

        im.close();
    }
}
