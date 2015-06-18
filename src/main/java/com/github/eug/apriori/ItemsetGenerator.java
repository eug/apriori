package com.github.eug.apriori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author eugf
 */
public class ItemsetGenerator {

    private final List<Candidate> pruneList;

    public ItemsetGenerator(List<Candidate> pruneList) {
        this.pruneList = pruneList;
    }

    /**
     * Generate initial singletons given the transactions.
     *
     * @param transactions List containing all transactions
     * @return List of singletons candidates
     */
    public ArrayList<Candidate> generateItemset(ArrayList<Transaction> transactions) {
        final ArrayList<Candidate> itemset = new ArrayList<>();
        final HashSet<Integer> items = new HashSet<>();
        for (ArrayList<Integer> transaction : transactions) {
            for (Integer item : transaction) {
                items.add(item);
            }
        }
        for (Integer item : items) {
            final Candidate candidate = new Candidate();
            candidate.addItem(item);
            itemset.add(candidate);
        }
        return itemset;
    }

    /**
     * Generate candidates containing k items.
     *
     * @param candidates List of previous candidates
     * @param k Number of items per candidate
     * @return List of candidates containing k items
     */
    public ArrayList<Candidate> generateItemset(ArrayList<Candidate> candidates, int k) {
        if (k <= 1) {
            return candidates;
        }
        
        HashSet<Candidate> itemset = new HashSet<>();
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                for (Candidate candidate : join(candidates.get(i), candidates.get(j), k)) {
                    itemset.add(candidate);
                }
            }
        }
        ArrayList<Candidate> candidateList = new ArrayList<>(itemset);
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
     *
     * @param candidate1 First candidate
     * @param candidate2 Second candidate
     * @param k Number of items per candidate
     * @return Return a set of candidates containing k items for each candidate.
     */
    private HashSet<Candidate> join(Candidate candidate1, Candidate candidate2, int k) {
        final HashSet<Candidate> candidates = new HashSet<>();
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
            for (Integer item : set) {
                base.add(item);
                if (i++ >= k) {
                    final Candidate candidate = new Candidate();
                    candidate.setItems(new HashSet<>(base));
                    candidates.add(candidate);
                    base.remove(item);
                }
            }
        }
        return candidates;
    }

}
