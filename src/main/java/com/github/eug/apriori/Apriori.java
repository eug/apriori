
package com.github.eug.apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
        System.out.println("Support Threshold: " + supportThreshold);
        System.out.println("Transactions "  + transactions.size());

        long start;
        int k = 1;
        
        // genereate singletons items
        ArrayList<Candidate> trulyFrequent = generateItemset(transactions);

        do {
            log("======================= Iteration " + k + " =======================");
            start = System.currentTimeMillis();
            
            frequencyStorage.clear();
            
            final ArrayList<Candidate> candidates = generateItemset(trulyFrequent, k);
            
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
            
            k++;
        } while(!frequencyStorage.isEmpty());

        log("======================= Output =======================", trulyFrequent);
    }

    /**
     * Generate initial singletons given the transactions.
     * @param transactions List containing all transactions
     * @return List of singletons candidates
     */
    private ArrayList<Candidate> generateItemset(ArrayList<Transaction> transactions) {
        final ArrayList<Candidate> itemset = new ArrayList<>();
        final HashSet<Integer> items = new HashSet<>();

        // creates a temporary hash containing all singletons
        for (ArrayList<Integer> transaction : transactions) {
            for (Integer item : transaction) {
                items.add(item);
            }
        }

        // put each singleton on itemset
        for (Integer item : items) {
            final Candidate candidate = new Candidate();
            candidate.addItem(item);
            itemset.add(candidate);
        }

        return itemset;
    }

    /**
     * Generate candidates containing k items.
     * @param candidates List of previous candidates
     * @param k Number of items per candidate
     * @return List of candidates containing k items
     */
    private ArrayList<Candidate> generateItemset(ArrayList<Candidate> candidates, int k) {
        
        if (k <= 1) {
            return candidates;
        }

        HashSet<Candidate> itemset = new HashSet<>();
        
        // generate all possibilities eliminating the duplicates
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                for (Candidate candidate : join(candidates.get(i), candidates.get(j), k)) {
                    itemset.add(candidate);
                }
            }
        }

        ArrayList<Candidate> candidateList = new ArrayList<>(itemset);

        // prune sets
        for (Candidate prune : pruneList) {
            for (int i = 0; i < candidateList.size(); i++) {
                Candidate candidate = candidateList.get(i);
                if (candidate.getItems().containsAll(prune.getItems())) {
                    candidateList.remove(i--);
                }
            }
        }
        pruneList.clear();

        return candidateList;
    }

    /**
     * Join two candidates to the next iteration.
     * @param candidate1 First candidate
     * @param candidate2 Second candidate
     * @param k Number of items per candidate
     * @return Return a set of candidates containing k items for each candidate.
     */
    private HashSet<Candidate> join(Candidate candidate1, Candidate candidate2, int k) {
        final HashSet<Candidate> candidates = new HashSet<>();
        
        // store a all items of both sets (candidate1 and candidate2)
        final HashSet<Integer> set = new HashSet<>();

        for (Integer item : candidate1.getItems()) {
            set.add(item);
        }

        for (Integer item : candidate2.getItems()) {
            set.add(item);
        }

        if (set.size() == k) {
            final Candidate candidate = new Candidate();
            candidate.setItems(set);
            candidates.add(candidate);
        } else if (set.size() > k) {
            // in this case we need to create more possibilities
            // spliting the 'set' variable
            int i = 1;
            final HashSet<Integer> base = new HashSet<>();

            // get the first k-1 items (base) and create 
            // new candidates with the remaining values
            for (Integer item : set) {
                base.add(item);

                if (i++ >= k) {
                    final Candidate candidate = new Candidate();
                    candidate.setItems(new HashSet<>(base));
                    candidates.add(candidate);
                    base.remove(item);
                }
            }
        } // otherwise ignore less than k

        return candidates;
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
