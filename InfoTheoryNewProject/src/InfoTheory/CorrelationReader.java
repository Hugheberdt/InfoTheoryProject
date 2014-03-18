package InfoTheory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/*
 * TODO: Fix what happens if correlation does not give any next char options
 * TODO: Clean-up up initial text.
 * TODO: Facilitate graphing
 * TODO: Write code that calculates correlation measures
 * TODO: Maybe: Make seeding the next character in generated text faster by calculating cumsum only once.
 * 
 */
public class CorrelationReader {
	
	//The maximum length of correlations registered. May affect memory.
	private double[] corrInfos;
	private String[] uniqueCharacters;
	private int nUniqueCharacters;
	private int[] uniqueCharacterFreqs;
	private HashMap<String,int[]> allCorrelationsMap;
	private HashMap<String,double[]> allCondProbMap;
	private HashMap<String, Double> allSequencesProbMap;
	private double probTolerance;
	
	/*
	 * Initialise the reader with a maximum correlation depth
	 */
	
	public CorrelationReader() {
		
		corrInfos = new double[0];
		uniqueCharacters = new String[0];
		uniqueCharacterFreqs = new int[0];
		nUniqueCharacters = 0;
		allCorrelationsMap = new HashMap<String,int[]>();
		allCondProbMap = new HashMap<String,double[]>();
		allSequencesProbMap = new HashMap<String,Double>();
		probTolerance = Math.pow(2, -166);
		
	}

	public static double log2(double number) {
		
		return ( Math.log(number) / Math.log(2) );
		
	}

	
	public void generateTextStatistics(String textAsString, int inputMaxCorrDepth, 
			int correlationInfoDepth) {
		
		//Read all the unique characters and store in uniqueCharacters
		storeUniqueCharsNFreqs(textAsString);
		
		//Default minimum correlation depth. Higher doesn't really make sense.
		int textMinCorrDepth = 0;
		
		//Register all conditional frequencies.		
		registerMultiDepthCorrs(textAsString, textMinCorrDepth, inputMaxCorrDepth);
		
		//Compute all conditional probabilities
		computeCondProbabilities();
		
		//Compute the sequence probabilities
		computeSeqProb(textAsString, inputMaxCorrDepth);
		
		//Compute the correlation informations
		computeCorrInfo(correlationInfoDepth);
		
		System.out.println("Checkpoint");
		
	}
	
	public void printCorrelationInfo() {
		
		int nComputedCorrs = corrInfos.length;
		
		double totalCorrInfo = 0.0;
		
		for (int i = 0; i < nComputedCorrs; i++) {
			
			System.out.println("Corr info at level: " + i + " is: " + corrInfos[i]);
			
			totalCorrInfo = totalCorrInfo + corrInfos[i];
			
		}
		
		System.out.println("Total correlation information is: " + totalCorrInfo);
		System.out.println("Log2(alphabetSize) is: " + log2(nUniqueCharacters));
		System.out.println("Entropy SHOULD be: " + (log2(nUniqueCharacters) - totalCorrInfo));
		
	}
	
	public String buildText(String startingSnippet, int maxCorrelation, int maxLetters) {
		
		int currentLength = 0;
		
		//Add the initial text to the string builder
		StringBuilder genTextBuilder = new StringBuilder(startingSnippet);
		
		while ( currentLength < maxLetters ) {
			
			//If the starting snippet is shorter than the maxCorrelation, make a shorter
			// substring
			
			int currentCorrelationDepth = 0;
			
			int idealLastIndexToCutFrom = genTextBuilder.length() - maxCorrelation;
			
			int lastIndexToCutFrom = 0;
			
			if (idealLastIndexToCutFrom < 0) {
				
				//Remains 0
				lastIndexToCutFrom = 0;
				
				currentCorrelationDepth = genTextBuilder.length();
				
			} else {
				
				lastIndexToCutFrom = idealLastIndexToCutFrom;
				
				currentCorrelationDepth = maxCorrelation;
				
			}
			
			String lastfewLetters = genTextBuilder.substring(lastIndexToCutFrom);
			
//			System.out.println("Last few letters: " + lastfewLetters);
			
			String nextCharacter = sampleNextChar(currentCorrelationDepth, lastfewLetters);
			
			genTextBuilder.append(nextCharacter);
			
			currentLength = currentLength + 1;
			
		}
		
		String theGeneratedText = genTextBuilder.toString();
		
		return theGeneratedText;
		
	}
	
