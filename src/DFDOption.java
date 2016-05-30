
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JCheckBox;

public class DFDOption extends JDialog{

	private final JPanel contentPanel = new JPanel();
	
	private int xmax;
	private long cycle;
	private boolean isclear;
	
	/**
	 * Create the dialog.
	 */
	public DFDOption(int Xmax, long Cycle, boolean isClear) {
		xmax = Xmax;
		cycle = Cycle;
		isclear = isClear;
		
		setResizable(false);
		setModal(true);
		setMinimumSize(new Dimension(350, 200));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Display Range");
		lblNewLabel.setBounds(16, 25, 97, 16);
		contentPanel.add(lblNewLabel);
		
		final JFormattedTextField rangeField = new JFormattedTextField
			(new InternationalFormatter() {
            protected DocumentFilter getDocumentFilter() {
                return filter;
            }
            private DocumentFilter filter = new IntFilter();
        });
		rangeField.setBounds(125, 20, 210, 26);
		contentPanel.add(rangeField);
		
		JLabel lblNewLabel_1 = new JLabel("Cycle (ms)");
		lblNewLabel_1.setBounds(16, 60, 97, 16);
		contentPanel.add(lblNewLabel_1);
		
		final JFormattedTextField cycleField = new JFormattedTextField
			(new InternationalFormatter() {
			protected DocumentFilter getDocumentFilter() {
			return filter;
			}
			private DocumentFilter filter = new IntFilter();
		});
		cycleField.setBounds(125, 55, 210, 26);
		contentPanel.add(cycleField);
		
		final JCheckBox clearCheck = new JCheckBox("Start with Clear");
		clearCheck.setBounds(12, 90, 128, 23);
		contentPanel.add(clearCheck);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						xmax = Integer.parseInt(rangeField.getText());
						cycle = Long.parseLong(cycleField.getText());
						isclear = clearCheck.isSelected();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				okButton.setPreferredSize(new Dimension(90,29));
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				cancelButton.setPreferredSize(new Dimension(90,29));
				buttonPane.add(cancelButton);
			}
		}
		
		this.addWindowListener(new WindowAdapter(){
			public void windowActivated(WindowEvent e){
				rangeField.setText(String.valueOf(xmax));
				cycleField.setText(String.valueOf(cycle));
				clearCheck.setSelected(isclear);
			}
		});
	}
	
	public int getXmax(){
		return xmax;
	}
	
	public long getCycle(){
		return cycle;
	}
	
	public boolean isClear(){
		return isclear;
	}
	
	class IntFilter extends DocumentFilter {
		public void insertString(FilterBypass fb, int offset, String string,
				AttributeSet attr) throws BadLocationException {
			StringBuilder builder = new StringBuilder(string);
			for (int i = builder.length() - 1; i >= 0; i--) {
				int cp = builder.codePointAt(i);
				if (!Character.isDigit(cp) && cp != '-') {
					builder.deleteCharAt(i);
					if (Character.isSupplementaryCodePoint(cp)) {
						i--;
						builder.deleteCharAt(i);
					}
				}
			}
			super.insertString(fb, offset, builder.toString(), attr);
		}
		public void replace(FilterBypass fb, int offset, int length, String string,
				AttributeSet attr) throws BadLocationException {
			if (string != null) {
				StringBuilder builder = new StringBuilder(string);
				for (int i = builder.length() - 1; i >= 0; i--) {
					int cp = builder.codePointAt(i);
					if (!Character.isDigit(cp) && cp != '-') {
						builder.deleteCharAt(i);
						if (Character.isSupplementaryCodePoint(cp)) {
							i--;
							builder.deleteCharAt(i);
						}
					}
				}
				string = builder.toString();
			}
			super.replace(fb, offset, length, string, attr);
		}
	}
}
