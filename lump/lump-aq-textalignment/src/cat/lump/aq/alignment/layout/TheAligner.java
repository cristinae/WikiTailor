package cat.lump.aq.alignment.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cat.lump.aq.alignment.control.ConfigReader;
import cat.lump.aq.check.CHK;

/**
 * An interface to manually identify both parallel and comparable 
 * sentences from a pair of files.
 * <br/>
 * The input is a pair of files that are to be compared to each 
 * other, looking for potential parallel or comparable fragments.
 * 
 * @author albarron
 * @since Sep 2012
 * @version 0.3
 *
 */
public class TheAligner extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
//	/**File with the configuration (if not hard-coded) */
//	private static String CONFIG_FILE;
	
	/** Hard-coded directory with the texts */
	private static final String HARDCODED_DIRECTORY = "TEXTS";
	
	/** Hard-coded file with index */
	private static final String HARDCODED_CSV = "ids_and_titles.csv";
	
	/** Hard-coded source language */
	private static final Locale HARDCODED_SRC_LANGUAGE = new Locale("en");
	
	/** Hard-coded target language */
	private static final Locale HARDCODE_TRG_LANGUAGE = new Locale("es");
	
	private FullPanel tAlign;

	private Locale srcLan;
	private Locale trgLan;
	private String articleIdTitle;
	private String articlesPath;
	
	/**
	 * Interface invocation. It sets the title and dimensions. 
	 * @param horizontal true if the texts should be displayed horizontally	 
	 */
	public TheAligner(String configFile)	{
		super("Sentences Alignment");
		
		ConfigReader cf = new ConfigReader(configFile);
		srcLan = new Locale(cf.getSourceLanguage());
		trgLan = new Locale(cf.getTargetLanguage());
		articleIdTitle = cf.getPairsCsvFile();
		articlesPath = cf.getArticlesPath();
		
		setUp();
		//this(srcLan, trgLan, articleIdTitle, articlesPath);
	}
	
	public TheAligner(Locale srcLan, Locale trgLan, String articleIdTitle, String articlesPath){
		super("Sentences Alignment");
		CHK.CHECK_NOT_NULL(srcLan);
		CHK.CHECK_NOT_NULL(trgLan);
		CHK.CHECK_NOT_NULL(articleIdTitle);
		CHK.CHECK_NOT_NULL(articlesPath);
		
		this.srcLan = srcLan;
		this.trgLan = trgLan;
		this.articleIdTitle = articleIdTitle;
		this.articlesPath = articlesPath;
		
		setUp();
	}
	
	
	private void setUp(){
		
		JPanel		panel1;
		
		//CONFIG_FILE = configFile;		

		setSize( 1500, 450 );		
		setBackground( Color.gray );

		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		
		// Create the tab pages
		tAlign = new FullPanel(srcLan, trgLan, articleIdTitle, articlesPath);
		
		
		//tAlign = new FullPanel(configFile);
		panel1 = tAlign.getPanel();
		
		topPanel.add( panel1, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				if (tAlign.confirmLooseProgress()){
					System.exit(0);
				}				
			}
		});
	}
	
	
	

	@SuppressWarnings("unused")
	private static void invokeWithConfig(String[] args){
		if (args.length == 0){
			System.err.println("I need the input configuration file!");
			System.exit(1);
		}		
	
		String configFile = args[0];
		
		TheAligner mainFrame	= new TheAligner(configFile);
		mainFrame.setVisible(true );
	}
	
	@SuppressWarnings("unused")
	private static void invokeHardCoded(){

		String currentPath = new File( "." ).getAbsolutePath();
		
		File directory = new File(currentPath + File.separator + 
							HARDCODED_DIRECTORY);
		
		File index = new File(currentPath + File.separator + 
							HARDCODED_DIRECTORY + File.separator + 
							HARDCODED_CSV);
		
		//Check that the folder and index exist
		if (! directory.isDirectory()){
			CHK.CHECK(false, 
					String.format("I cannot read input directory ('%s')", 
							HARDCODED_DIRECTORY));
		}		
		if (! index.canRead()){
			CHK.CHECK(false, 
					String.format("I cannot read index file ('%s%s%s')",
							HARDCODED_DIRECTORY, File.separator, HARDCODED_CSV));
		}
		
		TheAligner mainFrame	= new TheAligner(
				HARDCODED_SRC_LANGUAGE, 
				HARDCODE_TRG_LANGUAGE, 
				index.getAbsolutePath(), 
				directory.getAbsolutePath());
		
		mainFrame.setVisible(true );	
	}
	
	
	public static void main( String args[] )	{
//		invokeWithConfig(args);
		invokeHardCoded();
				
	}	
	
}