	private String sampleNextChar(int maxHindsight, String generatedText) {
		
		int lastCharIndex = generatedText.length();
		int firstLookbackIndex = lastCharIndex - maxHindsight;
		boolean nextCharacterFound = false;
		
//		System.out.println("The first lookback index: " + firstLookbackIndex);
		
		while (!nextCharacterFound) {
			
			if (firstLookbackIndex >= 0) {
				
				String charSeq = generatedText.substring(firstLookbackIndex, lastCharIndex);
				
				if (allCorrelationsMap.containsKey(charSeq)) {
					
					int[] charFreqList = allCorrelationsMap.get(charSeq);
					
					String nextCharacter = sampleCharPropToFreq(charFreqList);
					
					nextCharacterFound = true;
					
					return nextCharacter;
					
				} else if (firstLookbackIndex != lastCharIndex) {
					
					//Decrease the depth for which correlations are sought
					firstLookbackIndex = firstLookbackIndex + 1;
					
				} else {
					
					System.out.println("Generated a density information character");
					
					//Draw a letter from the density information!
					String nextCharacter = sampleCharPropToFreq(uniqueCharacterFreqs);
					
					nextCharacterFound = true;
					
					return nextCharacter;
					
				}
				
			} else {
				
				System.out.println("Initial text length incompatible with correlation depth");
				return null;
				
			}
			
		}
		
		return null;
		
	}
	
	/*
	 * TODO: Has some overhead for the density information case, but might not matter 
	 */
	private String sampleCharPropToFreq(int[] charFreqList) {
		
		ArrayList<String> validCharacters = new ArrayList<String>();
		ArrayList<Integer> nonZeroFrequencies = new ArrayList<Integer>();
		
		//Pick out only the characters that have non-zero frequencies
		for (int i = 0; i < charFreqList.length; i++) {
			
			if (charFreqList[i] != 0) {
				
				validCharacters.add(abIndex2Letter(i));
				nonZeroFrequencies.add(charFreqList[i]);
				
			}
			
		}
		
		//Compute the cumulative sum of character frequencies
		int totalSum = 0;
		int[] cumulativeSum = new int[nonZeroFrequencies.size()];
		for (int i = 0; i < nonZeroFrequencies.size(); i++) {
			
			int previousValue = nonZeroFrequencies.get(i);
			
			cumulativeSum[i] = previousValue + totalSum;
			
			totalSum = cumulativeSum[i];
			
		}
		
		int maxValue = cumulativeSum[cumulativeSum.length - 1];
//		System.out.println("Maxval in cumsum: " + maxValue);
		
//		System.out.println("The cumsum: ");
		
//		for (int i = 0; i < cumulativeSum.length; i++ ) {
//			
//			System.out.println(cumulativeSum[i]);
//			
//		}
		
		//By default
		int randomInteger = 1;
		
		//IF there is more than 1 element
		if (maxValue > 1) {
			
			Random tmpRandGen = new Random();
			
			//Note: maxValue is NOT inclusive
			randomInteger = tmpRandGen.nextInt(maxValue) + 1;
			
		}
		
//		System.out.println("Random integer " + randomInteger);
		
		int chosenIndex = -1;
		for (int i = 0; i < cumulativeSum.length; i++) {
			
			if (randomInteger <= cumulativeSum[i]){ 
				
				chosenIndex = i;
				break;
				
			}
			
		}
		
//		System.out.println("Chosen index: " + chosenIndex);
		
		String nextCharacter = validCharacters.get(chosenIndex);
		
		return nextCharacter;
		
	}
	
	private void registerMultiDepthCorrs(String textAsString, int minDepth, int maxDepth) {
		
		int depth = minDepth;
		
		while (depth <= maxDepth ) {
			
			registerConditionalAtDepth(textAsString, depth);
			
			depth = depth + 1;
			
		}
		
	}

