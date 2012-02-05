import java.util.Calendar;
import java.util.Date;




public class SMS {
	
	private String address;
	private String text;
	private Date date;
	private boolean sended,imessage;
	
	public SMS(String address, String date, String text, boolean sended, boolean imessage){
		setAddress(address);
		setiMessage(imessage);
		setDate(date);
		setText(text);
		setSended(sended);
		
	}

	private void setiMessage(boolean imessage) {
		this.imessage = imessage;
		
	}

	private void setSended(boolean sended2) {
		sended = sended2;
		
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}
	
	public boolean isSended() {
		return sended;
	}
	
	public boolean isiMessage() {
		return imessage;
	}

	public void setDate(String timeStamp) {
/*		if (imessage)
			timeStamp+=978307200;*/
		try{

			long unixTime = Long.parseLong(timeStamp);
			Date d = new Date(unixTime* 1000);
			if (isiMessage()){
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				cal.add(Calendar.YEAR, 31);
				cal.add(Calendar.DATE, 1);
				this.date = cal.getTime();
				
			}else{
				this.date = d;
				
			}
		}catch (NumberFormatException e){
			e.printStackTrace();
/*			long unixTime = Long.parseLong(timeStamp.replace(".", "").substring(0,10));
			this.date = new Date(unixTime* 1000);*/
		}
	}
}
