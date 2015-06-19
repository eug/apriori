# A-Priori
A naïve implementation of A-Priori algorithm using threads. More details about this algorithm can be found at on Ch. 6, [Mining Massive Datasets (2ed)](http://www.mmds.org/)

# Dataset
Some datasets to play around can be found at [Frequent Itemset Mining Dataset Repository](http://fimi.ua.ac.be/data/)

# Build
``` mvn install ```

# Run
``` java -cp target/apriori-1.0-SNAPSHOT.jar com.github.eug.apriori.Main -f chess.dat -s 2500 ```
