
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

import java.awt.Dimension;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
public class DFD extends JFrame {

	private JPanel maincontentPane;
	final private JList<String> list = new JList<String>();
	private JPanel IPgraphPane = new JPanel();
	private JPanel UsizegraphPane = new JPanel();
	private JPanel UnumbergraphPane = new JPanel();
	private JPanel dUnumbergraphPane = new JPanel();
	private JPanel portgraphPane = new JPanel();
	private JLabel dstatusLabel = new JLabel("Status:");
	private JLabel statusLabel = new JLabel("Status: Stopped");
	
	private coTask cotask;
	private timingTask timingtask;
	private detectionTask detectiontask;
	
	private int IPnumber = 0;
	private int UDPsize = 0;
	private int UDPnumber = 0;
	private int dUDPnumber = 0;
	private int[] port = new int[65536];
	
	private String log = "         Start Time                      End Time              Status      Lasting";
	private boolean laststatus = false;
	private Date startdate;
	private Date enddate;
	
	private int xmax = 100;
	private long cycle = 1000;
	private boolean isclear = true;
	private DFDOption option = new DFDOption(xmax,cycle,isclear);
	
	private dataReader trainingdata = new dataReader("Training Data");
	private textDisplay logdisplay = new textDisplay("Log");
	private textDisplay errordisplay = new textDisplay("Error Log");
	private String errorlog = "";
	
	private DFDChart ipnumberchart = new DFDChart();
	private DFDChart udpsizechart = new DFDChart();
	private DFDChart udpnumberchart = new DFDChart();
	private DFDChart dudpnumberchart = new DFDChart();
	private DFDChart portchart = new DFDChart();
	
	
	final private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
	private JpcapCaptor captor;
	
