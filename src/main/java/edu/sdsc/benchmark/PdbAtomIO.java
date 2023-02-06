package edu.sdsc.benchmark;

import org.rcsb.mmtf.api.StructureDataInterface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PdbAtomIO { 
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat FLOAT_2 = new DecimalFormat("0.00", SYMBOLS);
    private static final DecimalFormat FLOAT_3 = new DecimalFormat("0.000", SYMBOLS);
    /**
     * Traverses the MMTF data structure and writes PDB ATOM records
     * 
     * @param structure structure to be traversed
     */
    public static void writeAtoms(StructureDataInterface structure, Path path) throws IOException {
	// create an index that maps a chain to its entity
	int[] chainToEntityIndex = getChainToEntityIndex(structure);

	// Global indices that point into the flat (columnar) data structure
	int chainIndex = 0;
	int groupIndex = 0;
	int atomIndex = 0;

        StringBuilder builder = new StringBuilder();

	// Loop over models
	for (int i = 0; i < structure.getNumModels(); i++) {

    	    // Loop over chains in a model
	    for (int j = 0; j < structure.getChainsPerModel()[i]; j++) {
		String chainName = structure.getChainNames()[chainIndex]; // this is the chain name used in pdb files
		// String chainId = structure.getChainIds()[chainIndex]; // this is also called asym_id in mmCIF		
		int groups = structure.getGroupsPerChain()[chainIndex];
		String entityType = structure.getEntityType(chainToEntityIndex[chainIndex]);		

		// Loop over groups in a chain
		for (int k = 0; k < structure.getGroupsPerChain()[chainIndex]; k++) {
		    int groupId = structure.getGroupIds()[groupIndex]; // aka residue number
	            char insertionCode = structure.getInsCodes()[groupIndex];

		    // Unique groups (residues) are stored only once in a dictionary. 
		    // We need to get the group type to retrieve group information
		    int groupType = structure.getGroupTypeIndices()[groupIndex];	

	            // retrieve group info from dictionary
	   	    String groupName = structure.getGroupName(groupType);
		    String chemCompType = structure.getGroupChemCompType(groupType);
                    String recordName = "ATOM  ";
                    if (chemCompType.equals("NON-POLYMER"))
                        recordName = "HETATM";

		    // Loop over atoms in a group retrieved from the dictionary
		    for (int m = 0; m < structure.getNumAtomsInGroup(groupType); m++) {
			int atomId = structure.getAtomIds()[atomIndex];
			char altLocId = structure.getAltLocIds()[atomIndex];
			float x = structure.getxCoords()[atomIndex];
			float y = structure.getyCoords()[atomIndex];
			float z =structure.getzCoords()[atomIndex]; 
			float occupancy = structure.getOccupancies()[atomIndex];
			float bFactor = structure.getbFactors()[atomIndex];

		        // get group specific atom info from the group dictionary
			String atomName = structure.getGroupAtomNames(groupType)[m];
			String element = structure.getGroupElementNames(groupType)[m];

                        // format data for PDB
                        element = element.toUpperCase();
                        atomName = formatAtomName(atomName, element);
                        if (altLocId == Character.MIN_VALUE)
                            altLocId = ' ';
                        if (insertionCode == Character.MIN_VALUE)
                            insertionCode = ' ';

                        // build ATOM record
                        builder.append(recordName);
                        appendRightJustified(builder, String.valueOf(atomId), 5);
                        builder.append(" ");
                        builder.append(atomName);
                        builder.append(altLocId);
                        appendRightJustified(builder, groupName, 3);
                        builder.append(" ");
                        builder.append(chainName);
                        appendRightJustified(builder, String.valueOf(groupId), 4);
                        builder.append("   ");
                        builder.append(insertionCode);
                        appendRightJustified(builder, FLOAT_3.format(x), 8);
                        appendRightJustified(builder, FLOAT_3.format(y), 8);
                        appendRightJustified(builder, FLOAT_3.format(z), 8);
                        appendRightJustified(builder, FLOAT_2.format(occupancy), 6);
                        appendRightJustified(builder, FLOAT_2.format(bFactor), 6);
                        builder.append("          ");
                        appendRightJustified(builder, element, 2); 
                        builder.append("\n");

			atomIndex++;
		    }
		    groupIndex++;
 	        }
	        chainIndex++;
	    }
        }
        Files.writeString(path, builder.toString());
    }

     /**
      * Returns an array that maps a chain index to an entity index.
      * @param structureDataInterface structure to be traversed
      * @return index that maps a chain index to an entity index
      */
    private static int[] getChainToEntityIndex(StructureDataInterface structure) {
        int[] entityChainIndex = new int[structure.getNumChains()];

	for (int i = 0; i < structure.getNumEntities(); i++) {
	    for (int j: structure.getEntityChainIndexList(i)) {
		entityChainIndex[j] = i;
	    }
	}
	return entityChainIndex;
    }
    private static void appendRightJustified(StringBuilder builder, String val, int width) {
        int padding = width - val.length();
        for (int i = 0; i < padding; i++)
            builder.append(" ");
        builder.append(val);
    }
    private static String formatAtomName(String name, String element) {
	// RULES FOR ATOM NAME PADDING: 4 columns in total: 13, 14, 15, 16

        String fullName = null;
        // if length 4: nothing to do
	if (name.length() == 4)
	   fullName = name;

	// if length 3: they stay at 14
        else if (name.length() == 3)
	   fullName = " " + name;

	// for length 2 it depends:
	//    carbon, oxygen, nitrogen, sulfur, phosphorous stay at column 14
	//    elements with 2 letters (e.g. NA, FE) will go to column 13
	else if (name.length()==2) {
	    if (element.equals("C") || element.equals("N") || element.equals("O") || 
                element.equals("S") || element.equals("P"))
	       fullName = " " + name + " ";
            else
	      fullName = name + "  ";
	}

	// for length 1 (e.g. K but also C, O) they stay in column 14
	else if (name.length() == 1)
	    fullName = " " + name + "  ";

	return fullName;
    }
}
