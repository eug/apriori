
package com.github.eug.apriori;

import java.util.HashSet;

public class Candidate {

    private HashSet<Integer> items = new HashSet<>();

    @SuppressWarnings("unused")
    public void addItem(Integer item) {
        items.add(item);
    }

    @SuppressWarnings("unused")
    public void removeItem(Integer item) {
        items.remove(item);
    }

    public void setItems(HashSet<Integer> items) {
        this.items = items;
    }

    public HashSet<Integer> getItems() {
        return items;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * items.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        if (obj instanceof Candidate) {
            
            final Candidate other = (Candidate) obj;
            
            if (items.size() == other.getItems().size()) {
                return items.containsAll(other.getItems());
            } else {
                return false;
            }
            
        } else {
            return false;
        }
        
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
