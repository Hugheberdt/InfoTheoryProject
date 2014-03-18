package InfoTheory;

import java.util.HashMap;


public class Main {
	
//	private static String aliceFilePath = new String(".\\Books\\Alices Adventure in wonderland.txt");
	private static String fiftyFilePath = new String(".\\Books\\Fifty Notable Years 2014.txt");
	private static String grimmsFilePath = new String(".\\Books\\Grimms Fairy Tales.txt");
	private static String testFilePath = new String(".\\Books\\TestText.txt");
//	private static String tomSawyerFilePath = new String(".\\Books\\The Adventures of Tom Sawyer.txt");
//	private static String bibleFilePath = new String(".\\Books\\The Bible.txt");
//	private static String koranFilePath = new String(".\\Books\\The Koran.txt");
//	private static String originOfSpeciesMiniPath = new String(".\\Books\\PGOriginOfSpeciesMini.txt");
//	private static String originOfSpeciesPath = new String(".\\Books\\PGOriginOfSpecies.txt");
	
	public static void main(String[] args) throws Exception{
		
//		String filePath = grimmsFilePath;
		String filePath = testFilePath;
		
		//The number of different generations of random texts should be made
		int nGeneratedRealisations = 4;
		
		//The depths for which conditional probabilities are stored in the original text
//		int[] allCorrelationDepths = {1, 4, 8, 13};
		
		int[] allCorrelationDepths = {0};
		
		int nCorrelationDepths = allCorrelationDepths.length;
		
		//Process the original text. Cleanup and normalisation.
		String processedText = TextProcessing.processText(filePath);
		
		int lengthOfOriginalText = processedText.length();
		
		for (int iCorrDepth = 0; iCorrDepth < nCorrelationDepths; iCorrDepth++) {
		
			//Current depth for which conditional probabilities are stored in the original text
			int correlationDepth = allCorrelationDepths[iCorrDepth];
			
			int correlationInfoDepth = correlationDepth;
			
			CorrelationReader currentCorrelationReader = new CorrelationReader();
			
			//Compute the text statistics, such as correlation information and conditional
			//character probabilities.
			currentCorrelationReader.generateTextStatistics(processedText,
					correlationDepth, correlationInfoDepth);
					
			//Print the correlation info
			currentCorrelationReader.printCorrelationInfo();
			
			String correlationInfoFileName = "Output\\originalCorrelationInfo" +
			"CorrDepth" + Integer.toString(correlationDepth) + ".txt"; 
			
			//Write the correlation info to a file
			currentCorrelationReader.writeCorrelationInfoToFile(correlationInfoFileName);
			
			//Compute the frequencies of original words in a HashMap
			HashMap<String,Integer> wordsNFrequenciesOriginal = 
					currentCorrelationReader.getRankSumFrequency(processedText);
			
			//The filepath for the orginal words and frequencies for the current correlation
			//depth.
			
			String originalWordsPath = new String("Output\\originalWords" + "CorrDepth" + 
					Integer.toString(correlationDepth) + ".txt");
			String originalFrequenciesPath = new String("Output\\originalFrequencies" +
					"CorrDepth" + Integer.toString(correlationDepth) + ".txt");
			
			//Write the original words and frequencies in one column to two different files
			//in the output folder.
			currentCorrelationReader.writeWordsAndFrequenciesToFile(
					wordsNFrequenciesOriginal, originalWordsPath, originalFrequenciesPath);
			
			for (int iGeneratedText = 0; iGeneratedText < nGeneratedRealisations;
					iGeneratedText ++){
				
				//Generate a random text
				int textLength = lengthOfOriginalText;
				int maxCorrelation = correlationDepth;
				String startingSnippet = "";
				
				String generatedText = currentCorrelationReader.buildText(
						startingSnippet, maxCorrelation, textLength);
				
				//Compute the frequencies of generated words in a HashMap
				HashMap<String,Integer> wordsNFrequenciesGen = 
						currentCorrelationReader.getRankSumFrequency(generatedText);
				
				//Update filenames
				String generatedWordsPath = new String("Output\\generatedWords" +
						"CorrDepth" + Integer.toString(correlationDepth) + "iGeneration" +
						Integer.toString(iGeneratedText) + ".txt");
				String generatedFrequenciesPath = new String("Output\\generatedFrequencies" +
						"CorrDepth" + Integer.toString(correlationDepth) + "iGeneration" +
						Integer.toString(iGeneratedText) + ".txt");
				
				//Write the generated words and frequencies in one column to two different files
				//in the output folder.
				currentCorrelationReader.writeWordsAndFrequenciesToFile(
						wordsNFrequenciesGen, generatedWordsPath, generatedFrequenciesPath);
				
				//Update filename
				String generatedTextPath = new String("Output\\generatedText" +
						"CorrDepth" + Integer.toString(correlationDepth) + "iGeneration" +
						Integer.toString(iGeneratedText) + ".txt");
				
				//Write the generated text to file
				CorrelationReader.writeTextToFile(generatedText, generatedTextPath);
				
				//Compute the statistics of the generated text
				
				CorrelationReader generatedTextCorrelationReader = new CorrelationReader();
				
				generatedTextCorrelationReader.generateTextStatistics(generatedText,
						correlationDepth, correlationInfoDepth);
				
				String genCorrelationInfoFileName = "Output\\generatedCorrelationInfo" +
						"CorrDepth" + Integer.toString(correlationDepth) + 
						"iGeneration" + Integer.toString(iGeneratedText) + ".txt"; 
						
				//Write the correlation info to a file
				currentCorrelationReader.writeCorrelationInfoToFile(genCorrelationInfoFileName);
				
			}
			
		}
		
	}
	
}