	public void displayCorrArray(String charSeq) {
		
		int[] corrArray = allCorrelationsMap.get(charSeq);
		
		System.out.println("The key: " + charSeq);
		
		if (corrArray != null) {
		
			for (int i = 0; i < corrArray.length; i++) {
				
				System.out.println("Index: " + i + " value: " + corrArray[i] 
						+ " Letter: " + abIndex2Letter(i));
				
			}
			
		} else {
			
			System.out.println("The requested character sequence is not registered");
			
		}
	}
	
	private String abIndex2Letter(int alphabetIndex) {
		
		return uniqueCharacters[alphabetIndex];
		
	}
	
	private int letter2abIndex(String letter) {
		
//		System.out.println("Letter to search for: " + letter);
		
		for (int i = 0; i < nUniqueCharacters; i++) {
			
//			System.out.println("Unique char: " + uniqueCharacters[i] + ", index" + i);
			
			if (letter.equals(uniqueCharacters[i])) {
				
				return i;
				
			}
			
		}
		
		//The letter wasn't found. ADD SOME EXCEPTION. Now hopefully creates an error later.
		
		System.out.println("Found no corresponding index for a certain letter");
		
		return nUniqueCharacters + 10;
		
	}
	
	/*
	 * Make note of all conditional frequencies for character sequences of depth
	 * correlation depth.
	 */
	private void registerConditionalAtDepth(String textAsString, int correlationDepth){
		
		int textLength = textAsString.length();
		
		//There are no conditional probabilities for the next characters 
		if (correlationDepth == 0) {
			
			return;
			
		}
		
		if (uniqueCharacters.length == 0) {
			
			System.out.println("All unique characters have not yet been registered");
			
		}
		
		//Loop through the text
		for (int i = 0; i < textLength - correlationDepth; i++) {
			
			//-1 because one must look at the following letter. 
			String charSeq = textAsString.substring(i, i + correlationDepth);
			String nextChar = textAsString.substring(i + correlationDepth, i + correlationDepth + 1);
			
			//Get the index of the letter whose frequency should be increased
			int letterIndex = letter2abIndex(nextChar);
			
			if (! allCorrelationsMap.containsKey(charSeq)){
				
				//Make new array filled with zeros
				int[] newIndexArray = new int[nUniqueCharacters];
				Arrays.fill(newIndexArray, 0);
			
				newIndexArray[letterIndex] = 1;
				
				allCorrelationsMap.put(charSeq, newIndexArray);
				
			} else {
				
				int[] freqArrayToModify = allCorrelationsMap.get(charSeq);
				
				freqArrayToModify[letterIndex] = freqArrayToModify[letterIndex] + 1;
				
				//Not sure if this is necessary
				allCorrelationsMap.put(charSeq, freqArrayToModify);
				
			}
			
		}
		
	}
	
	/*
	 * Store the all unique characters in the input text
	 */
	private void storeUniqueCharsNFreqs(String textAsString) {
		
		int predictedAlphabetSize = 32;
		
		HashMap<Character,Integer> uniqueCharsNDensInfo =
				new HashMap<Character,Integer>(predictedAlphabetSize);
		
		for (int i = 0; i < textAsString.length(); i++) {
			
			char currentChar = textAsString.charAt(i);
			
			if (uniqueCharsNDensInfo.containsKey(currentChar)) {
				
				uniqueCharsNDensInfo.put(currentChar,
						uniqueCharsNDensInfo.get(currentChar) + 1);
				
			} else {
				
				uniqueCharsNDensInfo.put(currentChar, 1);
				
			}
			
		}
		
		//Take all the keys of this map and make into an array
		Set<Character> allKeys = uniqueCharsNDensInfo.keySet();
		Character[] tmpUniqueCharacters = (Character[]) allKeys.toArray(new Character[0]);
		
		this.nUniqueCharacters = tmpUniqueCharacters.length;
		
		//Convert the character array to a string array
		this.uniqueCharacters = new String[nUniqueCharacters];
		
		for (int i = 0; i<nUniqueCharacters; i++) {
			
			uniqueCharacters[i] = tmpUniqueCharacters[i].toString();
			
		}
		
		this.uniqueCharacterFreqs = new int[nUniqueCharacters];
		
		//Use the old Character map to get the character frequencies
		for (int i = 0; i < nUniqueCharacters; i++) {
			uniqueCharacterFreqs[i] = uniqueCharsNDensInfo.get(tmpUniqueCharacters[i]);
		}
		
	}
	
