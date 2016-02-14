package cat.lump.aq.alignment.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cat.lump.aq.alignment.control.ErrorControl;
import cat.lump.aq.alignment.control.History;
import cat.lump.aq.alignment.control.Tracker;
import cat.lump.aq.alignment.fragment.AlignmentLevel;
import cat.lump.aq.alignment.fragment.ComparableFragment;
import cat.lump.aq.alignment.fragment.ParallelFragment;
import cat.lump.aq.alignment.fs.PAN2Alignment;
import cat.lump.aq.alignment.fs.WikiAnn2PAN;
import cat.lump.aq.alignment.fs.WikiAnn2Plain;
import cat.lump.aq.check.CHK;
import cat.lump.aq.corpus.pan.xml.XMLwriterAlignment;
import cat.lump.aq.corpus.pan.DocumentAnnotation;
import cat.lump.aq.io.files.CsvFoolReader;
import cat.lump.aq.io.files.CsvFoolWriter;
import cat.lump.aq.io.files.FileIO;
import cat.lump.aq.structure.ArticlePair;


class FullPanel implements ActionListener{
	
	/**Number of elements to be shown in the article's selection list */
	private final int LIST_SIZE = 10;
	
	/**Path to the articles pairs */
	private String articlesPath;	
	
	/**String with article's ID and title */
	private String articleIdTitle;	

	/**Source language*/
	private Locale srcLan;

	/**Target language */
	private Locale trgLan;
	
	/**Source text*/
	private String srcText;
	
	/**Target text*/
	private String trgText;
	
	private JPanel panelText;	
	private GridBagConstraints gbConstraints;
    private GridBagLayout gbLayouts; 
    
    private String currentDocumentTitle;
    private int currentSrcID;
    private int currentTrgID;
    
    /**List with the article pairs  */
    private java.util.List<ArticlePair> pairs;
    
    protected List srcList;
	protected List trgList;	
		
	/**Button to set a parallel instance */
	private JButton btnParallel;
	/**Button to set a comparable instance */
	private JButton btnComparable;
	
	/**Button to undo */
	private JButton btnUndo;
	/**Button to select a document */	
	private JButton btnDocument;
	
	/**Button to show a summary of the selected instances*/
	private JButton btnSummary;
	
	/**Button to save the alignments	 */
	private JButton btnSave;
	
	/**Button to get help */
	private JButton btnHelp;
	
	private final String SAVE_ICON = 
			"/cat/lump/aq/alignment/layout/config/Save16.gif";
	
		
	private Label srcLast,
					trgLast;
	
	private JLabel top;
	private JLabel lastAdded;
	protected JLabel errorMessage;

	private JPanel confirm;
	
	//Warning: these should not be invoked as AlignmentLevel.
	protected AlignmentLevel parallelFragments; 
	private AlignmentLevel comparableFragments;
	
	/**Contains a record of the operations carried out*/
	protected History history;
	private Tracker tracker;

	protected ErrorControl error;
	
