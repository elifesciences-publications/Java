package methods;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;
import java.util.*;


public class ArrayMethods {

	public static void main(String[] args) {
		// Method Testing Area
		
		String[] tests = {"77;0,0,53,20;46;225.0;0;A;G:54;0,0,26,19;46;225.0;0;A;G:64;0,0,29,24;46;225.0;0;A;G", 
				"4;0,0,0,0;0;29.5864;0;TTCG;AG:3;0,0,0,0;0;29.5864;0;T;ATG:-------------------", 
				"-------------------:59;29,25,1,0;49;29.671;0;TGGGG;.:-------------------"};
		
		for(String test : tests) {
			System.out.println(test.matches("(.*);([A-Z]+);([A-Z]+)(.*)"));
		}
		
		

		// 
	}
	
	// Methods
	public static int estimateNumberOfPeaks(int nSearches, double[] values, int mergeDistance, 
			double propSearchesFoundInThreshold, Random random) {
		
		// Initialise an array to store the indices of the peaks identified
		ArrayList<Integer> peakIndices = new ArrayList<Integer>();
		ArrayList<Integer> peakHits = new ArrayList<Integer>();
		
		// Run the searches (hill climbing to peaks)
		for(int i = 0; i < nSearches; i++) {
			
			// Pick a random index
			int index = random.nextInt(values.length);
			
			// Initialise a variable to record whether moved
			boolean moved = true;
			
			// Search for local maxima
			while(moved) {
				
				// Get the indices on left and right
				int left = index - 1;
				int right = index + 1;
				
				// Check if can move left
				if(left >= 0 && values[left] > values[index]) {
					index = left;
				
				// Check if can move right
				}else if(right < values.length && values[right] > values[index]) {
					index = right;
					
				// If can't move finish - found peak!
				}else {
					moved = false;
				}
			}
			
			// Store the last index visited - if not too close to any other peak indices
			int indexOfClosePeak = -1;
			for(int j = 0; j < peakIndices.size(); j++) {
				if(Math.abs(peakIndices.get(j) - index) < mergeDistance) {
					indexOfClosePeak = j;
					break;
				}
			}
			if(indexOfClosePeak == -1) {
				peakIndices.add(index);
				peakHits.add(0);
			}else {
				peakHits.set(indexOfClosePeak, peakHits.get(indexOfClosePeak) + 1);
			}
		}
		
		// Count number peaks that were hit more than propSearchesFoundInThreshold * nSearches
		int count = 0;
		for(int hitCount : peakHits) {
			if(hitCount > propSearchesFoundInThreshold * nSearches) {
				count++;
			}
		}
		
		return count;
	}
	
	public static boolean[] initialise(int size, boolean value){
		boolean[] array = new boolean[size];
		for(int i = 0; i < array.length; i++){
			array[i] = value;
		}
		
		return array;
	}
	
	public static int[][] table(int[] array, boolean print){
		
		// Initialise a hashtable to record how many times each value appears
		Hashtable<Integer, Integer> countsPerValue = new Hashtable<Integer, Integer>();
		
		// Count each time each value appears
		for(int value : array){
			
			// Check if encountered
			if(countsPerValue.get(value) != null){
				countsPerValue.put(value, countsPerValue.get(value) + 1);
			}else{
				countsPerValue.put(value, 1);
			}
		}
		
		// Order the values
		int[] values = sort(HashtableMethods.getKeysInt(countsPerValue));
		
		// Get the counts
		int[] counts = new int[values.length];
		for(int i = 0; i < values.length; i++){
			counts[i] = countsPerValue.get(values[i]);
		}
		
		// If requested print an R like summary
		if(print == true){
						
			// Print the count for each value
			System.out.println(ArrayMethods.toString(values, "\t"));
			System.out.println(ArrayMethods.toString(counts, "\t"));
		}
		
		// Build an output
		int[][] output = {values, counts};
		
		return output;
	}
	
	public static char[] deletePositions(char[] array, int[] positions){
		
		// Initialise a new array to store the sequence
		char[] output = new char[array.length - positions.length];
		int pos = -1;
		for(int i = 0; i < array.length; i++){
			
			if(ArrayMethods.in(positions, i) == true){
				continue;
			}
			
			pos++;
			output[pos] = array[i];
		}
		
		return output;
	}
	public static char[] deletePositions(char[] array, Hashtable<Integer, Integer> positionsToIgnore){
		
		// Initialise a new array to store the sequence
		char[] output = new char[array.length - positionsToIgnore.size()];
		int pos = -1;
		for(int i = 0; i < array.length; i++){
			
			if(positionsToIgnore.containsKey(pos) == true){
				continue;
			}
			
			pos++;
			output[pos] = array[i];
		}
		
		return output;
	}
	
	public static boolean compare(String[] a, String[] b){
		
		// Initialise a variable to store the result
		boolean result = false;
		
		// Check if input arrays are the same length
		if(a.length == b.length){
			
			// Index the second input array
			Hashtable<String, Integer> bIndexed = HashtableMethods.indexArray(b);
			
			// Check whether all of a are present in b
			int count = 0;
			for(int i = 0; i < a.length; i++){
				if(bIndexed.get(a[i]) != null){
					count++;
				}else{
					break;
				}
			}
			
			if(count == a.length){
				result = true;
			}
		}
		
		return result;
	}
	