	public String generateRandomText(int targetLength) {
		
		StringBuilder textBuilder = new StringBuilder();
		
		int currentLength = 0;
		
		Random randomGen = new Random();
		
		while (currentLength < targetLength) {
			
			int randIndex = randomGen.nextInt(nUniqueCharacters);
			String randomNextLetter = uniqueCharacters[randIndex];
			
			textBuilder.append(randomNextLetter);
			
			currentLength = currentLength + 1; 
			
		}
		
		return textBuilder.toString();
		
	}
	
	private void computeCondProbabilities() {
		
		//Get all keys in the correlations map
		Set<String> allCharFreqs = allCorrelationsMap.keySet();
		
		Iterator<String> keyIter = allCharFreqs.iterator();
		
		while (keyIter.hasNext()) {
			
			String tmpKey = keyIter.next();
			int[] tmpFreqArray = allCorrelationsMap.get(tmpKey);
			double[] probArray = new double[nUniqueCharacters];
			
			//Sum the total number of frequencies
			double sum = 0;
			for (int i = 0; i < nUniqueCharacters; i++) {
				
				sum = sum + tmpFreqArray[i];
				
			}
			
			//Compute the relative frequencies
			for (int i = 0; i < nUniqueCharacters; i++) {
				
				probArray[i] = tmpFreqArray[i]/sum;
				
			}
			
			allCondProbMap.put(tmpKey, probArray);
			
		}
		
	}

	private void computeCorrInfo(int depthM) {
		
		//The array to hold all correlation informations. Update with the known depth.
		corrInfos = new double[depthM + 1];
		
		//TODO: FIX CORRELATION INFORMATION! QUICKLY! 
		
		//Initialise with 0:s
		Arrays.fill(corrInfos, 0);
		
		//Iterate over all elements in allCondProbMap
		Set<String> korrInfoKeys = allCondProbMap.keySet();
		Iterator<String> korrInfoItr = korrInfoKeys.iterator();
		
		while (korrInfoItr.hasNext()) {
			
			String charSeq = korrInfoItr.next();
			double[] charProbs = allCondProbMap.get(charSeq);
			
			int charSeqLength = charSeq.length();
			
			if (charSeqLength > depthM) {
				
				//Skip the current character sequence, because it is too long to consider.
				continue;
				
			} else if (charSeqLength == 1) {
				
				//If the sequence is length 1, correlation info is density info.
				
				double tmpCharFreq = allSequencesProbMap.get(charSeq);
				
				double densityInfoContribution = 
						tmpCharFreq*log2(tmpCharFreq*((double) nUniqueCharacters)); 
				
				//Add on the density information
				corrInfos[0] = corrInfos[0] + densityInfoContribution;
				
				continue;
				
			}
			
			//For conditional probability on string without the first element
			String shortenedCharSeq = charSeq.substring(1);
			double[] shortenedCharProbs = allCondProbMap.get(shortenedCharSeq);
			
			double probPrecedingSeq = allSequencesProbMap.get(charSeq);
			
			double tmpCorrInfo = 0.0;
			
			for (int i = 0; i < charProbs.length; i++) {
				
				if (Math.abs(charProbs[i]) > probTolerance) {
					
//					if (charProbs[i] > shortenedCharProbs[i]) {
//
//						System.out.println("charProbs greater than shorter charprobs");
//
//					}
					
					double logFraction = charProbs[i]/shortenedCharProbs[i];
					
					double leLogarithm = CorrelationReader.log2(logFraction);
					
					if (Math.abs(logFraction - 1) > probTolerance) {
						
						double newCorrInfo = probPrecedingSeq*charProbs[i]*leLogarithm;
					
						tmpCorrInfo = tmpCorrInfo + newCorrInfo;
						
					} else {
						
						//DO not add anything, the logarithm is considered to be 0
						
					}
					
				} else {
					
					//Add nothing to tempCorrInfo. Probability assumed to be 0.
					
				}
				
			}
			
			corrInfos[charSeqLength - 1] = corrInfos[charSeqLength - 1] + tmpCorrInfo;
			
		}
		
	}
	
