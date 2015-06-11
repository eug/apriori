
package com.github.eug.apriori;

public class Config {
    public String file = "";
    public int support = 0;
    public int threads = 40;
    
    public static Config parse(String[] args) {
        Config config = new Config();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-f": config.file = args[i + 1]; break;
                case "-s": config.support = Integer.parseInt(args[i + 1]); break;
                case "-t": config.threads = Integer.parseInt(args[i + 1]); break;
            }
        }
        return config;
    }
}
