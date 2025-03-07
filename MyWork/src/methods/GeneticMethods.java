package methods;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import geneticDistances.GeneticDistances;
import geneticDistances.Sequence;
import woodchesterGeneticVsEpi.CompareIsolates;

public class GeneticMethods {

	public static void main(String[] args) throws IOException{
		
	
	}

	public static char[] getReverseCompliment(char[] sequence, boolean verbose){
		
		// Initialise an array to store the reverse compliment
		char[] reverseCompliment = new char[sequence.length];
		
		// Note the compliment of each nucleotide
		Hashtable<Character, Character> nucleotideCompliment = new Hashtable<Character, Character>();
		nucleotideCompliment.put('A', 'T');
		nucleotideCompliment.put('C', 'G');
		nucleotideCompliment.put('G', 'C');
		nucleotideCompliment.put('T', 'A');
		
		// Examine each character in the sequence in reverse direction
		int pos = -1;
		for(int i = sequence.length - 1; i >= 0; i--){
			pos++;
			
			if(nucleotideCompliment.get(sequence[i]) != null){
				reverseCompliment[pos] = nucleotideCompliment.get(sequence[i]);
			}else{
				reverseCompliment[pos] = 'N';
				if(verbose){
					System.out.println("ERROR: non-nucleotide present in alignment: " + sequence[i]);
				}				
			}
		}
		
		return reverseCompliment;
	}
	
	public static double calculatePropNs(char[] sequence){
		
		double value = 0;
		
		for(int i = 0; i < sequence.length; i++){
			if(sequence[i] == 'N'){
				value++;
			}
		}
		
		return value/ (double)sequence.length;
	}
	
	public static int calculateNumberDifferencesBetweenSequences(char[] a, char[] b){
		
		// Assumes the sequences are the same length
		if(a.length != b.length){
			System.out.println("ERROR!: GeneralMethods:calculateGeneticPDistance: Sequences are not the same Length");
		}
		
		int count = 0;
		for(int i = 0; i < a.length; i++){
			if(a[i] != b[i] && a[i] != 'N' && b[i] != 'N'){
				count++;
			}
		}
		
		return count;
	}
	
	public static Sequence[] readFastaFile(String fileName) throws IOException{
		
		/**
		 * FASTA file structure:
		 * 		220 3927
		 *		>WB98_S53_93.vcf
		 *		GGGCCTCTNNNCTTCAATACCCCCGATACAC
		 *		>WB99_S59_94.vcf
		 *		GGGCCTCTNNNNTTCAATACCCCCGATACAC
		 *		... 
		 * 
		 */
		
		// Open the Sequence Fasta File for Reading
    	InputStream input = new FileInputStream(fileName);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	
    	// Initialise Variables to Store the Sequence Information
    	String isolateName = "";
    	String sequence = "";
    	Sequence[] sequences = new Sequence[0];
    	int pos = -1;
    	
    	int noSamples;
    	int noNucleotides = -1;
    	
    	// Begin Reading the Fasta File
    	String line = null;
    	String[] parts;
    	while(( line = reader.readLine()) != null){
    		
    		// Read the Header Information
    		if(line.matches("(^[0-9])(.*)")){
    			parts = line.split(" ");
    			
    			noSamples = Integer.parseInt(parts[0]);
    			noNucleotides = Integer.parseInt(parts[1]);
    			
    			sequences = new Sequence[noSamples];
    		
    		// Deal with the Isolate Sequences
    		}else if(line.matches("(^>)(.*)")){
    			
    			// Store the previous Sequence
    			if(isolateName != ""){
    				
    				pos++;
    				sequences[pos] = new Sequence(isolateName, sequence.toCharArray());
    			}
    			
    			// Get the current isolates Information
    			isolateName = line.substring(1);
    			sequence = "";
    		
    		// Store the isolates sequence
    		}else{
    			
    			sequence = sequence + "" + line;
       		}  		
    	}
		reader.close();
		
		// Store the last isolate
		pos++;
		sequences[pos] = new Sequence(isolateName, sequence.toCharArray()); 
		
		return Sequence.subset(sequences, 0, pos);
	}
	
