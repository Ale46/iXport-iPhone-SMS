import java.awt.Desktop;
import java.awt.EventQueue;

import java.util.Vector;

import javax.swing.JFrame;


import javax.swing.JFileChooser;
import javax.swing.JLabel;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import javax.swing.JSeparator;
import java.awt.Color;



public class iXportSMS {

	private JFrame frmIxportIphoneSms;
	private BackupManager bm = new BackupManager();
	private SmsDB s = new SmsDB();
/*	private JComboBox<String> cmbSendReceived;*/
	private JComboBox<String> cmbBackup;
	private JComboBox<String> cmbNumber;
	private Backup[] bs;
	private DefaultComboBoxModel<String> modelBackup = new DefaultComboBoxModel<String>();
	private DefaultComboBoxModel<String> modelNumber = new DefaultComboBoxModel<String>();
	private Backup b;
	
	private void loadLib() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException{
		Class<ClassLoader> loaderClass = ClassLoader.class;

		Field userPaths = loaderClass.getDeclaredField("sys_paths");
		userPaths.setAccessible(true);
		userPaths.set(null, null);


		//e aggiungo il percorso della cartella con le dll
		String libFolderPath = "libs";
		System.out.println(libFolderPath);
		//aggiungo la nuova cartella alla variabile java.library.path
		String libraryPath = System.getProperty("java.library.path")
				.concat(";" + libFolderPath);
		System.setProperty("java.library.path", libraryPath);
	}

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					iXportSMS window = new iXportSMS();
					window.frmIxportIphoneSms.setLocationRelativeTo(null);
					window.frmIxportIphoneSms.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws URISyntaxException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public iXportSMS() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		loadLib();
		initialize();
		bs = bm.getBackups();
		for (Backup b : bs){
			modelBackup.addElement(b.getName()+" ("+b.getDate()+")");
		}

		modelBackup.addElement("Custom sms database");

		

		


	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmIxportIphoneSms = new JFrame();
		frmIxportIphoneSms.setIconImage(Toolkit.getDefaultToolkit().getImage(iXportSMS.class.getResource("/images/icon.png")));
		frmIxportIphoneSms.setTitle("iXport iPhone SMS");
		frmIxportIphoneSms.setResizable(false);
		frmIxportIphoneSms.setBounds(100, 100, 575, 360);
		frmIxportIphoneSms.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(0, 0, 567, 85);
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/images/header.png")));

		cmbNumber = new JComboBox<String>(modelNumber);
		cmbNumber.setBounds(285, 156, 274, 20);

		JLabel lblNewLabel_1 = new JLabel("Choose what conversation to export");
		lblNewLabel_1.setBounds(45, 162, 230, 14);

/*		cmbSendReceived = new JComboBox<String>();
		cmbSendReceived.setBounds(285, 207, 274, 20);
		cmbSendReceived.setModel(new DefaultComboBoxModel<String>(new String[] {"All", "Only Sended", "Only Received"}));*/

		JLabel lblSendreceived = new JLabel("Send\\Received");
		lblSendreceived.setBounds(45, 213, 72, 14);

		JLabel lblNewLabel_2 = new JLabel("Select Backup");
		lblNewLabel_2.setBounds(45, 115, 124, 14);

