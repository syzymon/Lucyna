package pl.edu.mimuw.kd209238.indexer;

import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesManager {

    private DocumentExtractor extractor;
    private IndexManager index;

    public FilesManager(DocumentExtractor extractor, IndexManager index) {
        this.extractor = extractor;
        this.index = index;
    }

    public void performOperation(String dirPath, String operationName) {
        //check if file at dirPath exists, is not indexed etc
        if (operationName.equals("add"))
            traverse(Paths.get(dirPath), new AddingVisitor());
        //map op name to function name and call traverse
    }

    private void traverse(Path path, SimpleFileVisitor<Path> visitor) {
        try {
            if (Files.isDirectory(path))
                Files.walkFileTree(path, visitor);
            else
                singleVisit(path, visitor);

        } catch (IOException e) {

        }
    }

    private void singleVisit(Path path, SimpleFileVisitor<Path> visitor) throws IOException {
        visitor.visitFile(path, Files.readAttributes(path, BasicFileAttributes.class));
    }

    private class AddingVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            //here we try to parse the file
            // TODO: do not visit symbolic links
            //Open file and extract by extractor, get result with all useful information
            //Useful info:
            try {
                index.addRawDocument(extractor.extract(file));
            } catch (TikaException e) {
                //cannot read content so we shouldn't index this file
                return FileVisitResult.CONTINUE;
            }

            return FileVisitResult.CONTINUE;
        }
    }

    public void addFile(Path path) {
        try {
            singleVisit(path, new AddingVisitor());
        } catch (IOException e) {
            System.err.println("Unable to open file");
        }
    }
}