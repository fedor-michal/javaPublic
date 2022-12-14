package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws InterruptedException{

		/*	Stworz komunikacje Producent-Consumer miedzy watkami.

			grupa producentow:
			- jako argument dostaja folder w ktorym wyszukuja pliki
			  (rekurencyjnie w dol) .java i dostarczaja je do consumerow

			grupa konsumerow:
			- analizuja zawartosc pliku pod kątem wystąpień typow prymitywnych,
			  tzn: ile jest intow,byteow,booleanow,double,floatow,shortow,
			  longow i na ich podstawie zwiekszaja jakas mape wspolna dla
			  wszystkich consumerow gdzie kluczem bedzie prymitow a wartoscia
			  ilosc wystapien danego prymitowa.

		    mozna uruchomic wiele producentow i wiele consumerow,

		    consumerzy powinni zakonczyc swoje dzialanie w momencie gdy nie ma
		    juz aktywnego producenta i nie zostalo nic do procesowania  */


        // #done todo #done - w consumerze wrzucać od razu do ogólnej mapy dla całego obiektu konsumera

        final String PATH = "C:\\Users\\fedor\\IdeaProjects\\javakurs\\src\\com\\fedordevelopment\\wielowatkowosc\\filevisitor\\zadankofinalfilevisitor";
        final int CONSUMERS_NUMBER = 3;
        AtomicInteger activeProducerCounter = new AtomicInteger();
        BlockingQueue<File> producerOutput = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newCachedThreadPool();

        ProducerManager.createOneProducerPerSubfolder(PATH, producerOutput, activeProducerCounter, executorService);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < CONSUMERS_NUMBER; i++) {
            Future<Map<String, Integer>> future = executorService.submit(new Consumer(producerOutput, activeProducerCounter));
            futures.add(future);
        }

        Map<String, Integer> result = new HashMap<>(); // #done todo #done miałem tu zrobić chyba streama żeby nie było dużych zagnieżdżeń:
        futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum))
                .forEach((key, value) -> result.put(key, result.getOrDefault(key, 0) + value));


        // zmienne do testowania działania
        boolean i = true;
        byte k = 0;
        double f = 2.0;

        executorService.shutdown();
        System.out.println("final result: " + result);
    }
}
