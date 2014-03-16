package InfoTheory;

import java.util.HashMap;


public class Main {
	
//	private static String aliceFilePath = new String(".\\Books\\Alices Adventure in wonderland.txt");
	private static String fiftyFilePath = new String(".\\Books\\Fifty Notable Years 2014.txt");
	private static String grimmsFilePath = new String(".\\Books\\Grimms Fairy Tales.txt");
//	private static String tomSawyerFilePath = new String(".\\Books\\The Adventures of Tom Sawyer.txt");
//	private static String bibleFilePath = new String(".\\Books\\The Bible.txt");
//	private static String koranFilePath = new String(".\\Books\\The Koran.txt");
//	private static String originOfSpeciesMiniPath = new String(".\\Books\\PGOriginOfSpeciesMini.txt");
//	private static String originOfSpeciesPath = new String(".\\Books\\PGOriginOfSpecies.txt");
	
	private static String generatedWordsPath = new String("Output\\generatedWords.txt");
	private static String generatedFrequenciesPath = new String("Output\\generatedFrequencies.txt");
	
	private static String originalWordsPath = new String("Output\\originalWords.txt");
	private static String originalFrequenciesPath = new String("Output\\originalFrequencies.txt");
	
	private static String generatedTextPath = new String("Output\\generatedText.txt");

	
	public static void main(String[] args) throws Exception{
		
		CorrelationReader currentCorrelationReader = new CorrelationReader();
		
		String filePath = fiftyFilePath;
		
		String basePrefix = "fifty";
		
		//The number of different generations of random texts should be made
		int nGeneratedRealisations = 4;
		
		//Process the original text. Cleanup and normalisation.
		String processedText = TextProcessing.processText(filePath);
		
		int lengthOfOriginalText = processedText.length();
		
		int correlationDepth = 3;
		int correlationInfoDepth = 3;
		
		//Compute the text statistics, such as correlation information and conditional
		//character probabilities.
		currentCorrelationReader.generateTextStatistics(processedText,
				correlationDepth, correlationInfoDepth);
				
		//Print the correlation info
		currentCorrelationReader.printCorrelationInfo();
		
		String correlationInfoFileName = "Output\\originalCorrelationInfo.txt"; 
		
		//Write the correlation info to a file
		currentCorrelationReader.writeCorrelationInfoToFile(correlationInfoFileName);
		
		//Generate a random text
//		int textLength = 2000;
		int textLength = lengthOfOriginalText;
		int maxCorrelation = correlationDepth;
		String startingSnippet = "b";
		
		String generatedText = currentCorrelationReader.buildText(
				startingSnippet, maxCorrelation, textLength);
		
//		System.out.println("Test text: " + generatedText);
		
		//Compute the frequencies of generated words in a HashMap
		HashMap<String,Integer> wordsNFrequenciesGen = 
				currentCorrelationReader.getRankSumFrequency(generatedText);
		
		//Write the generated words and frequencies in one column to two different files
		//in the output folder.
		currentCorrelationReader.writeWordsAndFrequenciesToFile(
				wordsNFrequenciesGen, generatedWordsPath, generatedFrequenciesPath);
		
		//Compute the frequencies of generated words in a HashMap
		HashMap<String,Integer> wordsNFrequenciesOriginal = 
				currentCorrelationReader.getRankSumFrequency(processedText);
		
		//Write the generated words and frequencies in one column to two different files
		//in the output folder.
		currentCorrelationReader.writeWordsAndFrequenciesToFile(
				wordsNFrequenciesOriginal, originalWordsPath, originalFrequenciesPath);
		
		//Write the generated text to file
				
		CorrelationReader.writeTextToFile(generatedText, generatedTextPath);
		
	}

}
