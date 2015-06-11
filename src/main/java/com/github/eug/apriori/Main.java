
package com.github.eug.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

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
        
        Config config = Config.parse(args);
        
        if (!new File(config.file).exists()) {
            System.err.println("File not found.");
            System.exit(1);
        }
        
        Apriori algo = new Apriori();
        algo.run(readFile(config.file), config.support, config.threads);
    }
}
