import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class textDisplay extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JTextArea textArea = new JTextArea();

	/**
	 * Create the dialog.
	 */
	public textDisplay(String title) {
		setTitle(title);
		setBounds(100, 100, 550, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(15, 15));
		
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton saveButton = new JButton("Save");
				saveButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser filechooser = new JFileChooser();
						filechooser.setFileFilter(new FileFilter(){
							public boolean accept(File file) {
								if(file.getName().endsWith("txt")||file.isDirectory())
									return true;
								return false;
							}
							public String getDescription() {
								return "text file(*.txt)";
							}
						});
						int tempvalue = filechooser.showSaveDialog(null);
						if (tempvalue==JFileChooser.APPROVE_OPTION){
							try{
								File savefile = filechooser.getSelectedFile();
								if (!savefile.exists())
									savefile.createNewFile();
								BufferedWriter bw = new BufferedWriter(new FileWriter(savefile));
								bw.write(textArea.getText());
								bw.close();
							}
							catch (IOException ex){
								ex.printStackTrace();
							}
						}
					}
				});
				saveButton.setPreferredSize(new Dimension(80, 29));
				saveButton.setActionCommand("Save");
				buttonPane.add(saveButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setPreferredSize(new Dimension(80, 29));
				cancelButton.setActionCommand("Close");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void setText(String value){
		textArea.setText(value);
	}

}