		cmbBackup = new JComboBox<String>(modelBackup);
		cmbBackup.setBounds(285, 112, 274, 20);
		cmbBackup.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				System.out.println(arg0.getStateChange());
				if(arg0.getStateChange()==1){
					if (cmbBackup.getItemAt(cmbBackup.getSelectedIndex()).equals("Custom sms database")){
						
						String smsdb = showOpenDialog();
						System.out.println(smsdb);
						if (smsdb != null){
							b = new Backup("loaded", smsdb, null, null);
							s.loadBackup(b,true);
						}else{
							modelNumber.removeAllElements();
							return;
						}
						

					}else{
						b = bs[cmbBackup.getSelectedIndex()];
						s.loadBackup(bs[cmbBackup.getSelectedIndex()], false);

					}
					Vector<String> nums = s.getAllSMSNumbers();
					modelNumber.removeAllElements();
					modelNumber.addElement("All");
					for (String num : nums)
						modelNumber.addElement(num);
				}
			}
		});

								JButton btnExportTxt = new JButton("Export TXT");
								btnExportTxt.setBounds(477, 298, 85, 23);
								btnExportTxt.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										Backup b = bs[cmbBackup.getSelectedIndex()];
										Exporter e = null;
										try {
											String number = cmbNumber.getSelectedItem().toString();
											String path = showSaveDialog();
											if (path == null)
												return;
											if (number.contains("All")){

												e = new Exporter(s.getAllSMS(), path+".txt",b.getNumber());
												e.setMessage("Conversation Between Me and All");


											}else{

												e = new Exporter(s.getConversationSMS(number), path+".txt",b.getNumber());
												e.setMessage("Conversation Between Me and "+number);


											}
											e.exportAsTXT();
											showCompleteMessage();
										} catch (IOException e1) {

											e1.printStackTrace();
										}
									}
								});

								JButton btnExportPdf = new JButton("Export CSV");
								btnExportPdf.setBounds(384, 298, 87, 23);
								btnExportPdf.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										Backup b = bs[cmbBackup.getSelectedIndex()];
										Exporter e = null;
										try {
											String number = cmbNumber.getSelectedItem().toString();
											String path = showSaveDialog();
											if (path == null)
												return;
											if (number.contains("All")){

												e = new Exporter(s.getAllSMS(), path+".csv",b.getNumber());
												e.setMessage("Conversation Between Me and All");


											}else{


												e = new Exporter(s.getConversationSMS(number), path+".csv",b.getNumber());
												e.setMessage("Conversation Between Me and "+number);

											}
											e.exportAsCSV();
											showCompleteMessage();
										} catch (IOException e1) {

											e1.printStackTrace();
										}
									}
								});

								JButton btnExportHmtl = new JButton("Export HMTL");
								btnExportHmtl.setBounds(285, 298, 93, 23);
								btnExportHmtl.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										// = bs[cmbBackup.getSelectedIndex()];
										Exporter e = null;
										try {
											String number = cmbNumber.getSelectedItem().toString();
											String path = showSaveDialog();
											if (path == null)
												return;
											if (number.contains("All")){


												e = new Exporter(s.getAllSMS(), path+".html",b.getNumber());
												e.setMessage("Conversation Between Me and All");

											}else{

												e = new Exporter(s.getConversationSMS(number), path+".html",b.getNumber());
												e.setMessage("Conversation Between Me and "+number);




											}
											e.exportAsHTML();
											showCompleteMessage();
										} catch (IOException e1) {

											e1.printStackTrace();
										}
									}
								});

								JLabel lblNewLabel_3 = new JLabel("");
								lblNewLabel_3.setBounds(10, 107, 32, 32);
								lblNewLabel_3.setIcon(new ImageIcon(getClass().getResource("/images/Backup-round-32.png")));

								JLabel label = new JLabel("");
								label.setBounds(10, 152, 32, 32);
								label.setIcon(new ImageIcon(getClass().getResource("/images/Phone-Book-32.png")));

								/*JLabel label_1 = new JLabel("");
								label_1.setBounds(10, 205, 32, 32);
								label_1.setIcon(new ImageIcon(getClass().getResource("/images/Gnome-Mail-Send-Receive-32.png")));*/
								frmIxportIphoneSms.getContentPane().setLayout(null);

								JLabel lblNewLabel_4 = new JLabel("New label");
								lblNewLabel_4.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent arg0) {
										try {
											openURL("https://twitter.com/#!/Ale467");
										} catch (IOException e) {

											e.printStackTrace();
										} catch (URISyntaxException e) {

											e.printStackTrace();
										}
									}
								});
								lblNewLabel_4.setIcon(new ImageIcon(iXportSMS.class.getResource("/images/twitter.png")));
								lblNewLabel_4.setBounds(440, 21, 64, 64);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel_4);

								JLabel lblNewLabel_5 = new JLabel("New label");
								lblNewLabel_5.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										try {
											openURL("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=RFMAKH4HU66G2&lc=IT&item_name=Thanks%20for%20iXport%20iPhone%20SMS&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted");
										} catch (IOException e1) {

											e1.printStackTrace();
										} catch (URISyntaxException e1) {

											e1.printStackTrace();
										}
									}
								});
								lblNewLabel_5.setIcon(new ImageIcon(iXportSMS.class.getResource("/images/donate.png")));
								lblNewLabel_5.setBounds(503, 21, 64, 64);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel_5);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel);
								frmIxportIphoneSms.getContentPane().add(btnExportHmtl);
								frmIxportIphoneSms.getContentPane().add(btnExportPdf);
								frmIxportIphoneSms.getContentPane().add(btnExportTxt);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel_3);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel_2);
								frmIxportIphoneSms.getContentPane().add(label);
								frmIxportIphoneSms.getContentPane().add(lblNewLabel_1);
							/*	frmIxportIphoneSms.getContentPane().add(label_1);*/
								/*frmIxportIphoneSms.getContentPane().add(lblSendreceived);*/
								frmIxportIphoneSms.getContentPane().add(cmbNumber);
								frmIxportIphoneSms.getContentPane().add(cmbBackup);
								
								JSeparator separator = new JSeparator();
								separator.setBackground(Color.BLACK);
								separator.setBounds(10, 261, 552, -8);
								frmIxportIphoneSms.getContentPane().add(separator);
								/*frmIxportIphoneSms.getContentPane().add(cmbSendReceived);*/
	}

	private String showSaveDialog() {
		File file;
		String filename;
		JFileChooser fc = new JFileChooser();


		int rc = fc.showDialog(null, "Select file");

		if (rc == JFileChooser.APPROVE_OPTION){

			file = fc.getSelectedFile();

			filename = file.getAbsolutePath();
			return filename;

		}else {
			return null;
		}
	}
	
	private String showOpenDialog(){
		JFileChooser fc = new JFileChooser();
		int rc = fc.showDialog(null, "Select Data File");
		if (rc == JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			
			return file.getAbsolutePath();
		}
		return null;
	}

	private void showCompleteMessage(){
		JOptionPane.showMessageDialog(null, "Export complete");
	}

	private  void openURL (String url) throws IOException, URISyntaxException{
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE)){
			desktop.browse(new URI(url) );
		}
	}
}
