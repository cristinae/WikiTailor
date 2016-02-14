package cat.lump.aq.alignment.layout;

import java.awt.BorderLayout;
import java.awt.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import cat.lump.aq.alignment.fragment.AlignmentLevel;

/**
 * A class that displays a summary of the current annotation
 * for the article's pair.  
 * @author albarron
 * @since Mar 2014
 * @version 0.3
 */
class SummaryPannel {

	private final static String columnNames[] =
		{"kind", "src_id", "Source text", "", "Target text", "trg_id"};
	
	/** Pops up a summary of comparable and parallel sentences */
	public static void display(JPanel panelText,
				AlignmentLevel parallelFragments, 
				AlignmentLevel comparableFragments,
				List srcList, List trgList){
		
		String tabledata[][] = new String[parallelFragments.size() 
		                                  + comparableFragments.size()]
		                                [6];		

		int i = 0;
		for (Integer[] pair : parallelFragments.getAllids())		{
			tabledata[i][0] = "Parallel";
			tabledata[i][1] = String.valueOf(pair[0]);
			tabledata[i][2] = srcList.getItem(pair[0]);
			tabledata[i][3] = " - ";
			tabledata[i][4] = trgList.getItem(pair[1]);;
			tabledata[i][5] = String.valueOf(pair[1]);
			i++;			
		}
		
		for (Integer[] pair : comparableFragments.getAllids())		{
			tabledata[i][0] = "Comparable";
			tabledata[i][1] = String.valueOf(pair[0]);
			tabledata[i][2] = srcList.getItem(pair[0]);
			tabledata[i][3] = " - ";
			tabledata[i][4] = trgList.getItem(pair[1]);;
			tabledata[i][5] = String.valueOf(pair[1]);
			i++;
		}
		generateSummary(panelText, tabledata);
	}
	
	private static void generateSummary(JPanel panelText, String[][] tabledata){

		JTable tb_sum = new JTable(tabledata, columnNames);
		TableColumn col;
		tb_sum.setEnabled(false);
		tb_sum.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);		
		tb_sum.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment( JLabel.LEFT );

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
		
		//kind
		col = tb_sum.getColumnModel().getColumn(0);		
		col.setPreferredWidth(85);
		//src and trg ids
		col = tb_sum.getColumnModel().getColumn(1);
		col.setCellRenderer(centerRenderer);		col.setPreferredWidth(45);
		
		col = tb_sum.getColumnModel().getColumn(5);
		col.setCellRenderer(centerRenderer);		col.setPreferredWidth(45);
		
		//src and trg texts
		col = tb_sum.getColumnModel().getColumn(2);
		col.setCellRenderer(rightRenderer);		col.setPreferredWidth(350);
		
		col = tb_sum.getColumnModel().getColumn(4);
		col.setPreferredWidth(350);
		
		//dash 
		col = tb_sum.getColumnModel().getColumn(3);
		col.setPreferredWidth(10);
		JPanel summary = new JPanel();
		summary.setLayout(new BorderLayout()); // unless already there
		summary.add(tb_sum, BorderLayout.CENTER);
		summary.add(tb_sum.getTableHeader(), BorderLayout.NORTH);
		summary.setSize(2800,400);
		
		JOptionPane.showMessageDialog(
				panelText, 
				summary, 
				"Annotation summary", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	
}