	public static String[] returnElementsOnlyPresentInA(String[] a, String[] b){
		
		String[] elementsOnlyInA = new String[a.length];
		int pos = -1;
		
		for(String element : a){
			if(in(b, element) == false){
				pos++;
				elementsOnlyInA[pos] = element;
			}
		}
		
		return subset(elementsOnlyInA, 0, pos);
	}
	
	public static String[] returnNotCommonElements(String[] a, String[] b){
		
		// Initialise an array to the elements of vectors a and b that aren't found in both
		String[] elementsNotInBoth = new String[a.length + b.length];
		int pos = -1;
		
		// Initialise an array to record whether an element of b was found in a
		boolean[] foundInA = repeat(false, b.length);
		
		// Initialise a variable to record whether an element was found
		boolean found = false;
		
		// Check whether any elements of a aren't present in b and record
		for(int i = 0; i < a.length; i++){
			
			// Note that have found current element of a in b yet
			found = false;
			
			// Search for current element of a in b
			for(int j = 0; j < b.length; j++){
				
				// Record if found
				if(a[i].equals(b[j])){
					found = true;
					foundInA[j] = true; // Record that current element b was found in a
				}
			}
			
			// Check if never found current element of a
			if(found == false){
				pos++;
				elementsNotInBoth[pos] = a[i];
			}
		}
				
		// Check whether any elements of b aren't present in b
		for(int i = 0; i < b.length; i++){
			if(foundInA[i] == false){
				pos++;
				elementsNotInBoth[pos] = b[i];
			}
		}
		
		return subset(elementsNotInBoth, 0, pos);
	}
	
	public static Hashtable<Integer, double[]> findKClusters(double[] array){
		
		/**
		 * Method uses kmeans clustering and maximum likelihood
		 * Instruction taken from: https://en.wikipedia.org/wiki/K-means_clustering
		 */
		
		return new Hashtable<Integer, double[]>();
	}
	
	public static char[] toChar(String[] array){
		// Array must be of characters in string format!
		char[] output = new char[array.length];
		for(int i = 0; i < array.length; i++){
			
			if(array[i].length() != 1){
				System.out.println("ERROR: Input string not a single character! " + array[i]);
			}else{
				output[i] = array[i].charAt(0);
			}
		}
		
		return(output);
	}
	
	public static char[] reverse(char[] array){
		
		char[] reversed = new char[array.length];
		
		for(int i = 0; i < array.length; i++){
			reversed[array.length - (i + 1)] = array[i];
		}
		
		return reversed;
	}
	
	public static int[] addElementToSortedArray(int[] array, int element){
		
		// Initialise a new array
		int[] newArray = new int[array.length + 1];
		int pos = -1;
		
		// Examine each element of the sorted array
		for(int i = 0; i < array.length; i++){
			
			// Increment the position in the new array
			pos++;			
			
			// Is the sorted arrays current element less than the new element or is the new element added?
			if(array[i] < element || pos != i){
				
				// Add the sorted arrays current element
				newArray[pos] = array[i];
			
			// Is the new element =< the sorted arrays current element?
			}else{
				
				// Insert the new element
				newArray[pos] = element;
				
				// Note that we've moved on one position in the new array and insert the sorted arrays current element
				pos++;
				newArray[pos] = array[i];
			}
		}
		
		// Is the new element more than all the elements of the sorted array?
		if(pos == array.length - 1){
			
			// Add it to the end of the new array
			newArray[array.length] = element;
		}
		
		return newArray;
	}
	public static double[] addElementToSortedArray(double[] array, double element){
		
		// Initialise a new array
		double[] newArray = new double[array.length + 1];
		int pos = -1;
		
		// Examine each element of the sorted array
		for(int i = 0; i < array.length; i++){
			
			// Increment the position in the new array
			pos++;			
			
			// Is the sorted arrays current element less than the new element or is the new element added?
			if(array[i] < element || pos != i){
				
				// Add the sorted arrays current element
				newArray[pos] = array[i];
			
			// Is the new element =< the sorted arrays current element?
			}else{
				
				// Insert the new element
				newArray[pos] = element;
				
				// Note that we've moved on one position in the new array and insert the sorted arrays current element
				pos++;
				newArray[pos] = array[i];
			}
		}
		
		// Is the new element more than all the elements of the sorted array?
		if(pos == array.length - 1){
			
			// Add it to the end of the new array
			newArray[array.length] = element;
		}
		
		return newArray;
	}
	
	public static double median(int[] array, boolean sorted){
		
		// Sort the array
		if(sorted == false){
			Arrays.sort(array);
		}
		
		// Return the median
		double median = -1;
		
		if(array.length % 2 == 0){
		    median = ((double)array[array.length/2] + (double)array[array.length/2 - 1])/2;
		}else{
		    median = (double) array[array.length/2];
		}
		
		return median;
	}
	
	public static Hashtable<Integer, Integer> indexArray(int[] array){
		
		// Initialise a hashtable to store the index of each element of the input array
		Hashtable<Integer, Integer> indexed = new Hashtable<Integer, Integer>();
		
		// Index each element of the input array
		for(int i = 0; i < array.length; i++){
			indexed.put(array[i], i);
		}
		
		return indexed;
	}
	public static Hashtable<String, Integer> indexArray(String[] array){
		
		// Initialise a hashtable to store the index of each element of the input array
		Hashtable<String, Integer> indexed = new Hashtable<String, Integer>();
		
		// Index each element of the input array
		for(int i = 0; i < array.length; i++){
			indexed.put(array[i], i);
		}
		
		return indexed;
	}
	
