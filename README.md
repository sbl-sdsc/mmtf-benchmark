# mmtf-benchmark

Work in progres ...
 
## Installation

1. Install Java 11
2. Download [Maven])(https://maven.apache.org/install.html)
3. Clone this repo

```git clone https://github.com/sbl-sdsc/mmtf-benchmark```

4. Set environment variable to location of mmtf-benchmark

```export MMTF_BENCHMARK=<path_to_repo>```

5. Set environment variable to AF_YEAST_v4 directory

```export AF_YEAST_v4=<path_to_directory>```

6. Download Saccharomyces cerevisiae proteome from AlphaFold database version 4 

```wget https://ftp.ebi.ac.uk/pub/databases/alphafold/```
latest/UP000002311_559292_YEAST_v4.tar

7. Copy the .cif.gz files into the $AF_YEAST_v4/cif directory

6. Build the jar file with dependencies

```mvn install```

7. Run the benchmarks scripts in $MMTF_BENCHMARK/src/main/java/edu/sdsc/benchmark

```
 ./mmtf_lossless.sh
 ./mmtf_lossy.sh
```