	public FullPanel(Locale srcLan, Locale trgLan, String articleIdTitle, String articlesPath){
		CHK.CHECK_NOT_NULL(srcLan);
		CHK.CHECK_NOT_NULL(trgLan);
		CHK.CHECK_NOT_NULL(articleIdTitle);
		CHK.CHECK_NOT_NULL(articlesPath);
		
		this.srcLan = srcLan;
		this.trgLan = trgLan;
		this.articleIdTitle = articleIdTitle;
		this.articlesPath = articlesPath;
	
		error = new ErrorControl();
		parallelFragments = new ParallelFragment();
		comparableFragments = new ComparableFragment();
		history = new History();
		tracker = new Tracker();
		
//		updateTracker();
		
		gbConstraints = new GridBagConstraints();
	    gbLayouts = new GridBagLayout();		
		panelText = new JPanel();
		panelText.setLayout( gbLayouts );
		
		//Make components fill the available display area in the horizontal 
		//and vertical directions
		gbConstraints.fill = GridBagConstraints.BOTH;
		//Resize columns and keep them equal during window resizing 
		//Note: Since no weight value is specified, rows are not resized, 
		//but remain together in the middle of the display
		gbConstraints.weightx = 1.0;  
		
		
		generateHeader();		
//		generateLabels();
		generateLists();
		generateButtons();
		generateLogs();
		generateUndo();	
		generateSave();
		generateHelp();		
	}
	
	
	 public void actionPerformed(ActionEvent e) {
		 if (e.getSource() == btnParallel){
			 addParallel();
		 } else if (e.getSource() == btnComparable){
			 addComparable();
		 } else if (e.getSource() == btnUndo){ 
			 undo();
		 } else if (e.getSource() == btnSummary){ 
			 SummaryPannel.display(panelText, 
					 parallelFragments, comparableFragments, 
					 srcList, trgList);
		 } else if (e.getSource() == btnDocument){
			documentSelection();
		 } else if (e.getSource() == btnSave){
			save();
		 } else if(e.getSource() == btnHelp){
			HelpPanel htmlHelp = new HelpPanel(null); 
			htmlHelp.load(panelText);			
		}
	 }

	
	/** Pops up a window that asks for confirming whether the user wants 
	 * to follow without saving the current progress
	 */
	public boolean confirmLooseProgress(){
		//TRATA DE CARGAR EL NUEVO
		if (!checkIfChanged()){	//nothing has changed; no confirmation is necessary
			return true;
		}

		JTextArea confirm_message = new JTextArea();
		confirm_message.setBackground(Color.LIGHT_GRAY);
		
		confirm_message.setText("Current changes have not been saved.\n " +
								"Are you sure you want to continue? \n " +
								"(unsaved progress will be lost)");
		confirm_message.setEditable(false);
		//			help_message.setText("THIS IS \nJUST A \nTEST");
		confirm = new JPanel();
		confirm.setLayout(new BorderLayout()); // unless already there
		confirm.add(confirm_message, BorderLayout.CENTER);

		confirm.setSize(800,400);
		Object selected = JOptionPane.showConfirmDialog(
				panelText, 
				confirm, 
				"Warning",
				JOptionPane.YES_NO_OPTION);

		if (selected.equals(0))	{	//yes = 0
			return true;		
		}

		//no = 1			
		return false;
	}	

	
	
	/** Creates a simple header for the top of the window */
	private void generateHeader(){
		Panel stylePanel = new Panel();
	    
		//Avoids the button to fill all of the window		
	    gbConstraints.fill = GridBagConstraints.NONE;				
		//the last one in its row.		
	    gbConstraints.gridwidth = GridBagConstraints.REMAINDER;		
		//Pass the Label component and GridBagConstraints object to layout
	    gbLayouts.setConstraints(stylePanel, gbConstraints);
		//Add the Label component to container
	    panelText.add(stylePanel);						
		
		
		btnDocument = new JButton("Select document");
		btnDocument.setAlignmentX(5);
		btnDocument.addActionListener(this);
		
		//Avoids the button to fill all of the window
		gbConstraints.fill = GridBagConstraints.NONE;						
		//the last one in its row.
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;		
		
		//Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(btnDocument, gbConstraints);							
		//Add the Label component to container
		panelText.add(btnDocument);								

		top = new JLabel();
		top.setText("Document: ");
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;		//the last one in its row.
		gbLayouts.setConstraints(top, gbConstraints);				//Pass the Label component and GridBagConstraints object to layout.		
		panelText.add(top);								//Add the Label component to container
				
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		gbLayouts.setConstraints(sep, gbConstraints);					//Pass the Label component and GridBagConstraints object to layout.		
		panelText.add(sep);								//Add the Label component to container
	}

	/**
	 * Creates the lists containing the documents to align (one sentence
	 * per row)
	 */	
	private void generateLists(){		
		srcList = new List(LIST_SIZE, true);
		trgList = new List(LIST_SIZE, true);
		GridBagConstraints gbListConstraints = (GridBagConstraints) gbConstraints.clone();
		gbListConstraints.fill = GridBagConstraints.BOTH;
		gbListConstraints.weighty = 1.0;
		
					
		gbListConstraints.gridwidth = GridBagConstraints.RELATIVE;
		
				
		gbLayouts.setConstraints(srcList, gbListConstraints);
		panelText.add(srcList);		
						
		gbListConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayouts.setConstraints(trgList, gbListConstraints);
		panelText.add(trgList);
	}
	
