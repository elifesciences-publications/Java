package ComparingGenomes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class GenbankFile {
	
	private String fileName;
	private AnnotationSet[] annotationSets = new AnnotationSet[0]; // Note genbank file can have multiple annotated sequences
	
	public GenbankFile(String file, Hashtable<String, Integer> annotationTypes, boolean verbose) throws IOException{
		
		// Store the file name
		this.fileName = file;
		
		// Read the genbank file
		readGenbankFile(file, annotationTypes, verbose);
		
		// Print some statistics if requested
		if(verbose){
			System.out.println("Found " + this.annotationSets.length + " set(s) of annotations");
			for(int i = 0; i < this.annotationSets.length; i++){
				System.out.println("Set " + (i + 1) + " (" + this.annotationSets[i].getSource() + ")" + " had " + this.annotationSets[i].getAnnotations().size() + " annotations");
			}
			System.out.println();			
		}
	}
	
	// General methods
	private void readGenbankFile(String file, Hashtable<String, Integer> annotationTypes, boolean verbose) throws IOException{
		
		// Open the animals table file
		InputStream input = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		// Initialise a variables to parse the file
		String line = null;
		String[] parts;
		
		// Initialise a variable to store the annotation information and sequence
		String source;
		AnnotationSet annotations = null;
		boolean foundSequence = false;
		StringBuilder sequence = new StringBuilder();
												
		// Begin reading the file
		while(( line = reader.readLine()) != null){			
			
			// Search for SOURCE line
			if(line.matches("(^)DEFINITION(.*)") == true){
				source = line.split("  +")[1];
				
				// Initialise a new annotation set
				annotations = new AnnotationSet(source);
				continue;
			}
			
			// Split the current line into parts
			parts = line.split("( +)");
			
			// Check if found new annotation
			if(parts.length == 3 && parts[2].matches("(.*)\\.\\.(.*)") == true && annotationTypes.get(parts[1]) != null){
				
				// Store the annotation information
				annotations.addAnnotation(parts[2], new Annotation(parts[2], parts[1]));
				continue;
			}
			
			// Check if found sequence
			if(foundSequence == false && line.matches("ORIGIN(.*)")){
				foundSequence = true;
				continue;
			}
			
			// Check if reached end of sequence
			if(foundSequence == true && line.matches("//")){
				foundSequence = false;
				
				// Store the sequence
				annotations.setSequence(sequence.toString());
				
				// Extract the sequence for each annotation
				annotations.extractAnnotationSequences(verbose);
				
				// Store the annotations
				this.annotationSets = AnnotationSet.append(annotationSets, annotations);
				
				// Reset the sequence
				sequence = new StringBuilder();
				
				continue;
			}
			
			// Build sequence until end if reached
			if(foundSequence == true){
				for(int i = 2; i < parts.length; i++){
					sequence.append(parts[i].toUpperCase());
				}
			}						
		}
		
		// Close the input file
		input.close();
		reader.close();		
	}
	
	// Getting methods
	public String getFileName() {
		return this.fileName;
	}
	public AnnotationSet getAnnotationSet(int index){
		return this.annotationSets[index];
	}
	public int getNumberAnnotationSets(){
		return this.annotationSets.length;
	}
}
