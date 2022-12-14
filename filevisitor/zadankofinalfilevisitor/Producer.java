package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {

    // #done todo #done - producer ma robić listę obiektów a każdy obiekt to plik

    private static final String searchedExtension = ".java";
    private final String PATH;
    private final BlockingQueue<File> producerOutput;
    private final AtomicInteger activeProducerCounter;
    private final boolean isPathMain;

    public Producer(String path, boolean isPathMain, BlockingQueue<File> producerOutput, AtomicInteger activeProducerCounter) {
        Common.nonNull(path, "Path");
        Common.nonNull(producerOutput, "Queue with producer output");
        Common.nonNull(activeProducerCounter, "Active producer counter");
        this.PATH = path;
        this.isPathMain = isPathMain;
        this.producerOutput = producerOutput;
        this.activeProducerCounter = activeProducerCounter;
    }

    @Override
    public void run() {
        ProducerFileVisitor fileVisitor = new ProducerFileVisitor(searchedExtension, producerOutput);

        activeProducerCounter.incrementAndGet();

        try {
            if (isPathMain) {
                // kiedy w folderze głównym oprócz folderów mogą być też pliki - trzeba było dla nich utworzyć Producera.
                //    Tenże producer nie zagłębia się w strukturę, bo głębiej już pracują inne wątki.
                Files.walkFileTree(Paths.get(PATH), EnumSet.allOf(FileVisitOption.class), 1, fileVisitor);
            } else {
                Files.walkFileTree(Paths.get(PATH), fileVisitor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        activeProducerCounter.decrementAndGet();
    }


}
