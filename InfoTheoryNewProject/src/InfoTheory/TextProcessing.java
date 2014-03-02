package InfoTheory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

/* this program should be able to:
 * delete punctuation : check every sign on the keyboard
 * delete numbers
 * set all letters to lower case
 * avoid double spaces
 * count character
 * abbreviations
 * print length of text before and afterwards.
 
 */




public class TextProcessing {
	
	public TextProcessing(){
		
	}
	
	public static String processText(String filePath) throws Exception {
		

		FileReader file = new FileReader(filePath);
		BufferedReader reader = new BufferedReader(file);
		StringBuilder textBuilder = new StringBuilder();
		
		String line = reader.readLine();
		
		String fileName = "Processed text.txt";
		
		while ( line!= null)
		{
			textBuilder.append(line);
//			text += line;
			line = reader.readLine();
		}
		
		String text = textBuilder.toString();
		
		
//		System.out.println("Original text: " + text);
		int lengthBefore = text.length();
		
		
		text = text.toLowerCase(); // all letters lower case
		
		text = text.replaceAll("i'm", "i am").replaceAll("you're", "you are").replaceAll("we're", "we are").replaceAll("they're", "they are");
		text = text.replaceAll("don't", "do not").replaceAll("didn't", "did not").replaceAll("can't", "can not").replaceAll("won't", "will not").replaceAll("would't", "would not").replaceAll("couldn't", "could not").replaceAll("haven't", "have not").replaceAll("hasn't", "has not").replaceAll("ain't", "am not");// ain't??
		text = text.replaceAll("isn't", "is not").replaceAll("aren't", "are not").replaceAll("weren't", "were not");
		text = text.replaceAll("i'll", "i will").replaceAll("he'll", "he will").replaceAll("she'll", "she will").replaceAll("it'll", "it will").replaceAll("you'll", "you will").replaceAll("we'll", "we will");
		
		text = text.replaceAll("\\.", " ").replaceAll("\\:", " ").replaceAll("\\,", " ").replaceAll("\\;", " ").replaceAll("\\?", " ").replaceAll("\\!", " ");
		text = text.replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("\\[", " ").replaceAll("\\]", " ").replaceAll("\\{", " ").replaceAll("\\}", " ");// delete brackets
		text = text.replaceAll("\\-", " ").replaceAll("\\_", " ").replaceAll("\\+", " ").replaceAll("\\*", " ").replaceAll("\\/", " ").replaceAll("\\=", " ").replaceAll("\\<", " ").replaceAll("\\>", " "); // delete mathematical signs
		text = text.replaceAll("\\�", " ").replaceAll("\\$", " ").replaceAll("\\&", " ").replaceAll("\\%", " ").replaceAll("\\#", " ").replaceAll("\\-", " "); // delete other stuff
		text = text.replaceAll("\\^", " ").replaceAll("\\�", " ").replaceAll("\\'", " ").replaceAll("\\`", " ").replaceAll("\\�", " ").replaceAll("\\|", " ").replaceAll("\\~", " "); // delete
		text = text.replaceAll("\""," ").replaceAll("\\\\"," "); // replace quotes and backslashes
		
		
		text =	text.replaceAll("\\@", "");  // delete@
		text =	text.replaceAll("[0-9]", "");  // delete numbers
		text =	text.replaceAll("      ", " ");  // delete 6 white spaces
		text =	text.replaceAll("     ", " ");  // delete 5 white spaces
		text =	text.replaceAll("    ", " ");  // delete 4 white spaces
		text =	text.replaceAll("   ", " ");  // delete 3 white spaces
		text =	text.replaceAll("  ", " ");  // delete 2 white spaces
		
		int lengthAfter = text.length();
		
		
		//String processedText = text;
		
//		System.out.println("Processed text: " + text);
		System.out.println("Length before processing = " + lengthBefore);
		System.out.println("Length after processing = " +lengthAfter);
		
		try{
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(text); // stores in RAM first
			//outputStream.flush();
			outputStream.close(); // flushes the data to the file
			System.out.println("Output file generated!");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		
		return text;
	}

}


