package net.bearfather.goslink;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
public class GosLink implements Runnable{
public static Properties prop = new Properties();
public static int time;
static{
			InputStream input = null;
			try {
				input = new FileInputStream("config.properties");
				prop.load(input);
			} catch (IOException ex) {
				JFrame frame = null;
				JOptionPane.showMessageDialog(frame, "Can't find config.properties!","No Config File",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			} finally {
				if (input != null) {
					try {
						input.close();
						time=Integer.parseInt(prop.getProperty("time"));
						time=time*60*1000;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	 }
	static TelnetService TC1 = new TelnetService(prop.getProperty("server1"), 23);
//	public static DebugConsole dw=new DebugConsole();//Linux
	public static DebugWindow dw=new DebugWindow();  //Non-Linux
	static TelnetService TC2 = new TelnetService(prop.getProperty("server2"), 23);
    public static Thread server1 = new Thread (new GosLink(1));
    public static Thread server2 = new Thread (new GosLink(2));
    private int tcn;
	static int look=1;
	public static void main(String[] args) {
		server1.start();
		server2.start();
		Thread HB= new Thread (new HeartBeat());
		HB.start();
	}
	@Override
	public void run() {
		if (tcn==1){
			while (!server1.isInterrupted()){
				try {
					String rtn=Tclient(1);
					if (rtn.equals("reload")){
						TC1.loggedin=0;
						server1.interrupt();
						TC1.killme();
					}
				} catch (SocketException e) {
					dw.append("Server 1 offline.");
					TC1.loggedin=0;
					server1.interrupt();
				}catch (IOException | InterruptedException e) {e.printStackTrace();System.out.println("thats me");}
			}
		}else{
			while (!server2.isInterrupted()){
				try {
					String rtn=Tclient(2);
					if (rtn.equals("reload")){
						TC2.loggedin=0;
						server2.interrupt();
						TC2.killme();
					}
				} catch (SocketException e) {
					dw.append("Server 2 offline.");
					TC2.loggedin=0;
					server2.interrupt();
				}catch (IOException | InterruptedException e) {e.printStackTrace();System.out.println("thats me");}
			}
		}
	if (tcn==1){server1=null;}
	else{server2=null;}
	}
	
	public String Tclient(int num) throws SocketException, IOException, InterruptedException{
		TelnetService TC;
		if (num == 1){TC = TC1;}
		else {TC = TC2;}
		TC.getTelnetSessionAsString(Integer.toString(num));
		TC.readUntil(" ");
		dw.append("Server "+num+": ");
		TC.write("gos Goslink is enabled.");
		TC.readUntil("\n");
		TC.write("\n");
		String msg = null;
		while (TC.loggedin == 1){
			TC.readUntil("gossips:");
			msg=TC.readUntil("\n");
			if (msg.equals("!OffLINE+02")){
				dw.append("shit");
			}else{
				sayit(num,msg);
			}
		}
		dw.append("Server "+num+" is offline.");
		killme(num);
		return "reload";
	}
	public static void sayit(int tc,String msg){
		String player = TelnetService.player.trim();
		player=player.toLowerCase();
		String u1=GosLink.prps("muser1");
		String u2=GosLink.prps("muser2");
		if (!player.equals(u1.toLowerCase())){
		 if  (!player.equals(u2.toLowerCase())){
			if (tc == 1){
				dw.append("Server 2: ");
				TC2.write("gos  "+TelnetService.player+": "+msg.trim());}
			else{
				dw.append("Server 1: " );
				TC1.write("gos  "+TelnetService.player+": "+msg.trim());}
		 }
		}
	}
	public int getTcn() {
		return tcn;
	}
	public GosLink(int set) {
		this.tcn = set;
	}
	public static String prps(String name) {
		return prop.getProperty(name);
	}
	public static void startit(int num){
		if (num==1){server1=new Thread (new GosLink(1));server1.start();}
		else{server2=new Thread (new GosLink(2));server2.start();}
	}
    public static void killme(int num) {
    	if (num==1){
    		if (server1 != null) {server1.interrupt();}
    	}else{
    		if (server2 != null) {server2.interrupt();}
    	}
    }
}