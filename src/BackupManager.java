import java.io.File;


import com.dd.plist.NSDictionary;

import com.dd.plist.PropertyListParser;


public class BackupManager {

	private final String XP_DIR = "\\Application Data\\Apple Computer\\MobileSync\\Backup\\";
	private final String SEVEN_DIR = "\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\";
	private final String MAC_DIR = "/Library/Application Support/MobileSync/Backup/";
	private String BACKUP_DIR;

	public BackupManager(){
		String OS = System.getProperty("os.name");
		String HOME = (System.getProperty("user.home"));
		if (OS.contains("Windows 7")||OS.contains("Windows 8")) {
			BACKUP_DIR = HOME+SEVEN_DIR;
		}else if (OS.contains("Windows XP")) {
			BACKUP_DIR = HOME+XP_DIR;
		}else{
			BACKUP_DIR = HOME+MAC_DIR;
		}
	}

	public Backup[] getBackups(){
		String [] bdirs = new File(BACKUP_DIR).list();
		Backup[] backups = new Backup[bdirs.length];
		for  (int i = 0;i<bdirs.length;i++){
			String path = BACKUP_DIR+bdirs[i];
			String name = getName(path);
			String date = getDate(path);
			String number = getNumber(path);
			backups[i] = new Backup(name,path,date,number);
		}
		return backups;
	}

	private String getNumber(String path) {
		File file = new File(path+ File.separator+ "Info.plist");
		String date;
		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(file);
			date = rootDict.objectForKey("Phone Number").toString();
			

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

		return date;
	}

	private String getDate(String path) {
		File file = new File(path+ File.separator+ "Info.plist");
		String date;
		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(file);
			date = rootDict.objectForKey("Last Backup Date").toString();
			

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

		return date;
	}

	private String getName(String path) {
		File file = new File(path+ File.separator+"Info.plist");
		String name;
		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(file);
			name = rootDict.objectForKey("Device Name").toString();
			

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

		return name;
	}

	
}
