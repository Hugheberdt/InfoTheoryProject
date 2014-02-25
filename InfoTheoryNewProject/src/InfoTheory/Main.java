package InfoTheory;


public class Main {
	
	private static String defaultFilePath = new String("C:\\Users\\KNY\\Desktop\\TextTaxt.txt");
	
	public static void main(String[] args){
		
		CorrelationReader firstCorrelationReader = new CorrelationReader();
		
		String originOfSpeciesPath = new String("C:\\Users\\KNY\\Desktop\\PGOriginOfSpeciesMini.txt");
		
		firstCorrelationReader.processText(defaultFilePath);
		
//		firstCorrelationReader.displayCorrArray("cter. ");
	

		
	}

}
