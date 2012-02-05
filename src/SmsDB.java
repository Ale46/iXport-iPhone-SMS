import java.io.File;
import java.text.DateFormat;
import java.util.Vector;


import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;



public class SmsDB {


	private String DB;
	private final String DB_NAME = "3d0d7e5fb2ce288813306e4d4636395e047a3d28";
	private SQLiteConnection dblite;

	public SmsDB() {



	}


	public void loadBackup(Backup bk, boolean userLoaded){
		if (!userLoaded)
			DB = bk.getPath() + File.separator + DB_NAME;
		else
			DB = bk.getPath();
		
		dblite = new SQLiteConnection(new File(DB));
	

	}

	public Vector<SMS> query(String q){
		Vector<SMS> sms = new Vector<SMS>();
		try {
			if (!dblite.isOpen())
				dblite.openReadonly();
			SQLiteStatement st = dblite.prepare(q);


			while (st.step()) {
				//for (int i = 0; i < st.columnCount(); i++) {
				String address = st.columnValue(0) != null ? st.columnValue(0).toString() : "Error"; 
				String date = st.columnValue(1) != null ? st.columnValue(1).toString() : "Error";
				String text = st.columnValue(2) != null ? st.columnValue(2).toString() : "Error";
				boolean flag;
				boolean imessage  = (st.columnValue(4) != null) ? true : false;
				if (imessage) {
					
					address = st.columnValue(4).toString();
					if (st.columnValue(5).toString().equals("12289")||st.columnValue(5).toString().equals("77825"))
						flag = false;
					else
						flag = true;
				}
				else {
					if ((st.columnValue(3).toString()).equals("3")||(st.columnValue(3).toString().equals("16387")) )
						flag = true;
					else
						flag = false;
				}
					
				
				

				sms.add(new SMS(address, date , text,flag,imessage)); 
				//}

			}
			st.dispose();
			//dblite.dispose();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}		

		return sms;

	}


	private void print(Vector<SMS> sms) throws SQLiteException{
		for (SMS s : sms){
			System.out.println("From: "+s.getAddress() + " at: "+DateFormat.getDateTimeInstance(
		            DateFormat.SHORT, DateFormat.SHORT).format(s.getDate())+ " with text: "+s.getText());
			
		}
	}
	
	public Vector<SMS> getConversationSMS(String who){
		
		return query("select address,date,text,flags,madrid_handle,madrid_flags,group_id  from message where group_id = (SELECT group_id from message where address like'%"+who+"%') or madrid_handle like '%"+who+"%';");
	}
	public Vector<SMS> getReceivedSMS(){
		return query("select address,date,text,flags,madrid_handle,madrid_flags  from message where Flags = 2;");
	}
	
	public Vector<SMS> getSendedSMS(){
		return query("select address,date,text,flags,madrid_handle,madrid_flags  from message where Flags = 3;");
	}
	


	public Vector<SMS> getAllSMS(){
		return query("select address,date,text,flags,madrid_handle,madrid_flags  from message;");
	}
	
	public Vector<SMS> getSMSFrom(String address){
		return query("select address,date,text,flags,madrid_handle,madrid_flags  from message where address like '%"+address+"%' or where madrid_handle like '%"+address+"%';");
	}

	public Vector<String> getAllSMSNumbers(){
		Vector<String> nums = new Vector<String>();
		try {
			if (!dblite.isOpen())
				dblite.openReadonly();
			SQLiteStatement st = dblite.prepare("select DISTINCT address from message");


			while (st.step()) {
				if (st.columnValue(0)!=null) nums.add(st.columnValue(0).toString());
				//System.out.println(st.columnValue(0).toString());
			}
			st.dispose();
			//dblite.dispose();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}		
		return nums;
	}
	
	public void printReceivedSMS() throws SQLiteException{
		print(getReceivedSMS());
	}
	
	

	public void printSendedSMS() throws SQLiteException{
		print(getSendedSMS());
	}

	public void printConversation() throws SQLiteException{
		print(getAllSMS());
	}
	
	
	public void printSMSFrom(String address) throws SQLiteException{
		print(getSMSFrom(address));
	}

	
	public void printConversationWith(String address) throws SQLiteException{
		print(getConversationSMS(address));
	}
}
