package cat.lump.aq.alignment.layout;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A class to display help messages either in plain or html format.
 *  
 * @author albarron
 * @since Mar 2014
 * @version 0.3
 */
class HelpPanel extends JDialog {
	
	static final long serialVersionUID =  1L;
		
	/**File with the help message (html format)*/
	private static final String HELP_HTML_FILE =
			"/cat/lump/aq/alignment/layout/config/help.html";
//			"file://"
//			+ System.getProperty("user.dir")
//			+ System.getProperty("file.separator")
//			+ "files"
//			+ System.getProperty("file.separator")			
//			+ 
			
	
	private static JPanel help;
	
	
	public HelpPanel(JFrame frame){
		super(frame, false);
		setSize(700, 500);
		setName("Help");
		JEditorPane editorPane = null;
		try {			
			editorPane = new JEditorPane(
						HelpPanel.class.getResource(HELP_HTML_FILE));
			editorPane.setEditable(false);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
		help = new JPanel();
		help.setLayout(new BorderLayout()); 
		help.add(editorScrollPane, BorderLayout.CENTER);					
		add(help);			
	}
	
	/**
	 * Pops up an HTML help text including how to proceed and what 
	 * parallel and comparable mean.
	 * @param panelText
	 */
	public void load(JPanel panelText){
		setVisible(true);	
	}

	
}
