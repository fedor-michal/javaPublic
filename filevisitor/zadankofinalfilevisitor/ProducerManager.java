package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerManager {

    // #done todo #done - zrobić producer managera ktory tworzy nowy producer dla kazdego podfolderu

    public static void createOneProducerPerSubfolder(String directory, BlockingQueue<File> producerOutput, AtomicInteger activeProducerCounter, ExecutorService executorService) {
        Common.nonNull(directory, "Directory");
        Common.nonNull(producerOutput, "Producer output");
        Common.nonNull(activeProducerCounter, "Active producer counter");
        Common.nonNull(executorService, "Executor service");

        try {
            Files.walk(Paths.get(directory), 1)
                    .filter(Objects::nonNull)
                    .filter(Files::isDirectory)
                    .forEach(path -> executorService.submit(new Producer(path.toString(),
                            path.toString().equals(directory), producerOutput, activeProducerCounter)));
        } catch (IOException e) {
            throw new RuntimeException("Invalid directory: " + directory);
        }


    }

}