	public static double[] getBounds(double[] distribution, double proportion){
		
		double[] srtedDistribution = sort(distribution);
		
		double value = 0.5 * (1 - proportion);
		
		double lower = value * (double) distribution.length;
		double upper = (1 - value) * (double) distribution.length;
		
		double[] bounds = {srtedDistribution[(int) lower], srtedDistribution[(int) upper]};
		
		return bounds;
	}
	
	public static int[] combine(int[] a, int[] b){
		
		int[] combined = new int[a.length + b.length];
		
		for(int i = 0; i < a.length; i++){
			combined[i] = a[i];			
		}
		
		for(int i = 0; i < b.length; i++){
			combined[i + a.length] = b[i];
		}
		
		return combined;
	}
	public static String[] combine(String[] a, String[] b){
		
		String[] combined = new String[a.length + b.length];
		
		for(int i = 0; i < a.length; i++){
			combined[i] = a[i];			
		}
		
		for(int i = 0; i < b.length; i++){
			combined[i + a.length] = b[i];
		}
		
		return combined;
	}
	
	public static double[] convert2Double(String[] array){
		double[] values = new double[array.length];
		
		for(int i = 0; i < array.length; i++){
			values[i] = Double.parseDouble(array[i]);
		}
		
		return values;
	}
	public static int[] convertDouble2Int(double[] array){
		int[] values = new int[array.length];
		
		for(int i = 0; i < array.length; i++){
			values[i] = (int) array[i];
		}
		
		return values;
	}
	public static int[] convertToInteger(String[] array){
		int[] converted = new int[array.length];
		for(int i = 0; i < array.length; i++){
			converted[i] = Integer.parseInt(array[i]);
		}
		
		return converted;
	}
	
	public static int[] shuffle(int[] array, Random random) {
		
		int[] copy = ArrayMethods.copy(array);
		
		int n = copy.length;
		for (int i = 0; i < copy.length; i++) {
		    
			// Get a random index of the array past i.
		    int randomIndex = i + (int) (random.nextDouble() * (n - i));
		    
		    // Swap the random element with the present element.
		    int randomElement = copy[randomIndex];
		    copy[randomIndex] = copy[i];
		    copy[i] = randomElement;
		}
		
		return copy;
	}
	public static String[] shuffle(String[] array, Random random) {
		
		String[] copy = ArrayMethods.copy(array);
		
		int n = copy.length;
		for (int i = 0; i < copy.length; i++) {
		    
			// Get a random index of the array past i.
		    int randomIndex = i + (int) (random.nextDouble() * (n - i));
		    
		    // Swap the random element with the present element.
		    String randomElement = copy[randomIndex];
		    copy[randomIndex] = copy[i];
		    copy[i] = randomElement;
		}
		
		return copy;
	}
	public static String[] randomShuffle(String[] array, int times, Random random){
		
		String[] copy = ArrayMethods.copy(array);
		
		int index;
		int newIndex;
		String a;
		String b;
		
		for(int i = 0; i < times; i++){
			
			index = random.nextInt(array.length);
			newIndex = index;
			
			while(index == newIndex){
				newIndex = random.nextInt(array.length);
			}
			
			a = copy[index];
			b = copy[newIndex];
			
			copy[index] = b;
			copy[newIndex] = a;
		}
			
		return copy;
		
	}
	public static String[] randomPoissonShuffle(String[] array, double mean, int times){
		
		String[] copy = ArrayMethods.copy(array);
		
		// Generate a Poisson Distribution around the mean
		PoissonGenerator randomPoisson = new PoissonGenerator(mean, new Random());
		
		// Create Instance of a Random Number Generator
		Random random = new MersenneTwisterRNG();		
		
		int index;
		int newIndex;
		String a;
		String b;
		
		index = random.nextInt(array.length);
		newIndex = index;
			
		while(index == newIndex){
			newIndex = randomPoisson.nextValue();
			
			if(newIndex >= array.length){
				newIndex = index;
			}
		}
			
		a = copy[index];
		b = copy[newIndex];
		
		copy[index] = b;
		copy[newIndex] = a;
		
			
		return copy;
	}
	
	public static double variance4Beast(double[] array, double mean){
		
		double variance = 0;
		double diff = 0;
		
		for(double number : array){
			diff = number - mean;
			variance += Math.pow(diff, 2);
		}
		
		return variance / (array.length - 1); // Note the minus one for BEAST ML estimator
		
	}
	
