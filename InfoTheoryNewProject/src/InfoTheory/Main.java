package InfoTheory;

import java.util.HashMap;


public class Main {
	
	private static String aliceFilePath = new String(".\\Books\\Alices Adventure in wonderland.txt");
	private static String fiftyFilePath = new String(".\\Books\\Fifty Notable Years 2014.txt");
	private static String grimmsFilePath = new String(".\\Books\\Grimms Fairy Tales.txt");
	private static String tomSawyerFilePath = new String(".\\Books\\The Adventures of Tom Sawyer.txt");
	private static String BiblefilePath = new String(".\\Books\\The Bible.txt");
	private static String koranFilePath = new String(".\\Books\\The Koran.txt");
	private static String originOfSpeciesMiniPath = new String(".\\Books\\PGOriginOfSpeciesMini.txt");
	private static String originOfSpeciesPath = new String(".\\Books\\PGOriginOfSpecies.txt");
	
	private static String generatedWordsPath = new String("Output\\generatedWords.txt");
	private static String generatedFrequenciesPath = new String("Output\\generatedFrequencies.txt");
	
	private static String orginalWordsPath = new String(".\\Output\\originalWords.txt");
	private static String originalFrequenciesPath = new String(".\\Output\\originalFrequencies.txt");

	
	public static void main(String[] args) throws Exception{
		
		CorrelationReader firstCorrelationReader = new CorrelationReader();
		
		String filePath = originOfSpeciesMiniPath;
		
//		firstCorrelationReader.processText(filePath);
		
		String processedText = TextProcessing.processText(filePath);
		
		int correlationDepth = 4;
		int correlationInfoDepth = 4;
		
		//Compute the text statistics, such as correlation information and conditional
		//character probabilities.
		firstCorrelationReader.generateTextStatistics(processedText,
				correlationDepth, correlationInfoDepth);
				
		//Print the correlation info
		firstCorrelationReader.printCorrelationInfo();
		
		//Generate a random text
		int textLength = 100;
		int maxCorrelation = correlationDepth;
		String startingSnippet = "beginning";
		
		String generatedText = firstCorrelationReader.buildText(
				startingSnippet, maxCorrelation, textLength);
		
		System.out.println("Test text: " + generatedText);
		
		//Compute the frequencies of words in a HashMap
		HashMap<String,Integer> wordsNFrequencies = 
				firstCorrelationReader.getRankSumFrequency(generatedText);
		
		//Write the words and frequencies in one column to two different files
		//in the output folder.
		firstCorrelationReader.writeWordsAndFrequenciesToFile(
				wordsNFrequencies, generatedWordsPath, generatedFrequenciesPath);
		
	}

}
