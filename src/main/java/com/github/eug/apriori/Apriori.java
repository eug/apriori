
package com.github.eug.apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Apriori {
   
    // list of items that must be ignored when generating new itemsets
    private final List<Candidate> pruneList = Collections.synchronizedList(new ArrayList<Candidate>());
    
    // datastruture to store the counting of items
    private final ConcurrentHashMap<Candidate, Integer> frequencyStorage = new ConcurrentHashMap<>();

    @SuppressWarnings("empty-statement")
    public void run(final ArrayList<Transaction> transactions, int supportThreshold, int threads) {
        long start;
        int k = 1;
        ItemsetGenerator itemsetGen = new ItemsetGenerator(pruneList);
        
        System.out.println("Support Threshold: " + supportThreshold);
        System.out.println("Transactions "  + transactions.size());

        // genereate singletons items
        ArrayList<Candidate> trulyFrequent = itemsetGen.generateItemset(transactions);

        do {
            log("======================= Iteration " + k + " =======================");
            start = System.currentTimeMillis();
            
            frequencyStorage.clear();
            
            final ArrayList<Candidate> candidates = itemsetGen.generateItemset(trulyFrequent, k++);
            
            final ExecutorService executor = Executors.newFixedThreadPool(threads);
            
            for (Candidate candidate : candidates) {
                executor.execute(new Worker(
                    pruneList,
                    frequencyStorage,
                    candidate,
                    transactions,
                    supportThreshold
                ));
            }
            
            executor.shutdown();
            
            while(!executor.isTerminated());
            
            
            for (Map.Entry<Candidate, Integer> entry : frequencyStorage.entrySet()) {
                log(entry.getKey() + "\t" + entry.getValue() + "\t" + entry.getValue() / (float) transactions.size() * 100.0f + "%");
            }
            
            if (!frequencyStorage.isEmpty()) {
                trulyFrequent = new ArrayList<>(frequencyStorage.keySet());
            }
            
            log((System.currentTimeMillis() - start) / 1000 + " sec");
            
        } while(!frequencyStorage.isEmpty());

        log("======================= Output =======================", trulyFrequent);
    }

    // Log helpers
    private void log(String message) {
        System.out.println(message);
    }

    private void log(String message, ArrayList<Candidate> arrayList) {
        log(message);
        for (Candidate candidate : arrayList) {
            System.out.println(candidate);
        }
    }

}