	public static Sequence[] readFastaFile(String fileName, boolean verbose) throws IOException{
		
		/**
		 * FASTA file structure:
		 * 		220 3927
		 *		>WB98_S53_93.vcf
		 *		GGGCCTCTNNNCTTCAATACCCCCGATACAC
		 *		>WB99_S59_94.vcf
		 *		GGGCCTCTNNNNTTCAATACCCCCGATACAC
		 *		... 
		 * 
		 */
		
		if(verbose == true){
			System.out.println("Reading fasta file: " + fileName + "...");
		}
		
		// Open the Sequence Fasta File for Reading
    	InputStream input = new FileInputStream(fileName);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	
    	// Initialise Variables to Store the Sequence Information
    	String isolateName = "";
    	StringBuilder sequence = new StringBuilder();
    	Sequence[] sequences = new Sequence[999];
    	int pos = -1;
    	
    	int noSamples = -1;
    	
    	// Begin Reading the Fasta File
    	String line = null;
    	String[] parts;
    	while(( line = reader.readLine()) != null){
    		
    		// Read the Header Information
    		if(line.matches("(^[0-9])(.*)")){
    			parts = line.split(" ");
    			
    			noSamples = Integer.parseInt(parts[0]);
    			
    			sequences = new Sequence[noSamples];
    		
    		// Deal with the Isolate Sequences
    		}else if(line.matches("(^>)(.*)")){
    			
    			// Store the previous Sequence
    			if(isolateName != ""){
    				
    				pos++;
    				Sequence.append(sequences, pos, new Sequence(isolateName, sequence.toString().toCharArray()));
    			}
    			
    			// Get the current isolates Information
    			isolateName = line.substring(1);
    			sequence = new StringBuilder();
    		
    		// Store the isolates sequence
    		}else{
    			
    			sequence.append(line);
       		}  		
    	}
		reader.close();
		
		// Store the last isolate
		pos++;
		Sequence.append(sequences, pos, new Sequence(isolateName, sequence.toString().toCharArray()));
		
		if(noSamples == -1){
			sequences = Sequence.subset(sequences, 0, pos);
		}
		
		return sequences;
	}
	
	public static int calculateNumberDifferencesBetweenSequences(char[] a, char[] b, boolean[] informative){
		
		// Assumes the sequences are the same length
		if(a.length != b.length){
			System.out.println("ERROR!: GeneralMethods:calculateGeneticPDistance: Sequences are not the same Length");
		}
		
		int count = 0;
		for(int i = 0; i < a.length; i++){
			if(a[i] != b[i] && a[i] != 'N' && b[i] != 'N' && informative[i] == true){
				count++;
			}
		}
		
		return count;		
	}

	public static int[][] createGeneticDistanceMatrix(Sequence[] sequences){
		
		// Initialise the genetic distance matrix - NOTE using just count of differences
		int[][] distances = new int[sequences.length][sequences.length];
		int distance;
				
		// Compare each isolate to one another
		for(int i = 0; i < sequences.length; i++){
			
			for(int j = 0; j < sequences.length; j++){
				
				// Only make comparison once and skip self comparisons
				if(i >= j){
					continue;
				}
				
				// Calculate the genetic distance
				distance = GeneticMethods.calculateNumberDifferencesBetweenSequences(sequences[i].getSequence(), sequences[j].getSequence());
				
				// Store the calculate genetic distance
				distances[i][j] = distance;
				distances[j][i] = distance;
			}
		}
		
		return distances;
	}

	public static char[] consensus(char[][] sequences){
		
		// Initialise an array to store the consensus allele at each position
		char[] consensus = new char[sequences[0].length];
		
		// Initialise an array to count the alleles present at each position
		int[] counts = new int[5];
		char[] alleles = {'A', 'C', 'G', 'T', 'N'};
		Hashtable<Character, Integer> allelePositions = HashtableMethods.indexArray(alleles);	
		
		// Initialise an array to record which alleles are most supported
		int[] maxAlleles;
		
		// Examine each position in the alignment
		for(int pos = 0; pos < sequences[0].length; pos++){
			
			// Reset the counts of the alleles at the current position
			counts = new int[5]; 
			
			// Count the alleles present amongst the isolates
			for(int i = 0; i < sequences.length; i++){
				counts[allelePositions.get(sequences[i][pos])] = counts[allelePositions.get(sequences[i][pos])] + 1;
			}
			
			// Find the maximum values
			maxAlleles = ArrayMethods.findMaxs(counts);
			
			// Check if multiple maximum values present
			if(maxAlleles.length > 1){
				consensus[pos] = 'N';
			}else{
				consensus[pos] = alleles[maxAlleles[0]];
			}			
		}
		
		return(consensus);
	}
	
}
