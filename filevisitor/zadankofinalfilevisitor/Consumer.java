package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumer implements Callable<Map<String, Integer>> {

    // #done todo #done - consumer odbiera plik i tylko szuka w nim ile razy co wystąpiło

    private static final Set<String> PRIMITIVES = new HashSet<>(Arrays.asList("int", "byte", "boolean", "double", "float", "short", "long"));
    private final BlockingQueue<File> producerOutput;
    private final AtomicInteger activeProducerCounter;

    public Consumer(BlockingQueue<File> producerOutput, AtomicInteger activeProducerCounter) {
        Common.nonNull(producerOutput, "Queue with producer output");
        Common.nonNull(activeProducerCounter, "Active producer counter");
        this.producerOutput = producerOutput;
        this.activeProducerCounter = activeProducerCounter;
    }

    @Override
    public Map<String, Integer> call() {

        Map<String, Integer> consumerOutput = new HashMap<>();

        while (true) {
            //warunek zakonczenia:
            if (activeProducerCounter.intValue() == 0 && producerOutput.isEmpty()) {
                System.out.println("Thread done (" + Thread.currentThread().getName() + ").");
                break;
            }

            try {
             /* List<String> lines = Files.lines(producerOutput.take().toPath()).collect(Collectors.toList());
                for (String line : lines) {
                    for (Map.Entry<String, Integer> entry : getPrimitiveVariableCount(line).entrySet()) {
                        consumerOutput.put(entry.getKey(), consumerOutput.getOrDefault(entry.getKey(), 0) + entry.getValue());
                    }
                }*/
                // #done todo - mówiliśmy o Files.lines ale Scanner to będzie chyba fajneijsze rozwiązanie bo ładujemy cały plik a więc
                //              wykorzystujemy to że mamy całe pliki już w liście od producera:
                Scanner scanner = new Scanner(producerOutput.take());
                while (scanner.hasNextLine()) {
                    for (Map.Entry<String, Integer> entry : getPrimitiveVariableCount(scanner.nextLine()).entrySet()) {
                        consumerOutput.put(entry.getKey(), consumerOutput.getOrDefault(entry.getKey(), 0) + entry.getValue());
                    }
                }
            } catch (FileNotFoundException e) {
                throw new FileLoadingException("Couldn't load a file.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return consumerOutput;
    }

    private Map<String, Integer> getPrimitiveVariableCount(String line) {
        Map<String, Integer> mapToReturn = new HashMap<>();
        for (String variableType : PRIMITIVES) {
            String variableTypePattern = "\\b" + variableType + "\\b ";
            Pattern typePattern = Pattern.compile(variableTypePattern);
            Matcher typeMatcher = typePattern.matcher(line);
            int count = 0;
            while (typeMatcher.find()) {
                count++;
            }
            mapToReturn.put(variableType, count);
        }
        return mapToReturn;
    }
}


