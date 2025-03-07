package ComparingGenomes;

import java.util.Hashtable;

import methods.HashtableMethods;

public class AnnotationSet {

	private String source;
	private Hashtable<String, Annotation> annotations;
	private String sequence;
	
	public AnnotationSet(String name){
		
		this.source = name;
		this.annotations = new Hashtable<String, Annotation>();
	}
	
	// General methods
	public void extractAnnotationSequences(boolean verbose){
		
		// Initialise a variable to store the annotation information
		int start;
		int end;
		
		// Examine each of the annotations
		for(String key : HashtableMethods.getKeysString(this.annotations)){
			
			// Get the start and end coordinates of the current annotated element
			start = this.annotations.get(key).getStart();
			end = this.annotations.get(key).getEnd();
			
			// Extract the nucleotides for the current annotation
			this.annotations.get(key).setSequence(this.sequence.substring(start-1, end), verbose);
		}
	}
	
	public void addAnnotation(String coords, Annotation annotation){
		
		this.annotations.put(coords, annotation);
	}
	
	public static AnnotationSet[] append(AnnotationSet[] array, AnnotationSet value){
		
		AnnotationSet[] newArray = new AnnotationSet[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[newArray.length - 1] = value;
		
		return newArray;
	}
	
	public void removeSequence(){
		this.sequence = null;
	}
	
	// Getting methods
	public Hashtable<String, Annotation> getAnnotations(){
		return this.annotations;
	}
	public String getSource(){
		return this.source;
	}
	public String getSequence(){
		return this.sequence;
	}
	
	// Setting methods
	public void setSequence(String sequence){
		this.sequence = sequence;
	}	
}