	/*
	 * Compute the probabilities for sequences of all lengths up until maxSeqLength
	 */
	private void computeSeqProb(String textAsString, int maxSeqLength) {
		
		HashMap<String,Integer> seqFreqs = new HashMap<String, Integer>();
		int textLength = textAsString.length();
		
		//Repeat for all correlation lengths. Could skip iLen=1 and use density info.
		//But marginal difference.
		for (int iLen = 1; iLen <= maxSeqLength; iLen++) {
		
			//Iterate over all characters in the text in chunks of currSeqLength
			for (int i = 0; i <= textLength - iLen; i++) {
				
				String charSeq = textAsString.substring(i, i + iLen);
				
				if (seqFreqs.containsKey(charSeq) ) {
					
					seqFreqs.put(charSeq, seqFreqs.get(charSeq) + 1);
					
				} else {
					
					seqFreqs.put(charSeq,1);
					
				}
				
			}
			
		}
		
		//Now that all frequencies are registered, compute the sum of frequencies for all
		// sequences of the same length.
		int[] sumsVector = new int[maxSeqLength];
		
		Set<String> allSeqFreqKeys = seqFreqs.keySet();
		
		Iterator<String> seqFreqItr = allSeqFreqKeys.iterator();
		
		while (seqFreqItr.hasNext()) {
			
			String tmpSeq = seqFreqItr.next();
			int tmpFreq = seqFreqs.get(tmpSeq);
			int seqLength = tmpSeq.length();
			
			//Shift due to indexing starting at 0
			sumsVector[seqLength - 1] = sumsVector[seqLength - 1] + tmpFreq; 
			
		}
		
		//Execute the divisions
		Iterator<String> seqFreqItrDivStage = allSeqFreqKeys.iterator();
		
		while (seqFreqItrDivStage.hasNext()) {
			
			String tmpSeqDivStage = seqFreqItrDivStage.next();
			int tmpFreqDivStage = seqFreqs.get(tmpSeqDivStage);
			int seqLengthDivStage = tmpSeqDivStage.length();
			
			double tmpProb = ((double) tmpFreqDivStage)/
					((double) sumsVector[seqLengthDivStage - 1]);  
			
			allSequencesProbMap.put(tmpSeqDivStage, tmpProb);
			
		}
		
		int finalCheck=1;
		
	}

	
	public boolean writeStringToFile(String textToWrite, String fileName) {
		
		try {
			
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(textToWrite); // stores in RAM first
			outputStream.flush();
			outputStream.close(); // flushes the data to the file
			System.out.println("Text successfully written to: " + fileName);
			
		} catch (FileNotFoundException e) {
			
			System.out.println("Text could not be written to: " + fileName);
			e.printStackTrace();
			return false;
			
		}
		
		return true;
		
	}
	
	public HashMap<String,Integer> getRankSumFrequency(String textAsString) {
		
		//Only calculate the absolute frequencies
		HashMap<String,Integer> wordFrequencyMap = new HashMap<String,Integer>();
		
		//Get an array of strings of words
		String[] wordsInText = textAsString.split("\\s");
		
		//Store all the frequencies of unique words
		for (int i = 0; i < wordsInText.length; i++) {
			
			String wordToCheck = wordsInText[i];
			
			if (! wordFrequencyMap.containsKey(wordToCheck)) {
				
				//Set the frequency of the new word to 1
				wordFrequencyMap.put(wordToCheck, 1);
				
			} else {
				
				int updatedFrequency = wordFrequencyMap.get(wordToCheck) + 1;
				
				wordFrequencyMap.put(wordToCheck, updatedFrequency);
				
			}
			
		}
		
		return wordFrequencyMap;
		
	}
	
