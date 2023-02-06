#!/bin/bash
WORKDIR=$AF_YEAST_v4
NUM_REPS=5
BIOJAVA_JAR=$MMTF_BENCHMARK/target/mmtf-benchmark-1.0-SNAPSHOT.jar
MULTIPLIER=6 # coordinate precision <= 1/6 = 0.166 A -> max error: 0.083 A, ave error: 0.042 A

for i in $(seq 1 $NUM_REPS); do
    java -cp $BIOJAVA_JAR edu.sdsc.benchmark.CompressBatch $WORKDIR/cif $WORKDIR/mmtf_lossy/compressed/ $MULTIPLIER > $WORKDIR/log/mmtf_lossy_compression_$i.txt
    java -cp $BIOJAVA_JAR edu.sdsc.benchmark.DecompressBatch $WORKDIR/mmtf_lossy/compressed/ $WORKDIR/mmtf_lossy/decompressed/ > $WORKDIR/log/mmtf_lossy_decompression_$i.txt
done