	private Detection detection;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					DFD frame = new DFD();
					frame.setVisible(true);
				}
				catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				catch (InstantiationException e1) {
					e1.printStackTrace();
				}
				catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				catch (UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DFD() {
		setMinimumSize(new Dimension(600, 400));
		setTitle("DectionForDRDoS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, 900, 500);
		maincontentPane = new JPanel();
		maincontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		maincontentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(maincontentPane);
		
		errordisplay.setBounds(200, 200, errordisplay.getWidth(), errordisplay.getHeight());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		maincontentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel setPane = new JPanel();
		tabbedPane.addTab("Config", null, setPane, null);
		setPane.setLayout(new BorderLayout(0, 0));
		
		JPanel listPane = new JPanel();
		setPane.add(listPane, BorderLayout.CENTER);
		listPane.setLayout(new BorderLayout(0, 0));
		
		
		//list value
		Vector<String> listvalue = new Vector<String>();
		
		for (int i=0;i<devices.length;i++)
		{
			String tempstring = devices[i].name+"\n";
			tempstring += devices[i].datalink_name+"\n";
			tempstring += "MAC: ";
            for(int j=0;j<devices[i].mac_address.length;j++){
                tempstring += Integer.toHexString(devices[i].mac_address[j]&0xff) + ":";
            }
            tempstring = tempstring.substring(0, tempstring.length()-1);
            tempstring += "\nIP: ";
            for(int j=0;j<devices[i].addresses.length;j++){
            	tempstring += j==0?"":"    ";
            	String add = devices[i].addresses[j].address+"\n";
            	add = add.replaceAll("/", "");
            	add = add.replaceAll("%0", "");
            	tempstring += add;
            }
            tempstring = tempstring.substring(0, tempstring.length()-1);
			listvalue.addElement(tempstring);
		}
		//list value end
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setListData(listvalue);
		list.setCellRenderer(new DevicesCellRenderer());
		list.setSelectedIndex(0);
		
		JScrollPane scrollPane = new JScrollPane(list);
		listPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setPreferredSize(new Dimension(130, 10));
		listPane.add(buttonPane, BorderLayout.EAST);
		
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane,BoxLayout.X_AXIS));
		setPane.add(labelPane,BorderLayout.NORTH);
		final JLabel chooseLabel = new JLabel("choose");
		labelPane.add(chooseLabel);
		
		
		setPane.add(statusLabel, BorderLayout.SOUTH);
		
		JPanel displayPane = new JPanel();
		tabbedPane.addTab("Display", null, displayPane, null);
		displayPane.setLayout(new BorderLayout(0, 0));
		
		JPanel graphPane = new JPanel();
		displayPane.add(graphPane, BorderLayout.CENTER);
				
		IPgraphPane.setLayout(new BorderLayout(0, 0));
		UsizegraphPane.setLayout(new BorderLayout(0, 0));
		UnumbergraphPane.setLayout(new BorderLayout(0, 0));
		dUnumbergraphPane.setLayout(new BorderLayout(0, 0));
		portgraphPane.setLayout(new BorderLayout(0, 0));
		IPgraphPane.add(ipnumberchart.getChartPanel(), BorderLayout.CENTER);
		UsizegraphPane.add(udpsizechart.getChartPanel(), BorderLayout.CENTER);
		UnumbergraphPane.add(udpnumberchart.getChartPanel(), BorderLayout.CENTER);
		dUnumbergraphPane.add(dudpnumberchart.getChartPanel(), BorderLayout.CENTER);
		portgraphPane.add(portchart.getChartPanel(), BorderLayout.CENTER);
		graphPane.setLayout(new BoxLayout(graphPane, BoxLayout.Y_AXIS));
		graphPane.add(IPgraphPane);	
		graphPane.add(UsizegraphPane);	
		graphPane.add(UnumbergraphPane);				
		graphPane.add(dUnumbergraphPane);
		graphPane.add(portgraphPane);
		
		displayPane.add(dstatusLabel, BorderLayout.SOUTH);
		
		final JButton chooseButton = new JButton("Start");
		final JButton stopButton = new JButton("Stop");
		final JButton clearButton = new JButton("Log Clear");
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseLabel.setText("choose: "+devices[list.getSelectedIndex()].name);
				list.setEnabled(false);
				chooseButton.setEnabled(false);
				stopButton.setEnabled(true);
				clearButton.setEnabled(false);
				statusLabel.setText("Status: Running...");
				if (isclear){
					ipnumberchart.clear();
					udpsizechart.clear();
					udpnumberchart.clear();
					dudpnumberchart.clear();
					portchart.clear();
				}
				startdate = new Date();
				enddate = startdate;
				log = log+String.format("%n%tF %tT   %tF %tT   OK        0",startdate,startdate,startdate,startdate);
				if (logdisplay.isVisible())
					logdisplay.setText(log);
				cotask = new coTask();
				timingtask = new timingTask();
				cotask.execute();
				timingtask.execute();
			}
		});
		chooseButton.setPreferredSize(new Dimension(120, 29));
		buttonPane.add(chooseButton);
		
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				captor.breakLoop();
				timingtask.isEnabled = false;
				list.setEnabled(true);
				chooseButton.setEnabled(true);
				chooseLabel.setText("choose");
				stopButton.setEnabled(false);
				clearButton.setEnabled(true);
				statusLabel.setText("Status: Stopped");
				dstatusLabel.setText("Status:");
				laststatus = false;
			}
		});
		stopButton.setPreferredSize(new Dimension(120, 29));
		stopButton.setEnabled(false);
		buttonPane.add(stopButton);
		
		JButton dataButton = new JButton("Training Data");
		dataButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					trainingdata.setData
						(detection.getnumTrainingInstances(), detection.getnumTrainingClasses(), 
								detection.getTrainingData(),detection.getClassesName());
				} catch (Exception e1) {
					keepErrorLog(e1);
				}
				trainingdata.setVisible(!trainingdata.isVisible());
			}
		});
		dataButton.setPreferredSize(new Dimension(120,29));
		buttonPane.add(dataButton);
		
		JButton fileButton = new JButton("Training File");
		fileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser filechooser = new JFileChooser();
				filechooser.setFileFilter(new FileFilter(){
					public boolean accept(File file) {
						if(file.getName().endsWith("arff")||file.isDirectory())
							return true;
						return false;
					}
					public String getDescription() {
						return "data file(*.arff)";
					}
				});
				int tempvalue = filechooser.showOpenDialog(null);
				if (tempvalue==JFileChooser.APPROVE_OPTION){
					try {
						detection.setTrainFileAddress(filechooser.getSelectedFile().getAbsolutePath());
						trainingdata.setData
							(detection.getnumTrainingInstances(), detection.getnumTrainingClasses(), 
								detection.getTrainingData(),detection.getClassesName());
						trainingdata.setFileName(detection.getTrainFileName());
					} catch (Exception e1) {
						keepErrorLog(e1);
					}
				}
			}
		});
		fileButton.setPreferredSize(new Dimension(120,29));
		buttonPane.add(fileButton);
		
		JButton optionButton = new JButton("Option");
		optionButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				option.pack();
				option.setVisible(true);
				xmax = option.getXmax();
				cycle = option.getCycle();
				isclear = option.isClear();
				ipnumberchart.setXmax(xmax);
				udpsizechart.setXmax(xmax);
				udpnumberchart.setXmax(xmax);
				dudpnumberchart.setXmax(xmax);
				portchart.setXmax(xmax);
			}
		});
		optionButton.setPreferredSize(new Dimension(120,29));
		buttonPane.add(optionButton);
		
		JButton logButton = new JButton("Log");
		logButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logdisplay.setText(log);
				logdisplay.setVisible(!logdisplay.isVisible());
			}
		});
		logButton.setPreferredSize(new Dimension(120,29));
		buttonPane.add(logButton);
		
		for (int i=0;i<65536;i++)
			port[i] = 0;
		
		try {
			detection = new Detection("5chargen-120-171-212-192.168.1.32-ip-usr-unr-dun-po.arff");
			trainingdata.setFileName(detection.getTrainFileName());
		} catch (Exception e1) {
			keepErrorLog(e1);
		}
		
		clearButton.setPreferredSize(new Dimension(120, 29));
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				log = "         Start Time                      End Time              Status      Lasting";
				if (logdisplay.isVisible())
					logdisplay.setText(log);
			}
		});		
		buttonPane.add(clearButton);
		
		JButton errorButton = new JButton("Error Log");
		errorButton.setPreferredSize(new Dimension(120,29));
		errorButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				errordisplay.setVisible(!errordisplay.isVisible());
			}
		});
		buttonPane.add(errorButton);
		
		JButton exitButton = new JButton("Exit");
		exitButton.setPreferredSize(new Dimension(120,29));
		exitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		buttonPane.add(exitButton);
		
	}