	public static String[] subset(String[] array, int start, int end){
		String[] part = new String[end - start + 1];

		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	public static char[] subset(char[] array, int start, int end){
		char[] part = new char[end - start + 1];
		
		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	public static int[] subset(int[] array, int start, int end){
		int[] part = new int[end - start + 1];
		
		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	public static double[] subset(double[] array, int start, int end){
		double[] part = new double[end - start + 1];
		
		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	public static long[] subset(long[] array, int start, int end){
		long[] part = new long[end - start + 1];
		
		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	public static double[][] subset(double[][] array, int start, int end){
		double[][] part = new double[end - start + 1][array[0].length];
		
		for(int index = start; index <= end; index++){
			
			part[index - start] = array[index];
		}
		
		return part;
	}
	
	public static String[] getValues(String[] array, int[] indices){
		String[] output = new String[indices.length];
		int pos = -1;
		
		for(int index : indices){
			pos++;
			output[pos] = array[index];
		}
		
		return output;
	}
	public static String[] getValues(String[] array, boolean[] keep){
		String[] output = new String[array.length];
		int pos = -1;
		
		for(int i = 0; i < array.length; i++){
			
			if(keep[i] == true){
				pos++;
				output[pos] = array[i];
			}			
		}
		
		return subset(output, 0, pos);
	}
	
	public static double[] sort(double[] array){
		
		double[] srtdArray = copy(array);
		
		/**
		 * This Method Uses the Bubble Sort Algorithm
		 * 		Described here: http://en.wikipedia.org/wiki/Bubble_sort
		 * 
		 * 	For each element, compare it to the next element. If it is larger than the next element, swap the elements.
		 * 	Do this for each element of the list (except the last). Continue to iterate through the list elements and
		 *  make swaps until no swaps can be made.
		 */
		
		double a;
		double b;
		
		int swappedHappened = 1;
		while(swappedHappened == 1){ // Continue to compare the List elements until no swaps are made
		
			int swapped = 0;
			for(int index = 0; index < array.length - 1; index++){
				
				// Compare Current Element to Next Element
				if(srtdArray[index] > srtdArray[index + 1]){
					
					// Swap Current Element is Larger
					a = srtdArray[index];
					b = srtdArray[index + 1];
					
					srtdArray[index] = b;
					srtdArray[index + 1] = a;
					
					// Record that a Swap occurred
					swapped++;
				}
			}
			
			// Check if any swaps happened during the last iteration - if none then finished
			if(swapped == 0){
				swappedHappened = 0;
			}
		}
		
		return srtdArray;
	}
	public static int[] sort(int[] array){
		
		int[] srtdArray = copy(array);
		
		/**
		 * This Method Uses the Bubble Sort Algorithm
		 * 		Described here: http://en.wikipedia.org/wiki/Bubble_sort
		 * 
		 * 	For each element, compare it to the next element. If it is larger than the next element, swap the elements.
		 * 	Do this for each element of the list (except the last). Continue to iterate through the list elements and
		 *  make swaps until no swaps can be made.
		 */
		
		int swappedHappened = 1;
		while(swappedHappened == 1){ // Continue to compare the List elements until no swaps are made
		
			int swapped = 0;
			for(int index = 0; index < array.length - 1; index++){
				
				// Compare Current Element to Next Element
				if(srtdArray[index] > srtdArray[index + 1]){
					
					// Swap Current Element is Larger
					int a = srtdArray[index];
					int b = srtdArray[index + 1];
					
					srtdArray[index] = b;
					srtdArray[index + 1] = a;
					
					// Record that a Swap occurred
					swapped++;
				}
			}
			
			// Check if any swaps happened during the last iteration - if none then finished
			if(swapped == 0){
				swappedHappened = 0;
			}
		}
		
		return srtdArray;
	}
	public static int[] getOrder(int[] array){
		
		int[] srtdArray = copy(array);
		int[] orderedIndices = seq(0, array.length - 1, 1);
		
		/**
		 * This Method Uses the Bubble Sort Algorithm
		 * 		Described here: http://en.wikipedia.org/wiki/Bubble_sort
		 * 
		 * 	For each element, compare it to the next element. If it is larger than the next element, swap the elements.
		 * 	Do this for each element of the list (except the last). Continue to iterate through the list elements and
		 *  make swaps until no swaps can be made.
		 */
		
		int a;
		int b;
		int aIndex;
		int bIndex;
		
		int swappedHappened = 1;
		while(swappedHappened == 1){ // Continue to compare the List elements until no swaps are made
		
			int swapped = 0;
			for(int index = 0; index < array.length - 1; index++){
				
				// Compare Current Element to Next Element
				if(srtdArray[index] > srtdArray[index + 1]){
					
					// Swap Current Element is Larger
					a = srtdArray[index];
					b = srtdArray[index + 1];
					
					srtdArray[index] = b;
					srtdArray[index + 1] = a;
					
					aIndex = orderedIndices[index];
					bIndex = orderedIndices[index + 1];
					orderedIndices[index] = bIndex;
					orderedIndices[index + 1] = aIndex;					
					
					// Record that a Swap occurred
					swapped++;
				}
			}
			
			// Check if any swaps happened during the last iteration - if none then finished
			if(swapped == 0){
				swappedHappened = 0;
			}
		}
		
		return orderedIndices;
	}

	public static int[] orderArray(int[] array, int[] order){
		
		int[] orderedArray = new int[array.length];
		
		for(int i = 0; i < order.length; i++){
			orderedArray[i] = array[order[i]];
		}
		
		return orderedArray;
	}
	public static String[] orderArray(String[] array, int[] order){
		
		String[] orderedArray = new String[array.length];
		
		for(int i = 0; i < order.length; i++){
			orderedArray[i] = array[order[i]];
		}
		
		return orderedArray;
	}
	public static char[] orderArray(char[] array, int[] order){
		
		char[] orderedArray = new char[array.length];
		
		for(int i = 0; i < order.length; i++){
			orderedArray[i] = array[order[i]];
		}
		
		return orderedArray;
	}
	
	public static char[] randomWeightedChoice( char[][] array, double[] weights, Random random){
		
		/** Convert Integer Weights into Weights which can be used
		 * (1, 2, 4, 6, 4, 2, 1) => 1/20 = 0.05 => multiply and sum => (0.05, 0.15, 0.35, 0.65, 0.85, 0.95, 1)
		 * Previous and Current Define bounds in Bin e.g. 0.05 <= y < 0.15
		 */
		
		double value = 0;
		for(double weight : weights){
			value += weight;
		}
	
		value = 1 / value; // Value by which to multiply each weight
		double[] actualWeights = new double[array.length];
		
		double previous = 0;
		for(int index = 0; index < array.length; index++){
			
			double calculatedWeight  = (weights[index] * value) + previous; // Weights move towards 1
			actualWeights[index] = calculatedWeight;
			previous = calculatedWeight;
		
		}
	
		double y = random.nextDouble();
		int pos = -99;
		for(int index = 0; index < array.length; index++){
		
			if(y < actualWeights[index]){
				pos = index;
				break;
			}
		}
	
		return array[pos];
	}
	public static int randomWeightedChoice( int[] array, double[] weights, Random random){
		
		/** Convert Integer Weights into Weights which can be used
		 * (1, 2, 4, 6, 4, 2, 1) => 1/20 = 0.05 => multiply and sum => (0.05, 0.15, 0.35, 0.65, 0.85, 0.95, 1)
		 * Previous and Current Define bounds in Bin e.g. 0.05 <= y < 0.15
		 */
	
		double value = 0;
		for(double weight : weights){
			value += weight;
		}
	
		value = 1 / value; // Value by which to multiply each weight
		double[] actualWeights = new double[array.length];
		
		double previous = 0;
		for(int index = 0; index < array.length; index++){
			
			double calculatedWeight  = (weights[index] * value) + previous; // Weights move towards 1
			actualWeights[index] = calculatedWeight;
			previous = calculatedWeight;
		
		}
	
		double y = random.nextDouble();
		int pos = 99;
		for(int index = 0; index < array.length; index++){
		
			if(y < actualWeights[index]){
				pos = index;
				break;
			}
		}
	
		return array[pos];
	}
	public static int[] randomWeightedChoices(int [] array, double[] weights, Random random,
			int nChoices, int replacement){
		
		if(array.length != weights.length){
			System.out.println("ERROR: ArrayMethods.randomWeightedChoices: The input array lengths do not match.");
		}
		
		// Initialise an array to store the random choices
		int[] choices = new int[nChoices];
		int chosenIndex;
		
		// Make the random choices
		for(int i = 0; i < nChoices; i++){
			
			chosenIndex = randomWeightedIndex(weights, random);
			choices[i] = array[chosenIndex];
			
			// Remove the choice if replacement is OFF
			if(replacement == 0){
				weights[chosenIndex] = 0;
			}
		}
		
		return choices;
	}
	public static int randomWeightedIndex(double[] weights, Random random){
		
		/** Convert Integer Weights into Weights which can be used
		 * (1, 2, 4, 6, 4, 2, 1) => 1/20 = 0.05 => multiply and sum => (0.05, 0.15, 0.35, 0.65, 0.85, 0.95, 1)
		 * Previous and Current Define bounds in Bin e.g. 0.05 <= y < 0.15
		 */
	
		int pos = -1;
		if(weights.length > 1){
		
			double value = 0;
			for(double weight : weights){
				value += weight;
			}
	
			value = 1 / value; // Value by which to multiply each weight
			double[] actualWeights = new double[weights.length];
		
			double previous = 0;
			for(int index = 0; index < weights.length; index++){
			
				double calculatedWeight  = (weights[index] * value) + previous; // Weights move towards 1
				actualWeights[index] = calculatedWeight;
				previous = calculatedWeight;
		
			}
	
			double y = random.nextDouble();
			pos = 99;
			for(int index = 0; index < weights.length; index++){
				
				if(y < actualWeights[index]){
					pos = index;
					break;
				}
			}
		}else if(weights.length == 1){
			pos = 0;
		}else{
			System.out.println("Error: Weights for Random Weighted choice has length 0");
		}
	
		return pos;
	}
	
	public static int find(int[] array, int element){
		
		int pos = -1;
		for(int x : array){
			pos++;
			if(x == element){
				break;
			}
		}
		
		return pos;
	}
	public static int find(char[] array, char element){
		
		int pos = -1;
		for(int x : array){
			pos++;
			if(x == element){
				break;
			}
		}
		
		return pos;
	}
	public static int find(String[] array, String element){
		
		int pos = -1;
		for(String x : array){
			pos++;
			if(x.equals(element)){
				break;
			}
		}
		
		return pos;
	}
	public static int found(int[] array, int element){
		
		int found = 0;
		
		for(int x : array){
			if(x == element){
				found = 1;
				break;
			}
		}
		
		return found;
	}
	public static boolean in(String[] array, String element){
		
		boolean found = false;
		
		for(String x : array){
			if(x.equals(element)){
				found = true;
				break;
			}
		}
		
		return found;
	}
	public static boolean in(int[] array, int element){
		
		boolean found = false;
		
		for(int x : array){
			if(x == element){
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	public static double[] oneMinusElements(double[] array){
		double[] newArray = new double[array.length];
		
		for(int i = 0; i < array.length; i++){
			newArray[i] = 1 - array[i];
		}
		
		return newArray;
	}
	
	public static int[] deletePosition(int[] array, int position){
		int[] newArray = new int[array.length - 1];
		
		int pos = -1;
		for(int index = 0; index < array.length; index++){
			if(index != position){
				pos++;
				newArray[pos] = array[index];
			}
		}
		
		return newArray;
	}
	public static String[] deletePosition(String[] array, int position){
		String[] newArray = new String[array.length - 1];
		
		int pos = -1;
		for(int index = 0; index < array.length; index++){
			if(index != position){
				pos++;
				newArray[pos] = array[index];
			}
		}
		
		return newArray;
	}
	public static int[] deleteElement(int[] array, int element){
		
		// This method will remove all the occurrences of this element
		int count = 0;
		for(int x : array){
			if(element == x){
				count++;
			}
		}
		
		int[] newArray = new int[array.length - count];
		
		int pos = -1;
		for(int x : array){
			if(x != element){
				pos++;
				newArray[pos] = x;
			}
		}
		
		return newArray;
	}
	
	public static char[] append(char[] array, char value){
		char[] newArray = new char[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[newArray.length - 1] = value;
		
		return newArray;
	}
	public static int[] append(int[] array, int value){
		int[] newArray = new int[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[array.length] = value;
		
		return newArray;
	}
	public static long[] append(long[] array, long value){
		long[] newArray = new long[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[newArray.length - 1] = value;
		
		return newArray;
	}

	public static double[] append(double[] array, double value){
		double[] newArray = new double[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[newArray.length - 1] = value;
		
		return newArray;
	}
	public static String[] append(String[] array, String value){
		String[] newArray = new String[array.length + 1];
		
		for(int index = 0; index < array.length; index++){
			newArray[index] = array[index];
		}
		newArray[newArray.length - 1] = value;
		
		return newArray;
	}
	public static String[] append(String[] array1, String[] array2){
		String[] array = new String[array1.length + array2.length];
		for(int i = 0; i < array1.length; i++){
			array[i] = array1[i];
		}
		for(int i = 0; i < array2.length; i++){
			array[i + array1.length] = array2[i];
		}
		
		return array;
	}

	public static int sum(int[] array){
		int total = 0;
		for(int element : array){
			total += element;
		}
		
		return total;
	}
	public static double sum(double[] array){
		double total = 0;
		for(double element : array){
			total += element;
		}
		
		return total;
	}
	
	public static int min(int[] array){
		
		int min = array[0];
		for(int index = 0; index < array.length; index++){
			if(array[index] < min){
				min = array[index];
			}
		}
		
		return min;
	}
	public static double min(double[] array){
		
		double min = array[0];
		for(int index = 0; index < array.length; index++){
			if(array[index] < min){
				min = array[index];
			}
		}
		
		return min;
	}
	public static int[] findMins(double[] array){
		
		int[] minPositions = new int[0];
		
		// Find the Min Value
		double min = array[0];
		for(int index = 0; index < array.length; index++){
			if(array[index] < min){
				min = array[index];
			}
		}
		
		// Find all occurrences of the Min Value
		for(int index = 0; index < array.length; index++){
			if(array[index] == min){
				minPositions = ArrayMethods.append(minPositions, index);
			}
		}
		
		return minPositions;
	}
	public static int[] findMins(int[] array){
		
		int[] minPositions = new int[0];
		
		// Find the Min Value
		int min = array[0];
		for(int index = 0; index < array.length; index++){
			if(array[index] < min){
				min = array[index];
			}
		}
		
		// Find all occurrences of the Min Value
		for(int index = 0; index < array.length; index++){
			if(array[index] == min){
				minPositions = ArrayMethods.append(minPositions, index);
			}
		}
		
		return minPositions;
	}
	
	public static double[] findMinMaxAndMean(double[] array) {
		
		// Initialise variables for recording the min, max and mean
		double min = array[0];
		double max = array[0];
		double sum = array[0];
		
		// Examine each number
		for(int i = 1; i < array.length; i++) {
			
			// Check for new min
			if(array[i] < min) {
				min = array[i];
			}
			
			// Check for new max
			if(array[i] > max) {
				max = array[i];
			}
			
			// Continue calculating sum
			sum += array[i];
		}
		
		// Finish calculating mean
		double mean = sum / (double) array.length;
		
		// Create the output
		double[] output = {min, mean, max};
		
		return output;
	}
	public static double[] findMinMaxAndMean(int[] array) {
		
		// Initialise variables for recording the min, max and mean
		double min = array[0];
		double max = array[0];
		double sum = array[0];
		
		// Examine each number
		for(int i = 1; i < array.length; i++) {
			
			// Check for new min
			if(array[i] < min) {
				min = array[i];
			}
			
			// Check for new max
			if(array[i] > max) {
				max = array[i];
			}
			
			// Continue calculating sum
			sum += array[i];
		}
		
		// Finish calculating mean
		double mean = sum / (double) array.length;
		
		// Create the output
		double[] output = {min, mean, max};
		
		return output;
	}
	
	public static int[] findMaxs(int[] array){
		
		int max = array[0];
		for(int i = 0; i < array.length; i++){
			if(array[i] > max){
				max = array[i];
			}
		}
		
		// Find all occurrences of the Min Value
		int[] maxPositions = new int[array.length];
		int pos = -1;
		for(int i = 0; i < array.length; i++){
			if(array[i] == max){
				pos++;
				maxPositions[pos] = i;
			}
		}
				
		
		return subset(maxPositions, 0, pos);
	}
	
	public static double max(double[] array){
		
		double max = array[0];
		for(double element : array){
			if(element > max){
				max = element;
			}
		}
		
		return max;
	}
	public static int max(int[] array){
		
		int max = array[0];
		for(int element : array){
			if(element > max){
				max = element;
			}
		}
		
		return max;
	}

	public static double mean(int[] array){
		
		double total = 0;
		for(double element : array){
			total += element;
		}
		
		return total / (double) array.length;
	}
	public static double mean(double[] array){
		
		double total = 0;
		for(double element : array){
			total += element;
		}
		
		return total / (double) array.length;
	}
	
	public static int[] repeat(int value, int times){
		
		int[] array = new int[times];
		
		for(int index = 0; index < times; index++){
			array[index] = value;
		}
		
		return array;
	}
	public static double[] repeat(double value, int times){
		
		double[] array = new double[times];
		
		for(int index = 0; index < times; index++){
			array[index] = value;
		}
		
		return array;
	}
	public static String[] repeat(String value, int times){
		
		String[] array = new String[times];
		
		for(int index = 0; index < times; index++){
			array[index] = value;
		}
		
		return array;
	}
	public static boolean[] repeat(boolean value, int times){
		
		boolean[] array = new boolean[times];
		
		for(int index = 0; index < times; index++){
			array[index] = value;
		}
		
		return array;
	}
	public static char[] repeat(char value, int times){
		
		char[] array = new char[times];
		
		for(int index = 0; index < times; index++){
			array[index] = value;
		}
		
		return array;
	}
	
	public static int[] range(int[] array){
		
		int[] output = {999999999, -999999999};
		for(int value : array){
			
			if(value < output[0]){
				output[0] = value;
			}
			
			if(value > output[1]){
				output[1] = value;
			}
		}
		
		return output;
	}
	
	public static int[] seq(int start, int end, int step){
		
		int arrayLength = (end - start) / step; // Note automatically resolves to integer - using FLOOR - works out well in this method
		int[] array = new int[arrayLength + 1]; // Note 1 bigger
		
		array[0] = start;
		for(int index = 1; index < array.length; index++){
			array[index] = array[index - 1] + step;
		}
		
		return array;
	}
	public static double[] seq(double start, double end, double step){
		
		int arrayLength = (int) ((end - start) / step); // Note automatically resolves to integer - using FLOOR - works out well in this method
		double[] array = new double[arrayLength + 1]; // Note 1 bigger
		
		double value = start;
		for(int index = 0; index < array.length; index++){
			array[index] = value;
			
			value += step;
		}
		
		return array;
	}

	public static int randomChoice( int[] array, Random random){
		
		int randomIndex = random.nextInt(array.length);
		
		return array[randomIndex];
	}
	public static char randomChoice( char[] array, Random random){
		
		int randomIndex = random.nextInt(array.length);
		
		return array[randomIndex];
	}
	
	public static int[] randomChoices(int[] array, int n, Random random, boolean replacement){
		
		// Copy the array
		int[] arrayCopy = ArrayMethods.copy(array);
		
		// Initialise an array to store the randomly chosen indices
		int[] chosen = new int[n];
		
		// Check that there are enough numbers to be chosen (if no replacement)
		if(replacement == false && n >= arrayCopy.length){
			chosen = arrayCopy;
		}else{
			
			// Randomly pick n indices
			int index;
			for(int i = 0; i < n; i++){
				index = random.nextInt(arrayCopy.length - 1);
				
				chosen[i] = arrayCopy[index];
				
				if(replacement == false){
					arrayCopy = deletePosition(arrayCopy, index);
				}
			}
		}		
				
		return chosen;
	}
	public static int[] randomIndices(int length, int replacement, int n, Random random){
				
		// Create an array of indices
		int[] indices = seq(0, length - 1, 1);
		
		// Initialise an array to store the randomly chosen indices
		int[] chosen = new int[n];
		
		// Random pick n indices
		int index;
		for(int i = 0; i < n; i++){
			
			index = random.nextInt(indices.length - 1);
			
			chosen[i] = indices[index];
			
			if(replacement == 0){
				indices = deletePosition(indices, index);
			}
		}
		
		return chosen;
	}
	public static String[] randomChoices(String[] array, int n, Random random, boolean replacement){
		
		
		
		// Copy the array
		String[] arrayCopy = ArrayMethods.copy(array);
		
		// Initialise an array to store the randomly chosen indices
		String[] chosen = new String[n];
		
		// Check that there are enough numbers to be chosen (if no replacement)
		if(replacement == false && n >= arrayCopy.length){
			chosen = arrayCopy;
		}else{
			
			// Randomly pick n indices
			int index;
			for(int i = 0; i < n; i++){
				index = random.nextInt(arrayCopy.length - 1);
				
				chosen[i] = arrayCopy[index];
				
				if(replacement == false){
					arrayCopy = deletePosition(arrayCopy, index);
				}
			}
		}
				
		return chosen;
	}
	
	public static int randomIndex(int length, Random random){
		return random.nextInt(length);
	}
	
	public static int[] copy(int[] array){
		int[] copy = new int[array.length];
		
		for(int index = 0; index < array.length; index++){
			copy[index] = array[index];
		}
		
		return copy;
	}
	public static double[] copy(double[] array){
		double[] copy = new double[array.length];
		
		for(int index = 0; index < array.length; index++){
			copy[index] = array[index];
		}
		
		return copy;
	}
	public static String[] copy(String[] array){
		String[] copy = new String[array.length];
		
		for(int index = 0; index < array.length; index++){
			copy[index] = array[index];
		}
		
		return copy;
	}
	public static char[] copy(char[] array){
		char[] copy = new char[array.length];
		
		for(int index = 0; index < array.length; index++){
			copy[index] = array[index];
		}
		
		return copy;
	}
	
	public static int[] unique(int[] array){
		int[] newArray = {};
		Hashtable<Integer, Boolean> used = new Hashtable<Integer, Boolean>();
		
		for(int index = 0; index < array.length; index++){
			
			if(used.get(array[index]) == null){
				newArray = append(newArray, array[index]);
				used.put(array[index], true);
			}
		}
		
		return newArray;
	}
	public static String[] unique(String[] array){
		String[] newArray = {};
		Hashtable<String, Boolean> used = new Hashtable<String, Boolean>();
		
		for(int index = 0; index < array.length; index++){
			
			if(used.get(array[index]) == null){
				newArray = append(newArray, array[index]);
				used.put(array[index], true);
			}
		}
		
		return newArray;
	}

	public static void print(String[] array, String sep){
		System.out.print(array[0]);
		for(int index = 1; index < array.length; index++){
			System.out.print(sep + array[index]);
		}
		System.out.println();
	}
	public static void print(char[] array, String sep){
		System.out.print(array[0]);
		for(int index = 1; index < array.length; index++){
			System.out.print(sep + array[index]);
		}
		System.out.println();
	}
	public static void print(int[] array, String sep){
		System.out.print(array[0]);
		for(int index = 1; index < array.length; index++){
			System.out.print(sep + array[index]);
		}
		System.out.println();
	}	
	public static void print(double[] array, String sep){
		System.out.print(array[0]);
		for(int index = 1; index < array.length; index++){
			System.out.print(sep + array[index]);
		}
		System.out.println();
	}

	public static String toString(char[] array){
		StringBuilder string = new StringBuilder();
		
		for(int i = 0; i < array.length; i++){
			string.append(array[i]);
		}
		
		return string.toString();
	}
	public static String toString(String[] array, String sep){
		String string = array[0];
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + array[i];
		}
		
		return string;
	}
	public static String toString(boolean[] array, String sep){
		String string = "" + array[0];
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + array[i];
		}
		
		return string;
	}
	public static String toString(double[] array, String sep){
		String string = Double.toString(array[0]);
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + Double.toString(array[i]);
		}
		
		return string;
	}
	public static String toString(int[] array, String sep){
		String string = Integer.toString(array[0]);
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + Integer.toString(array[i]);
		}
		
		return string;
	}
	public static String toString(char[] array, String sep){
		String string = array[0] + "";
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + array[i];
		}
		
		return string;
	}	
	public static String toString(long[] array, String sep){
		String string = array[0] + "";
		
		for(int i = 1; i < array.length; i++){
			string = string + sep + array[i];
		}
		
		return string;
	}
	
	public static double multiply(int[] a, double[] b){
		double result = 0;
		if(a.length != b.length){
			System.out.println("Input Arrays are not the Correct Length: " + a.length + "\t" + b.length);
		}else{
			for(int i = 0; i < a.length; i++){
				result += a[i] * b[i];
			}
		}
		
		return result;
	}
	public static double[] divide(double[] a, double[] b){
		double[] result = new double[a.length];
		if(a.length != b.length){
			System.out.println("Input Arrays are not the Correct Length: " + a.length + "\t" + b.length);
		}else{
			for(int i = 0; i < a.length; i++){
				result[i] = a[i] / b[i];
			}
		}
		
		return result;
	}
	public static double[] divide(double[] array, double value){
		double[] result = new double[array.length];

		for(int i = 0; i < array.length; i++){
			result[i] = array[i] / value;
		}
	
		return result;
	}

	public static double[] add(double[] a, double[] b){
		
		double[] result = new double[a.length];
		
		if(a.length != b.length){
			System.out.println("ERROR: methods.add - Arrays are not the same length");
		}else{
			
			for(int i = 0; i < a.length; i++){
				result[i] = a[i] + b[i];
			}
		}
		
		return result;
	}
	public static int[] add(int[] a, int[] b){
		
		int[] result = new int[a.length];
		
		if(a.length != b.length){
			System.out.println("ERROR: methods.add - Arrays are not the same length");
		}else{
			
			for(int i = 0; i < a.length; i++){
				result[i] = a[i] + b[i];
			}
		}
		
		return result;
	}
	public static double[] add(double[] a, int[] b){
		
		double[] result = new double[a.length];
		
		if(a.length != b.length){
			System.out.println("ERROR: methods.add - Arrays are not the same length");
		}else{
			
			for(int i = 0; i < a.length; i++){
				result[i] = a[i] + (double) b[i];
			}
		}
		
		return result;
	}
}
