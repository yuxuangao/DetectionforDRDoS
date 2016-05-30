
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class DevicesCellRenderer extends JPanel implements ListCellRenderer<Object>{

	public DevicesCellRenderer(){
		this.setOpaque(true);
	}
	
	public Component getListCellRendererComponent
	(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT,5,10));
		
		ImageIcon icon = new ImageIcon("netcardicon.png");
		icon.setImage(icon.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
		JLabel iconlabel = new JLabel(icon);
		
		JPanel textpane = new JPanel();
		textpane.setLayout(new BoxLayout(textpane, BoxLayout.Y_AXIS));
		String[] stringtemp = value.toString().split("\n");
		JLabel[] labelgroup = new JLabel[stringtemp.length];
		for (int i=0;i<stringtemp.length;i++){
			labelgroup[i] = new JLabel(stringtemp[i]);
			if (list.isEnabled())
				labelgroup[i].setForeground(isSelected ? Color.white : Color.black);
			else
				labelgroup[i].setForeground(isSelected ? Color.white : Color.gray);
			textpane.add(labelgroup[i]);
			textpane.setBackground(null);
		}
		
		this.removeAll();
		this.add(iconlabel);
		this.add(textpane);
		if (list.isEnabled())
			setBackground(isSelected ? new Color(120,120,255) : Color.white);
		else
			setBackground(isSelected ? Color.gray : Color.white);
		
		return this;
	}

}