	public void writeWordsAndFrequenciesToFile(
			HashMap<String,Integer> wordFrequencyMap,
			String wordFilePath, String frequencyFilePath) {
		
		int nUniqueWords = wordFrequencyMap.size();
		
		//Get all keys in the map
		Set<String> wordsKeys = wordFrequencyMap.keySet();
		
		//Cast the key set of the map to an array of strings
		String[] wordsStringList = (String[]) wordsKeys.toArray(new String[0]);
		
		Integer[] frequencyIntList = new Integer[nUniqueWords];
		
		//Write all frequencies corresponding to words
		for (int i = 0; i < nUniqueWords; i++) {
			
			frequencyIntList[i] = wordFrequencyMap.get(wordsStringList[i]);
			
		}
		
		//Write all the words to a text file
		try {
 
			File wordFile = new File(wordFilePath);
 
			// if file doesnt exists, then create it
			if (!wordFile.exists()) {
				wordFile.createNewFile();
			}
			
			File absoluteWordFile = wordFile.getAbsoluteFile();
			String fileWordAsString = absoluteWordFile.getPath();
 
			FileWriter wordFileWriter = new FileWriter(absoluteWordFile);
			BufferedWriter wordBWriter = new BufferedWriter(wordFileWriter);
			
			for (int i = 0; i < nUniqueWords; i++){
			
				wordBWriter.write(wordsStringList[i]);
				
				if (i != nUniqueWords) {
					
					wordBWriter.newLine();
					
				}

			}
			wordBWriter.close();
 
		} catch (IOException e) {
			System.out.println("Could not sucessfully print the words in"
					+ " word frequency map to file");
			e.printStackTrace();
		}
		
		//Write all the frequencies to a text file
		try {
 
			File freqFile = new File(frequencyFilePath);
 
			// if file doesnt exists, then create it
			if (!freqFile.exists()) {
				freqFile.createNewFile();
			}
			
			File absoluteFreqFile = freqFile.getAbsoluteFile();
			String fileFreqAsString = absoluteFreqFile.getPath();
 
			FileWriter freqFileWriter = new FileWriter(absoluteFreqFile);
			BufferedWriter freqBWriter = new BufferedWriter(freqFileWriter);
			
			for (int i = 0; i < nUniqueWords; i++){
			
				freqBWriter.write(Integer.toString(frequencyIntList[i]));
				
				if (i != nUniqueWords) {
					
					freqBWriter.newLine();
					
				}

			}
			freqBWriter.close();
 
		} catch (IOException e) {
			System.out.println("Could not sucessfully print the frequencies in"
					+ " word frequency map to file");
			e.printStackTrace();
		}
		
	}
	
	public static void writeTextToFile(String textAsString, String fileName){
		
		try{
			
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(textAsString); // stores in RAM first
//			outputStream.flush();
			outputStream.close(); // flushes the data to the file
			System.out.println("Output file generated!");
			
		}
		
			catch (FileNotFoundException e)
			
		{
				
				e.printStackTrace();
				
		}
		
	}
	
	public void writeCorrelationInfoToFile(String outputFilePath) {
		
		int nComputedCorrs = corrInfos.length;
		
		//Write all the correlation informations to a text file
		try {
 
			File outputFile = new File(outputFilePath);
 
			// if file doesnt exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			
			File absoluteOutputFile = outputFile.getAbsoluteFile();
			String fileOutputAsString = absoluteOutputFile.getPath();
 
			FileWriter outputFileWriter = new FileWriter(absoluteOutputFile);
			BufferedWriter outputBWriter = new BufferedWriter(outputFileWriter);
			
			for (int i = 0; i < nComputedCorrs; i++){
			
				outputBWriter.write(Double.toString(corrInfos[i]));
				
				if (i != nComputedCorrs) {
					
					outputBWriter.newLine();
					
				}

			}
			outputBWriter.close();
 
		} catch (IOException e) {
			System.out.println("Could not sucessfully print the correlation infos "
					+ " to file");
			e.printStackTrace();
		}
		
	}
	
}
