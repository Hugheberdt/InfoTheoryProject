package InfoTheory;


public class Main {
	
	
	private static String defaultFilePathKarl = new String("C:\\Users\\KNY\\Desktop\\TextTaxt.txt");
	private static String defaultFilePathMax = new String("C:/Users/M�x/Documents/Eclipse/TextProcessingIT/short text.txt");
	private static String originOfSpeciesMiniPath = new String("C:\\Users\\KNY\\Desktop\\PGOriginOfSpeciesMini.txt");
	private static String originOfSpeciesPath = new String("C:\\Users\\KNY\\Desktop\\PGOriginOfSpecies.txt");
	
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
		
		firstCorrelationReader.processText(filePath);
		
		String output = TextProcessing.processText(filePath);	
		
	}

}
