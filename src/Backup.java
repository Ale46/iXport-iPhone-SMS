
public class Backup {
	
	private String name;
	private String path;
	private String date;
	private String number;
	
	public Backup(String name, String path, String date2, String number){
		this.setName(name);
		this.setPath(path);
		this.setDate(date2);
		this.setNumber(number);
		
	}


	private void setNumber(String number) {
		this.number = number;
		
	}

	public String getNumber(){
		return number;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date2) {
		this.date = date2;
	}
	
	
	
}
