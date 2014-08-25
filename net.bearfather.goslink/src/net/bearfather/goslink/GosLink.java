package net.bearfather.goslink;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
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
//	public static DebugWindow dw=new DebugWindow();  //Non-Linux
	public static DebugConsole dw=new DebugConsole();//Linux
	public static gosbot gb=new gosbot();
	static TelnetService TC1 = new TelnetService(prop.getProperty("server1"), 23);
	static TelnetService TC2 = new TelnetService(prop.getProperty("server2"), 23);
    public static Thread server1 = new Thread (new GosLink(1));
    public static Thread server2 = new Thread (new GosLink(2));
    public static File fnames = new File("names.txt");
    public static Thread HB= new Thread (new HeartBeat());
    public static ArrayList<String> names =new ArrayList<String>();
    private int tcn;
    static int look=1;

    public static void main(String[] args) {
    	server1.start();
		server2.start();
		HB.start();
		try {filerdr();} catch (IOException e) {e.printStackTrace();}
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
		if (num == 1){TC = TC1;TC1.mynum=1;}
		else {TC = TC2;TC2.mynum=2;}
		String rtn=TC.getTelnetSessionAsString(Integer.toString(num));
		if (rtn.equals("reload")){return rtn;}
		TC.readit(" ");
		dw.append("Server "+num+": ");
		TC.write("gos Goslink is enabled.");
		TC.readit("\n");
		TC.write("\n");
		String msg = null;
		while (TC.loggedin == 1){
			TC.readUntil("gossips:");
			msg=TC.readUntil("\n");
			if (msg.equals("!OffLINE+02")){
			}else{
				sayit(num,msg);
			}
		}
		dw.append("Server "+num+" is offline.");
		killme(num);
		return "reload";
	}
	public static void sayit(int tc,String msg){
		String tmsg[]=msg.split("<4;2>0:");
		String player = tmsg[0];
		String capplayer=player;
		player=player.toLowerCase().trim();
		String u1=GosLink.prps("muser1");
		String u2=GosLink.prps("muser2");
		if (TC1.ghost ==1 || TC2.ghost == 1){tmsg[1]=tmsg[1]+"\n";}
		if (!player.equals(u1.toLowerCase())){
		 if  (!player.equals(u2.toLowerCase())){
			if (tc == 1){
				dw.append("Server 2: ");
				TC2.write("gos  "+capplayer+": "+tmsg[1].trim());}
			else{
				dw.append("Server 1: " );
				TC1.write("gos  "+capplayer+": "+tmsg[1].trim());}
		 }
		}
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
    @SuppressWarnings("resource")
	public static void filerdr() throws IOException{
    	if (!fnames.exists()){fnames.createNewFile();}
    	BufferedReader rfile = new BufferedReader(new FileReader(fnames));
    	String nme=null;
    	for (int i=0;i<6;i++){
    		nme=rfile.readLine();
    		if (nme!=null && !nme.isEmpty()){
    			names.add(nme);
    		}
    	}
    }
    public static void filewrt() throws FileNotFoundException, UnsupportedEncodingException{
    	PrintWriter wfile = new PrintWriter(fnames, "UTF-8");
    	for (int i=0;i<names.size();i++){
    		wfile.println(names.get(i));
    	}
    	wfile.close();
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
}