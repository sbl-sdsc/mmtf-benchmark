#!/bin/bash
WORKDIR=$AF_YEAST_v4
NUM_REPS=5
BIOJAVA_JAR=$MMTF_BENCHMARK/target/mmtf-benchmark-1.0-SNAPSHOT.jar
MULTIPLIER=1000 # coordinate precision <= 1/1000 = 0.001 A

for i in $(seq 1 $NUM_REPS); do
    java -cp $BIOJAVA_JAR edu.sdsc.benchmark.CompressBatch $WORKDIR/cif $WORKDIR/mmtf_lossless/compressed/ $MULTIPLIER > $WORKDIR/log/mmtf_lossless_compression_$i.txt
    java -cp $BIOJAVA_JAR edu.sdsc.benchmark.DecompressBatch $WORKDIR/mmtf_lossless/compressed/ $WORKDIR/mmtf_lossless/decompressed/ > $WORKDIR/log/mmtf_lossless_decompression_$i.txt
done
