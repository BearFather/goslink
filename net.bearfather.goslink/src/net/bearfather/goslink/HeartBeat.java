package net.bearfather.goslink;
public class HeartBeat implements Runnable {
	private int offline1=0;
	private int offline2=0;
	private int ol;
	private int count1=0;
	private int count2=0;
	private int ct;
	private TelnetService TC;
	private int time=Integer.parseInt(GosLink.prps("time"));
	

	public void run(){
		if (time!=0){time=time-1;}
		GosLink.dw.append("HeartBeat Started.");
		while (true){
			try {
				Thread.sleep(60000);
				checkserver(1);
				checkserver(2);
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	public void checkserver(int num){
		if (num==1){TC=GosLink.TC1;ol=offline1;ct=count1;}
		else{TC=GosLink.TC2;ol=offline2;ct=count2;}
		if (TC.loggedin == 1){
			if (ct==3){
				TC.write("");
				GosLink.dw.append("PING");
				ct=0;
			}else{ct++;}
			if (ct >5){ct=0;}
		}else{
			if (ol==0){GosLink.dw.append("Server "+num+" is offline, waiting to restart.");}
			if (ol == time){
				ol=0;
				GosLink.dw.append("Starting Server "+num+".");
				GosLink.startit(num);
			}else{ol++;}
		}
		if (num==1){offline1=ol;count1=ct;}
		else{offline2=ol;count2=ct;}
	}
}