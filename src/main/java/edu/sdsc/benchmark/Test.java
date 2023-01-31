package edu.sdsc.benchmark;

import java.io.IOException;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.StructureTools;


public class Test{
    public static void main(String[] args) throws Exception {
           Structure structure = StructureIO.getStructure("4HHB");
           // and let's print out how many atoms are in this structure
           System.out.println(StructureTools.getNrAtoms(structure));
    }   
}
