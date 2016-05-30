import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;

public class dataReader extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JLabel filenameLabel = new JLabel();
	
	private int rowcount;
	private int columncount;
	private String[] columnname;
	private Object data[][];

	/**
	 * Create the dialog.
	 */
	public dataReader(String title) {
		setTitle(title);
		setBounds(120, 120, 600, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(5,5));
		
		contentPanel.add(filenameLabel, BorderLayout.NORTH);
		{
			table = new JTable();
			JScrollPane scrollpane = new JScrollPane(table);
			contentPanel.add(scrollpane, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.setActionCommand("Close");
				closeButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				buttonPane.add(closeButton);
			}
		}
	}
	
	public void setData(int rowCount, int columnCount, Object data[][]){
		rowcount = rowCount;
		columncount = columnCount;
		this.data = data;
		columnname = new String[columncount];
		table.setModel(new tableData());
	}
	
	public void setData(int rowCount, int columnCount, Object data[][], String columnName[]){
		rowcount = rowCount;
		columncount = columnCount;
		this.data = data;
		columnname = columnName;
		table.setModel(new tableData());
	}
	
	public void setFileName(String filename){
		filenameLabel.setText(filename);
	}
	
	private class tableData extends AbstractTableModel
	{
		
		public int getRowCount() {
			return rowcount;
		}

		public int getColumnCount() {
			return columncount;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
		public String getColumnName(int column){
			return columnname[column];
		}
	}

}
