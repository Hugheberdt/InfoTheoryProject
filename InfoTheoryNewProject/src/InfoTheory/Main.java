package InfoTheory;


public class Main {
	
	
	private static String defaultFilePathKarl = new String("C:\\Users\\KNY\\Desktop\\TextTaxt.txt");
	private static String defaultFilePathMax = new String("C:/Users/Mäx/Documents/Eclipse/TextProcessingIT/short text.txt");
	private static String originOfSpeciesMiniPath = new String("C:\\Users\\KNY\\Desktop\\PGOriginOfSpeciesMini.txt");
	private static String originOfSpeciesPath = new String("C:\\Users\\KNY\\Desktop\\PGOriginOfSpecies.txt");
	private static String generatedWordsPathKarl = new String("C:\\Users\\KNY\\Desktop\\generatedWords.txt");
	private static String generatedFrequenciesPathKarl = new String("C:\\Users\\KNY\\Desktop\\generatedFrequencies.txt");
	private static String generatedWordsPathMax = new String("C:\\Users\\KNY\\Desktop\\generatedWords.txt");
	private static String generatedFrequenciesPathMax = new String("C:\\Users\\KNY\\Desktop\\generatedFrequencies.txt");
	
	public static void main(String[] args) throws Exception{
		
		CorrelationReader firstCorrelationReader = new CorrelationReader();
		
		String filePath = null;
		
		boolean runAsMax = false;
		
		if (runAsMax == true)
		{
			filePath = defaultFilePathMax;
		}
		else{
			
			filePath = originOfSpeciesPath;
			
		}
		
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