//captor thread
	class coTask extends SwingWorker<Void,Void>
	{
		public Void doInBackground()
		{
			try {
				captor=JpcapCaptor.openDevice(devices[list.getSelectedIndex()], 65536, false, 20);
				captor.loopPacket(-1, new PacketPro());
				captor.close();
			} catch (IOException e) {
				keepErrorLog(e);
			}
			return null;
		}
		
		public void done()
		{
			
		}
	}
	
//packet process
	private class PacketPro implements PacketReceiver
	{
		public void receivePacket(Packet pack) 
		{
			if (pack instanceof IPPacket){
				IPPacket ippack = (IPPacket)pack;
				for (int i=0;i<devices[list.getSelectedIndex()].addresses.length;i++)
				{
					if (ippack.src_ip.toString().equals(devices[list.getSelectedIndex()].addresses[i].address.toString()))
					{
						IPnumber += 1;
						if (pack instanceof UDPPacket){
							UDPPacket udppack = (UDPPacket)pack;
							UDPnumber += 1;
							if (udppack.data.length<576)
								IPnumber -= 1;
							UDPsize += udppack.len;
							port[udppack.src_port] += 1;
						}
						else if (pack instanceof TCPPacket){
							TCPPacket tcppack = (TCPPacket)pack;
							port[tcppack.src_port] += 1;
							IPnumber -= 1;
						}
						else if (pack instanceof ICMPPacket){
							IPnumber -= 1;
						}
						break;
					}
					else if (ippack.dst_ip.toString().equals(devices[list.getSelectedIndex()].addresses[i].address.toString()))
					{
						if (pack instanceof UDPPacket){
							dUDPnumber += 1;
						}
						break;
					}
				}
			}
		}
	}
	
//timing
	class timingTask extends SwingWorker<Void,Void>
	{
		public boolean isEnabled = true;
		
		public Void doInBackground()
		{
			while(isEnabled){
				detectiontask = new detectionTask();
				detectiontask.execute();
				try {
					Thread.sleep(cycle);
				} catch (InterruptedException e) {
					keepErrorLog(e);
				}
			}
			
			return null;
		}
		
		public void done()
		{
			
		}
	}
	
//detection and chart
	class detectionTask extends SwingWorker<Void,Void>
	{
		public Void doInBackground()
		{
			dUDPnumber = UDPnumber-dUDPnumber;
			int portnumber=0;
			for (int i=0;i<65536;i++){
				portnumber=port[i]>portnumber?port[i]:portnumber;
				port[i]=0;
			}
			
			ipnumberchart.addData(IPnumber);
			udpsizechart.addData(UDPsize);
			udpnumberchart.addData(UDPnumber);
			dudpnumberchart.addData(dUDPnumber);
			portchart.addData(portnumber);
			
			try {
				/*if ((int)(0+Math.random()*99)<30)
					detection.detect(44, 74444, 44, 44, 44);
				else*/
				detection.detect(IPnumber, UDPsize, UDPnumber, dUDPnumber, portnumber);
			} catch (Exception e) {
				keepErrorLog(e);
			}
			
			IPnumber = 0;
			UDPsize = 0;
			UDPnumber = 0;
			dUDPnumber = 0;
			return null;
		}
		
		public void done()
		{
			dstatusLabel.setText(detection.isAttacked()?"Status: Attacked.":"Status: OK.");
			String[] tempstring = log.split("\n");
			if (laststatus == detection.isAttacked()){
				enddate = new Date();
				log = "";
				for (int i=0;i<tempstring.length-1;i++)
					log += tempstring[i]+"\n";
			}
			else{
				startdate = enddate;
				enddate = new Date();
				log += "\n";
			}
			log += String.format("%tF %tT   %tF %tT   %s   %d",startdate,startdate,enddate,enddate,
						detection.isAttacked()?"Attacked":"OK        ",
								(int)Math.rint((double)(enddate.getTime()-startdate.getTime())/1000));
			laststatus = detection.isAttacked();
			if (logdisplay.isVisible())
				logdisplay.setText(log);
		}
	}
//keep error log
	private void keepErrorLog(Exception e){
		Date date = new Date();
		errorlog += String.format("%tF %tT  %s%n",date,date,e.getMessage());
		errordisplay.setText(errorlog);
	}
}
