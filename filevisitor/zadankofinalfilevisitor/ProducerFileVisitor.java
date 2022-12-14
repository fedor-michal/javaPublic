package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;

public class ProducerFileVisitor implements FileVisitor<Path> {

    // #done todo #done - FileVisitor to ma być Klasa osobna

    private final String searchedExtension;
    private final BlockingQueue<File> producerOutput;

    public ProducerFileVisitor(String searchedExtension, BlockingQueue<File> producerOutput) {
        Common.nonNull(searchedExtension, "Searched extension");
        Common.nonNull(producerOutput, "Producer output queue");
        this.searchedExtension = searchedExtension;
        this.producerOutput = producerOutput;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String path = file.toString();
        if (path.endsWith(searchedExtension)) {
            try {
                producerOutput.put(new File(path));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
