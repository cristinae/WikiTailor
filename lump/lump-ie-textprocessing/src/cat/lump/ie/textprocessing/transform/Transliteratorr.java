package cat.lump.ie.textprocessing.transform;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;

import com.ibm.icu.text.Transliterator;

/**
 * A class to transliterate a text with ICU4J
 * @author albarron
 *
 */
public class Transliteratorr {//extends StringText{
	
	private Transliterator translit;


	
	public Transliteratorr(Locale language){
		if (language.getLanguage().equals("russian")){
			translit = Transliterator.getInstance("Cyrillic-Latin");
		} else if (language.getLanguage().equals("greek")){
			translit = Transliterator.getInstance("Greek-Latin");
		} else {
			CHK.CHECK(false, "I cannot handle the required language");
		}
	}

	public String get(String str){		
		return translit.transliterate(str);		
	}	

	private static Options getOptions(){
		Options options = new Options();
		options.addOption("h", "help", false, 
				"this help message");	
		options.addOption("i", "input-file", true, 
				"File to transliterate");			

		options.addOption("l", "language", true, 
				"Source language (either 'ru' or 'el')" );
		return options;		
	}
	
	private static void printHelp(Options options){
		HelpFormatter f = new HelpFormatter();
	    f.printHelp("Help", options);
	    System.exit(1);
	}	
	
	public static void main(String[] args){
		Transliteratorr trans = null;
		CommandLine line = null;
		
		Options options = getOptions();
		
		CommandLineParser parser = new PosixParser();		
		
		try {
			line = parser.parse( options, args );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if( line.hasOption( "h" ) ) {
		 	printHelp(options);

		}
		
		if(! line.hasOption("i") ) {
			System.err.println("I need a readable file as input");
			printHelp(options);		    
		}
		
		if(! line.hasOption("l") ) {
			System.err.println("Please, set the source language");
			printHelp(options);			
		}
		
		File input = new File(line.getOptionValue("i"));
		if (! input.canRead()){
			System.err.println("I cannot read the input file");
			printHelp(options);			
		}
		
		String lan = line.getOptionValue("l");
		
		if (lan.equals("ru")){
			trans = new Transliteratorr(new Locale("Russian"));
		} else if (lan.equals("el")){
			trans = new Transliteratorr(new Locale("Greek"));
		} else {
			System.err.println("I cannot handle the required language");
			printHelp(options);			
		}
		
	   		
		try {
			FileIO.stringToFile(
				new File(input.getParent() + FileIO.separator + "trans." + 
								input.getName()), 
				trans.get(FileIO.fileToString(input)), 
				false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
}


