
package com.github.eug.apriori;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static class Config {
        public String file = "";
        public int supportThreshold = 0;
        public int threads = 40;
    }

    private static Config parse(String[] args) {
        Config config = new Config();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-f": config.file = args[i + 1]; break;
                case "-s": config.supportThreshold = Integer.parseInt(args[i + 1]); break;
                case "-t": config.threads = Integer.parseInt(args[i + 1]); break;
            }
        }
        return config;
    }

    private static ArrayList<Transaction> readFile(String file) 
            throws FileNotFoundException, IOException {
        
        final ArrayList<Transaction> transactions = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            while (bf.ready()) {
                
                final String[] items = bf.readLine().split(" ");
                
                transactions.add(new Transaction());
                
                for (int i = 0; i < items.length; i++) {
                    transactions.get(transactions.size() - 1)
                                .add(Integer.parseInt(items[i]));
                }
            }
        }

        return transactions;
    }

    public static void main(String[] args) 
            throws FileNotFoundException, IOException {
        
        Config config = parse(args);
        Apriori algo = new Apriori();
        algo.run(readFile(config.file), config.supportThreshold, config.threads);
    }
}
