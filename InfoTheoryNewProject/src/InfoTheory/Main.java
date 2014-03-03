package InfoTheory;


public class Main {
	
	private static String defaultFilePathAlice = new String(".\\Books\\Alices Adventure in wonderland.txt");
	private static String defaultFilePathFifty = new String(".\\Books\\Fifty Notable Years 2014.txt");
	private static String defaultFilePathGrimms = new String(".\\Books\\Grimms Fairy Tales.txt");
	private static String defaultFilePathTomSawyer = new String(".\\Books\\The Adventures of Tom Sawyer.txt");
	private static String defaultFilePathBible = new String(".\\Books\\The Bible.txt");
	private static String defaultFilePathKoran = new String(".\\Books\\The Koran.txt");
	private static String originOfSpeciesMiniPath = new String(".\\Books\\PGOriginOfSpeciesMini.txt");
	private static String originOfSpeciesPath = new String(".\\Books\\PGOriginOfSpecies.txt");
	
	private static String generatedWordsPath = new String(".\\Output\\generatedWords.txt");
	private static String generatedFrequenciesPath = new String(".\\Output\\generatedFrequencies.txt");

	
	public static void main(String[] args) throws Exception{
		
		CorrelationReader firstCorrelationReader = new CorrelationReader();
		
		String filePath = defaultFilePathAlice;
		
//		firstCorrelationReader.processText(filePath);
		
		String processedText = TextProcessing.processText(filePath);
		
		int correlationDepth = 8;
		int correlationInfoDepth = 8;
		
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
		
	}

}