	/**Puts the parallel and comparable buttons in their position. */
	private void generateButtons(){

		btnParallel = new JButton("Parallel (ALT+p)");
		btnParallel.setMnemonic(KeyEvent.VK_P);			//shortcut: ALT+p
		btnParallel.addActionListener(this);

		//Avoids the button to fill all of the window
		gbConstraints.fill = GridBagConstraints.NONE;			
		gbConstraints.gridwidth = GridBagConstraints.RELATIVE;		//not the last element	    
		gbLayouts.setConstraints(btnParallel, gbConstraints);
		panelText.add(btnParallel);
	
		btnComparable = new JButton( "Comparable (ALT+c )" );
		btnComparable.setMnemonic(KeyEvent.VK_C);			//shortcut: ALT+c		
		btnComparable.addActionListener(this);
		
		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayouts.setConstraints(btnComparable, gbConstraints);		
		panelText.add(btnComparable);
	}
	
	/**Generates the logs	 */
	private void generateLogs(){
		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		errorMessage = new JLabel("Log: ", JLabel.LEFT);
		errorMessage.setHorizontalTextPosition(JLabel.LEADING);
		gbLayouts.setConstraints(errorMessage, gbConstraints);
		panelText.add(errorMessage);
		
		srcLast = new Label(".", Label.LEFT);
		trgLast = new Label(".", Label.LEFT);
		srcLast.setBackground(Color.WHITE);
		trgLast.setBackground(Color.WHITE);
		srcLast.setForeground(Color.DARK_GRAY);
		trgLast.setForeground(Color.DARK_GRAY);
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.gridwidth = GridBagConstraints.RELATIVE;
		gbLayouts.setConstraints(srcLast, gbConstraints);		
		panelText.add(srcLast);
		
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayouts.setConstraints(trgLast, gbConstraints);		
		panelText.add(trgLast);
		lastAdded = new JLabel("Last added: ");
		gbConstraints.fill = GridBagConstraints.NONE;
		//next to last one in its row (1 or more components can be added)
		gbConstraints.gridwidth = GridBagConstraints.RELATIVE;		
		
		gbLayouts.setConstraints(lastAdded, gbConstraints);
		panelText.add(lastAdded);
	}
	
	/**Configuration of the UNDO button */
	private void generateUndo(){
		btnUndo = new JButton ("Undo (ALT+u)");
		btnUndo.setMnemonic(KeyEvent.VK_U);
		btnUndo.addActionListener(this);

		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayouts.setConstraints(btnUndo, gbConstraints);		
		gbLayouts.setConstraints(btnUndo, gbConstraints);		
		panelText.add(btnUndo);
		
		JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;		//the last one in its row.
		//Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(sep2, gbConstraints);							
		panelText.add(sep2);
	}
	
	/**Configuration and actions for the save button*/
	private void generateSave(){		
		btnSummary = new JButton("Summary");	
		btnSummary.addActionListener(this);	
		
		//Avoids the button to fill all of the window
		gbConstraints.fill = GridBagConstraints.NONE;				
		//the last one in its row.
	    gbConstraints.gridwidth = GridBagConstraints.RELATIVE;		
	    //Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(btnSummary, gbConstraints);					
		//Add the Label component to container
		panelText.add(btnSummary);						
		
		//defineSaveButton();
		btnSave = new JButton("Save",
					new ImageIcon(FullPanel.class.getResource(SAVE_ICON)));
//		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		
		//Avoids the button to fill all of the window
		gbConstraints.fill = GridBagConstraints.NONE;				
	    //the last one in its row.
		gbConstraints.gridwidth = GridBagConstraints.RELATIVE;			    
		//Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(btnSave, gbConstraints);					
		//Add the Label component to container
		panelText.add(btnSave);						
		}
	
	
	private void generateHelp(){
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		sep.setBackground(Color.BLACK);
		
		//Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(sep, gbConstraints);							
		panelText.add(sep);	
		
		btnHelp = new JButton("Help");	
		btnHelp.addActionListener(this);	
		
		//Avoids the button to fill all of the window
		gbConstraints.fill = GridBagConstraints.NONE;				
	    //the last one in its row.	
		gbConstraints.gridwidth = GridBagConstraints.RELATIVE;		    
		//Pass the Label component and GridBagConstraints object to layout.
		gbLayouts.setConstraints(btnHelp, gbConstraints);					
		//Add the Label component to container
		panelText.add(btnHelp);						
	}
	

