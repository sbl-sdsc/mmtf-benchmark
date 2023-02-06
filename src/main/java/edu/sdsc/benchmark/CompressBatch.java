package edu.sdsc.benchmark;

import org.biojava.nbio.structure.chem.ChemCompGroupFactory;
import org.biojava.nbio.structure.chem.ReducedChemCompProvider;
import org.biojava.nbio.structure.chem.DownloadChemCompProvider;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.cif.CifStructureConverter;
import org.biojava.nbio.structure.io.mmtf.MmtfStructureWriter;
import org.rcsb.mmtf.api.StructureAdapterInterface;
import org.rcsb.mmtf.serialization.MessagePackSerialization;
import org.rcsb.mmtf.encoder.GenericEncoder;
import org.rcsb.mmtf.encoder.AdapterToStructureData;
import org.rcsb.mmtf.encoder.WriterUtils;

import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;


// Adopted from https://github.com/steineggerlab/foldcomp-analysis/blob/main/benchmark/scripts/bcif/CompressBatch.java

public class CompressBatch {
    public static void main(String[] args) throws Exception {
        // Get args
        // args[0] = input directory that contains cif.gz files
        // args[1] = output directory that will contain mmtf.gz files
        // args[2] = multiplier to applied to coordinates for lossy compression. 
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        // Get all files in input directory
        File[] files = input.toFile().listFiles();

        // The build-in Chemical Component Definitions contains 
        // the most frequent used chemical components. It does not guarantee a correct
        // representation, but it is fast and does not require network access.
        // see: https://github.com/biojava/biojava-tutorial/blob/master/structure/chemcomp.md
        // ChemCompGroupFactory.setChemCompProvider(new ReducedChemCompProvider());

        // For the general case, the DownlaodChemCompProvider() downloads Chemical Components
        // over the network. This may be slower but works for every PDB file produced by wwwPDB.
        ChemCompGroupFactory.setChemCompProvider(new DownloadChemCompProvider());

        int multiplier = Integer.parseInt(args[2]); // coordinate multiplier for lossy compression

        // Loop through all files
        for (File file : files) {
            // Get file name
            String fileName = file.getName();
            String outputFileName = fileName.replace(".cif.gz", ".mmtf.gz");
            // Get input file path
            Path inputFilePath = Paths.get(input.toString(), fileName);
            // Get output file path
            Path outputFilePath = Paths.get(output.toString(), outputFileName);
            // Measure running time
            long endTime;
            long startTime = System.nanoTime();
            // Handle exceptions
            try {
                // Read cif file
                Structure structure = CifStructureConverter.fromPath(inputFilePath);
                // Write mmtf file
                writeToFile(structure, multiplier, outputFilePath);
                // may need to use this to add gzip compression
                endTime = System.nanoTime();
                System.out.println(fileName + "\t" + (endTime - startTime) / 1000000000.0);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println(fileName+ "\t" + "NA");
            }
        }
    }

    private static void writeToFile(Structure structure, int multiplier, Path path) throws IOException {
        // Set up this writer
        AdapterToStructureData writerToEncoder = new AdapterToStructureData();
	// Get the writer - this is what people implement
	new MmtfStructureWriter(structure, writerToEncoder);
	// Now write this data to file
	writeDataToFile(writerToEncoder, multiplier, path);
    }

    private static void writeDataToFile(AdapterToStructureData writerToEncoder, int multiplier, Path path) throws IOException {
	byte[] byteArray = getDataAsByteArr(writerToEncoder, multiplier);
	OutputStream fos = Files.newOutputStream(path); 
	fos.write(byteArray);
	fos.close();
    }

    private static byte[] getDataAsByteArr(AdapterToStructureData writerToEncoder, int multiplier) throws IOException {
	MessagePackSerialization mmtfBeanSeDerializerInterface = new MessagePackSerialization();
	GenericEncoder genericEncoder = new GenericEncoder(writerToEncoder, multiplier);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	mmtfBeanSeDerializerInterface.serialize(genericEncoder.getMmtfEncodedStructure(), bos);
        return WriterUtils.gzipCompress(bos.toByteArray());
    }		
}
