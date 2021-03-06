package net.bearfather.goslink;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;
public class TelnetService {
	private final String server;
	private final int port;
	public TelnetClient telnet = new TelnetClient();
	private InputStream dataIn;
	private PrintStream dataOut;
	public static String player;
	public int loggedin=0;
	public static int runner=1;
	public int ghost =0;
	public int mynum=0;
	String hangup=GosLink.prps("cleanup");
    String nonstop="(N)onstop, (Q)uit, or (C)ontinue?";
    String ghosts="Enter your password to end the other connection and log on.";
   	int cnt;
	public TelnetService(String server, int port) {
		this.server = server.replace("http://", "");
		this.port = port;
	}
	public void killme() throws IOException{
		GosLink.dw.append("Ghost detected.  Reloging.");
		telnet.disconnect();
	}
	private void startTelnetSession() throws SocketException, IOException {
		telnet.connect(server, port);
		dataIn = telnet.getInputStream();
		dataOut = new PrintStream(telnet.getOutputStream());
	}
	public String getTelnetSessionAsString(String tcb) throws SocketException, IOException, InterruptedException {
        startTelnetSession();
        String rtn="blah";
        GosLink.dw.append("Logging into server "+tcb+".");
        if (tcb.equals("1")){
        	rtn=loginUser(GosLink.prps("user1"),GosLink.prps("game1"),GosLink.prps("pass1"));
        }
        else{
        	rtn=loginUser(GosLink.prps("user2"),GosLink.prps("game2"),GosLink.prps("pass2"));
        }
        if (rtn.equals("reload")){return rtn;}
        loggedin=1;
        return "Logged in";
}
	private String loginUser(String name, String cmd,String pass) throws InterruptedException, IOException {
		readUntil(GosLink.prps("puser"));
		write(name);
		readUntil(GosLink.prps("ppass"));
		write(pass+"\r\n");
		readUntil(GosLink.prps("pmenu"));
		if (ghost == 1){
			write("=x\n");
			ghost=0;
			return "reload";
		}
		else {write(cmd);}
		readUntil(GosLink.prps("pmud"));
		write("e");
		write("\n");
		return "blah";
}
	public void write(String value) {
		try {
			dataOut.println(value);
			dataOut.flush();
			if (loggedin==1){
				if (value.equals("\n")){}
				else if(value.equals("")){}
				else{GosLink.dw.append(value);}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String readUntil(String pattern) throws InterruptedException, IOException {
		cnt=0;
		char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuffer buffer = new StringBuffer();
        char ch = (char) dataIn.read();
        String msg;
        String broken[];
        while (runner==1) {
            buffer.append(ch);
        	msg=buffer.toString();
        	String chk=msg.trim();
        	String rtn=msgchk(chk,msg);
        	if (rtn != null){return rtn;}
        	if (ch == lastChar) {
         		if (buffer.toString().endsWith(pattern)) {
                	broken=msg.split(" ");
                	for (int i=0;i<broken.length;i++ ) {
                		if (broken[i].equals("gossips:")){
                			player=broken[i-1];
                		}
                	}
                    return player+"<4;2>0:"+buffer.toString();
                    
                }
         		
         		if (buffer.toString().endsWith("telepaths:")) {
         			broken=msg.split(" ");
                	for (int i=0;i<broken.length;i++ ) {
                		if (broken[i].equals("telepaths:")){
                			player=broken[i-1];
                		}
                	}
                    GosLink.gb.tele(player.trim().toLowerCase(),mynum);
                }
            }
            ch = (char) dataIn.read();
            
        }
       	return null;
            
        }
	public String readit(String pattern) throws InterruptedException, IOException {
		cnt=0;
		char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuffer buffer = new StringBuffer();
        char ch = (char) dataIn.read();
        while (runner==1) {
            buffer.append(ch);
            String msg=buffer.toString().trim();
            if (msg.contains("Room error")){
     			return "Room error";
     		}
        	if (ch == lastChar) {
        		if (buffer.toString().endsWith(pattern)) {
                    return buffer.toString();
                }
            }
            ch = (char) dataIn.read();
            
        }
       	return null;
            
        }

	private String msgchk(String chk, String msg) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        	String broken[];
    		
        	if (chk.endsWith(ghosts)){
        		ghost=1;
        	}
        	if (chk.endsWith(nonstop)){
        	  if (cnt == 0){
            	dataOut.print("n\b");
    			dataOut.flush();
        	  }
        	  cnt++;
        	}
     		if (chk.endsWith("Room error")){
     			return "Room error";
     		}
     		if (msg.endsWith("just entered the Realm.")){
     			broken=msg.split(" ");
            	for (int i=0;i<broken.length;i++ ) {
            		if (broken[i].equals("just")){
            			player=broken[i-1];
            		}
            	}
                GosLink.gb.enter(player.trim(),mynum);
     		}
            if (chk.endsWith(hangup)){
            	GosLink.dw.append("BBS shutdown detected!");
            	loggedin=0;
            	return "!OffLINE+02";
            }
            return null;
        }
	
}