	 /**Resets everything for starting with a new document */
	private void restartForNewDocuments(){
		 history.clear();		//the history must be removed
		 parallelFragments.removeAll();
		 comparableFragments.removeAll();
		 updateTracker();
		// setArticleIdTitle();		 
	 }
	 
	 
	/**Pops up a window for selecting the pair of documents to work with */
	private void documentSelection(){
		if (! confirmLooseProgress()){
			//The user doesn't want to loose the progress; operation cancelled
			return;		
		}

		restartForNewDocuments();	

		Object[] possibilities = getPossibilities();

		Object selectedValue = JOptionPane.showInputDialog(panelText, 
				"Select a new pair of documents to work with. \n"
				+ "(unsaved changes will be lost!)", 
				"Select document",
				JOptionPane.INFORMATION_MESSAGE,
				//JOptionPane.OK_CANCEL_OPTION, 
				null,	
				possibilities, 
				possibilities[0]);//initialSelectionValue);

		if (selectedValue != null){			
			setCurrentDocument(selectedValue.toString());			 
			top.setText("Document: " + currentDocumentTitle);			 
			loadSourceText();
			loadTargetText();
			loadAnnotationsIfAvailable();
			//http://sujitpal.blogspot.com/2011/04/uima-sentence-annotator-using-opennlp.html
		}
	}
	 
