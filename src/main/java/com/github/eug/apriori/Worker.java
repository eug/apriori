
package com.github.eug.apriori;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class Worker implements Runnable {

    private final Candidate candidate;
    private final ConcurrentHashMap<Candidate, Integer> frequencyStorage;
    private final ArrayList<Transaction> transactions;
    private final List<Candidate> pruneList;
    private final int supportThreshold;
    
    public Worker(final List<Candidate> pruneList,
            final ConcurrentHashMap<Candidate, Integer> frequencyStorage, 
            final Candidate candidate, ArrayList<Transaction> transactions,
            final int supportThreshold) {
        this.frequencyStorage = frequencyStorage;
        this.candidate = candidate;
        this.transactions = transactions;
        this.pruneList = pruneList;
        this.supportThreshold = supportThreshold;
    }

    @Override
    public void run() {
        count(candidate, transactions);
        filter(candidate, supportThreshold);
    }
    
    private void count(final Candidate candidate,
            final ArrayList<Transaction> transactions) {

        for (ArrayList<Integer> transaction : transactions) {
            if (transaction.containsAll(candidate.getItems())) {
                final Integer frequency = frequencyStorage.get(candidate);
                if (frequency != null) {
                    frequencyStorage.put(candidate, frequency + 1);
                } else {
                    frequencyStorage.put(candidate, 1);
                }
            }
        }
        
    }
    
    private void filter(final Candidate candidate, final int supportThreshold) {
        final Integer frequency = frequencyStorage.get(candidate);
        if ((frequency != null) && (frequency < supportThreshold)) {
            frequencyStorage.remove(candidate);
            // prune list ignores singleton candidates
            if (candidate.getItems().size() > 1) {
                pruneList.add(candidate);
            }
        }
    }
}
