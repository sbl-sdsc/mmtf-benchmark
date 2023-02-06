package edu.sdsc.benchmark;

import edu.sdsc.benchmark.PdbAtomIO;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public class DecompressBatch {
    public static void main(String[] args) throws Exception {
        // Get args
        // args[0] = input directory that contains mmtf.gz files
        // args[1] = output directory that will contain pdb files
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        // Get all files in input directory
        File[] files = input.toFile().listFiles();

        // Loop through all files
        for (File file : files) {
            // Get file name
            String fileName = file.getName();
            String outputFileName = fileName.replace(".mmtf.gz", ".pdb");
            // Get input file path
            Path inputFilePath = Paths.get(input.toString(), fileName);
            // Get output file path
            Path outputFilePath = Paths.get(output.toString(), outputFileName);
            // Measure running time
            long endTime;
            long startTime = System.nanoTime();
            // Handle exceptions
            try {
                // Read mmtf.gz file
                StructureDataInterface structure = readMmtf(inputFilePath);
                // Write pdb file
                PdbAtomIO.writeAtoms(structure, outputFilePath);
                endTime = System.nanoTime();
                System.out.println(fileName + "\t" + (endTime - startTime) / 1000000000.0);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(fileName+ "\t" + "NA");
            }
        }
    }
    public static StructureDataInterface readMmtf(Path inputFilePath) throws IOException {
	byte[] array = Files.readAllBytes(inputFilePath);
	array = ReaderUtils.deflateGzip(array);
	ByteArrayInputStream stream = new ByteArrayInputStream(array);
	MmtfStructure mmtf = ReaderUtils.getDataFromInputStream(stream);
        stream.close();
	StructureDataInterface structure = new GenericDecoder(mmtf);
        return structure;
   }
}