	/**Load previous annotations for this document pair, if they exist */
	private void loadAnnotationsIfAvailable(){
		File f = new File(getXmlFileName());
		if (f.exists()){
			PAN2Alignment p2a = new PAN2Alignment(srcList, trgList);
			try {
				p2a.loadAnnotations(f);
				p2a.loadTexts(articlesPath, srcLan, trgLan);
				parallelFragments = p2a.getParallelSentences();
				comparableFragments = p2a.getComparableSentences();
				history.clear();		//the history must be removed
				updateTracker();
			//	setArticleIdTitle();		 

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void setCurrentDocument(String title){
		currentDocumentTitle = title;
		currentSrcID = Integer.parseInt(
				 currentDocumentTitle.substring(0, 
						 currentDocumentTitle.indexOf("::")));
		 for (ArticlePair p : pairs){
			 if (p.srcID == currentSrcID)
				 currentTrgID = p.trg_id;
		 }	
	}
		
	/**Tries to add a new parallel pair. It also refreshes the history.
	 * If the pair cannot be added, an error message is displayed and 
	 *  nothing is done 
	 *  TODO whether this should be moved to the alignmentlevel class
	 * @see cat.talp.lump.alignment.sentences.AlignmentLevel#addElement(int, int) 
	 */
	protected void addParallel(){		 
		StringBuffer sb_answer = new StringBuffer();
		
		//get the indexes from the lists
		int[] srcSelect = srcList.getSelectedIndexes();
		int[] trgSelect = trgList.getSelectedIndexes();

		for (int i = 0; i< srcSelect.length; i++){
			srcList.deselect(srcSelect[i]);

			for (int j= 0 ; j < trgSelect.length; j++){
				trgList.deselect(trgSelect[j]);			

				//tries to add the new pair and gets the answer
				error = parallelFragments.add(srcSelect[i], trgSelect[j], 
						srcList.getItem(srcSelect[i]), trgList.getItem(trgSelect[j]));
				if (error.getID() == 0 || error.getID() == 2){//new pair accepted
					history.pushParallel(srcSelect[i], trgSelect[j], 
								formatLog(srcList.getItem(srcSelect[i])), 
								formatLog(trgList.getItem(trgSelect[j])) );
					refreshLog();	
				}		
				//refresh the error message (empty if everything's fine)
				errorMessage.setText(error.getText());
				sb_answer.delete(0, sb_answer.length());																	
			}
		}
		
		
	}
		
//	/**
//	 * Originally intended to delete the parallel fragments. Now, as parallel
//	 * and comparable elements behave in the same way, we don't need it.
//	 * 
//	 * @param list
//	 * 
//	 */
//	private void deselectFromList(List list){
//		for (int i : list.getSelectedIndexes())				
//			list.deselect(i);
//	}
	
	 /**
	  * Tries to add a new comparable pair. It also refreshes the history.
	  * If the pair cannot be added, an error message is displayed and 
	  * nothing is done. 
	  * TODO whether this should be moved to the alignmentlevel class
	  * @see cat.talp.lump.alignment.sentences.AlignmentLevel#addElement(int, int) 
	  */
	private void addComparable(){
		StringBuffer sb_answer = new StringBuffer(); 

		//get the indexes from the lists
		int[] srcSelect = srcList.getSelectedIndexes();
		int[] trgSelect = trgList.getSelectedIndexes();

		for (int i = 0; i< srcSelect.length; i++){
			srcList.deselect(srcSelect[i]);

			for (int j= 0 ; j < trgSelect.length; j++){
				trgList.deselect(trgSelect[j]);			

				//tries to add the new pair and gets the answer
				error = comparableFragments.add(srcSelect[i], trgSelect[j], 
						srcList.getItem(srcSelect[i]), trgList.getItem(trgSelect[j]));
				if (error.getID() == 0 || error.getID() == 2){//new pair accepted
					history.pushComparable(srcSelect[i], trgSelect[j], 
								formatLog(srcList.getItem(srcSelect[i])), 
								formatLog(trgList.getItem(trgSelect[j])) );
					refreshLog();	
				}		
				//refresh the error message (empty if everything's fine)
				errorMessage.setText(error.getText());
				sb_answer.delete(0, sb_answer.length());																	
			}
		}
	}	
	
	/** 
	 * Undoes the last operation (either the addition of a parallel or
	 * comparable pair). 
	 */
	private void undo(){
		if (history.isEmpty())
			errorMessage.setText("Nothing to undo");
		else {
			//We get the data from the last operation
			Map<String, Integer> rec = history.pop();	
			if (rec.get("kind") == 1) //it is parallel  
				parallelFragments.remove(rec.get("id_src"), 
										rec.get("id_trg") );	
			 else 		//It's comparable
				comparableFragments.remove(rec.get("id_src"), 
										rec.get("id_trg") );	
			
			errorMessage.setText("Last operation undone");
			refreshLog();
		}			
	}
	
	/**
	 * @return true of either the comparable or parallel sets changes
	 */
	private boolean checkIfChanged(){
		if (tracker.parallelChanged(parallelFragments) 
			|| tracker.comparableChanged(comparableFragments)){	
			
			return true;	
		}
		return false;		
	}
	
	
	/**
	 * TODOuse also one with line,line,parallel/comparable
	 */
	private void save() {
		savePAN();
		savePlain();
	}
	
	/** Saves the alignment for the current pair of documents in PAN format. */
	private void savePAN(){
		//agregar longitud de los documents
		//agregar primera línea del documento
		WikiAnn2PAN ann2pan = new WikiAnn2PAN();
		DocumentAnnotation 
		annDoc = ann2pan.convert2pan(String.valueOf(currentTrgID),
				String.valueOf(currentTrgID),
				String.valueOf(currentSrcID),
				parallelFragments, 
				comparableFragments,
				srcText,
				trgText);

		XMLwriterAlignment  xmlWriter = new XMLwriterAlignment(
				srcLan, trgLan, 
				srcList.getItemCount(),	trgList.getItemCount(), 
				srcList.getItem(0), trgList.getItem(0));
		
		xmlWriter.exportPlagiarismCasesXML(getXmlFileName(), annDoc);
		
		updateTracker();
	}
	
	/** 
	 * Saves the alignment for the current pair of documents in tab-separated
	 * csv format. 
	 */
	private void savePlain(){
		String[][] values = WikiAnn2Plain.get(parallelFragments, 
							comparableFragments);
		CsvFoolWriter writer = new CsvFoolWriter("\t");
		writer.matrix2csv(values, new File(getPlainFileName()));		
	}

	/** 
	 * Save the hash values for the current parallel and comparable 
	 * sentences. It has to be called if a new document is loaded or 
	 * the current progress is saved. In that way, the program can 
	 * alert the user if some modification is not saved.
	 */
	private void updateTracker(){
		 tracker.setComparable(comparableFragments);
		 tracker.setParallel(parallelFragments);
	 }
	
	/**Loads the source text from the file	*/
	private void loadSourceText(){
		 srcList.removeAll();
		 try {
			srcText = FileIO.fileToString(
					 	new File(getArticleFname(srcLan, currentSrcID)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 for ( String line : srcText.split("\n") )
			 srcList.add(line);
	}
	
	/**Loads the target text from the file */
	private void loadTargetText(){
		 trgList.removeAll();
		 try {
			trgText = FileIO.fileToString(
					 		new File (getArticleFname(trgLan, currentTrgID)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		 for (String line : trgText.split("\n") )
			 trgList.add(line);
	 }
	 
	/**
	* @param lan
	* @param id
	* @return name of the file with the article contents
	*/
	private String getArticleFname(Locale lan, int id){
		//JOSU's FORMAT
		return String.format("%s%s%d.%s.txt", 
				articlesPath,
				File.separator, 
				id,
				lan);
		
		
		//ORIGINAL FORMAT
//		return String.format("%s%s%s_%d.txt", 
//				articlesPath,
//				File.separator, 
//				lan, 
//				id);
	 }
	
	/**
	 * The output file name is composed as follows:
	 * <br>
	 * path/[trg_language].[trg_id].xml
	 * 
	 * @return name of the XML file for this case.
	 *  
	 */
	public String getXmlFileName(){
		return String.format("%s%s%s.%s.xml",
							articlesPath, 
							File.separator, 
							currentTrgID, 
							trgLan);		
	}
	
	public String getPlainFileName(){
		return String.format("%s%s%s.%s.ann.csv",
				articlesPath, 
				File.separator, 
				currentTrgID, 
				trgLan);
	}
	
		
	/**Updates the operations log*/
	protected void refreshLog(){
		if (history.size() > 0){
			lastAdded.setText("Last added:" + history.lastKind());
			srcLast.setText(history.lastTxtSrc());		
			trgLast.setText(history.lastTxtTrg());
		} else {
			lastAdded.setText("Last added:");
			srcLast.setText(".");		
			trgLast.setText(".");
		}
	}	

	protected String formatLog(String text){
		StringBuffer sb = new StringBuffer("    ");
		sb.append(text);	
		return sb.substring(0, Math.min(40, sb.length()))
				 .toString();
	}
	
	private Object[] getPossibilities(){
		//System.out.println(article_id_title);
		pairs = cvs2Pairs(articleIdTitle); 
		Map<Integer, String> pos = new TreeMap<Integer, String>();
		for (ArticlePair p : pairs){
			pos.put(p.srcID, 
					String.format("%d::\t%s-%s", 
							p.srcID,  
							p.srcTitle, 
							p.trgTitle));			
			//p.srcID + "::\t" + p.srcTitle + " - " + p.trgTitle); /
		}
		
		String[] options = new String[pos.size()];
		int i = 0;
		for (int opt: pos.keySet()){
			options[i++] = pos.get(opt);
		}
		
		return options;
	}
	
	/**
	 * Reads the csv with pairs generated by AGettingPairs (in
	 * file f) and loads the pairs into List<Pair>
	 * @param f file with ids and titles. It should be tab-separated
	 */
	public static java.util.List<ArticlePair> cvs2Pairs(String f){
		java.util.List<ArticlePair> pairs = new ArrayList<ArticlePair>();
		Map<String, String>  ids = new TreeMap<String, String>();

		String[][] fields = CsvFoolReader.csv2matrix(new File(f), "\t");
		
		for (int i = 0 ; i < fields.length; i ++){
	        ids.put(fields[i][0], fields[i][2]);
	        
	        pairs.add(new ArticlePair(
	        		Integer.parseInt(fields[i][0]),	//src_id 
	        		fields[i][1], 					//src_tit
	        		Integer.parseInt(fields[i][2]), //trg_id
	        		fields[i][3]));					//trg_tit
	    }
//	    saveIndex(ids);
	    return pairs;
	}

	
	//WITH THE OLD CSV READER (openCSV)
//	public static java.util.List<ArticlePair> cvs2Pairs(String f) throws IOException{
//		java.util.List<ArticlePair> pairs = new ArrayList<ArticlePair>();
//		CSVReader reader = new CSVReader(new FileReader(f));
//		Map<String, String>  ids = new TreeMap<String, String>();
//		String [] nextLine;
//	    while ((nextLine = reader.readNext()) != null) {
//	        // nextLine[] is an array of values from the line
//	    	//TODO make an extension of opencsv to ignore lines with heading #
//	    	if (nextLine[0].substring(0, 1).equals("#"))
//	    		continue;
//	       // System.out.println(nextLine[0] + "|" + nextLine[1] + "|" + nextLine[2] + "|" + nextLine[3]);
//	        
//	        ids.put(nextLine[0], nextLine[2]);
//	        
//	        pairs.add(new ArticlePair(Integer.parseInt(nextLine[0]),	//src_id 
//	        					nextLine[1], 					//src_tit
//	        					Integer.parseInt(nextLine[2]), 	//trg_id
//	        					nextLine[3]));					//trg_tit
//	    }
////	    saveIndex(ids);
//	    return pairs;
//	}
	
	public JPanel getPanel(){
		return panelText;
	}
	
	
	
	

}