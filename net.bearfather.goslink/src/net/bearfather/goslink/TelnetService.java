package net.bearfather.goslink;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
	public int bug;
	public TelnetService(String server, int port) {
		this.server = server.replace("http://", "");
		this.port = port;
	}
	public void killme() throws IOException{
		telnet.disconnect();
	}
	private void startTelnetSession() throws SocketException, IOException {
		telnet.connect(server, port);
		dataIn = telnet.getInputStream();
		dataOut = new PrintStream(telnet.getOutputStream());
	}
	public String getTelnetSessionAsString(String tcb) throws SocketException, IOException, InterruptedException {
        startTelnetSession();
        GosLink.dw.append("Logging into server "+tcb+".");
        if (tcb.equals("1")){bug=1;
            loginUser(GosLink.prps("user1"),GosLink.prps("game1"),GosLink.prps("pass1"));
        }
        else{bug=2;
        	loginUser(GosLink.prps("user2"),GosLink.prps("game2"),GosLink.prps("pass2"));
        }
        loggedin=1;
        return "Logged in";
}
	private void loginUser(String name, String cmd,String pass) throws InterruptedException, IOException {
		readUntil(GosLink.prps("puser"));
		write(name);
		readUntil(GosLink.prps("ppass"));
		write(pass+"\r\n");
		readUntil("(N)onstop, (Q)uit, or (C)ontinue?");
//		readUntil2("(N)onstop, (Q)uit, or (C)ontinue?",bug);
		write("c");
		write("\n");
		readUntil(GosLink.prps("pmenu"));
		write("\n"+cmd);
		readUntil(GosLink.prps("pmud"));
		write("e");
		write("\n");
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
        String hangup=GosLink.prps("cleanup");
       	char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuffer buffer = new StringBuffer();
        char ch = (char) dataIn.read();
        String msg;
        String broken[];
        while (runner==1) {
            buffer.append(ch);
        	msg=buffer.toString();
        	String chk=msg.trim();
         	if (ch == lastChar) {
                if (buffer.toString().endsWith(pattern)) {
                	broken=msg.split(" ");
                	for (int i=0;i<broken.length;i++ ) {
                		if (broken[i].equals("gossips:")){
                			player=broken[i-1];
                		}
                	}
                    return buffer.toString();
                }
        	}
            if (chk.endsWith(hangup)){
            	GosLink.dw.append("BBS shutdown detected!");
            	loggedin=0;
            	return "!OffLINE+02";
            }
            ch = (char) dataIn.read();
        }
        return null;
	}
	public String readUntil2(String pattern,int num) throws InterruptedException, IOException {
        String hangup=GosLink.prps("cleanup");
       	char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuffer buffer = new StringBuffer();
        char ch = (char) dataIn.read();
        String msg;
        String broken[];
        while (runner==1) {
            buffer.append(ch);
        	msg=buffer.toString();
        	String chk=msg.trim();
        	if (num==1){GosLink.dw.append(chk);}
         	if (ch == lastChar) {
                if (buffer.toString().endsWith(pattern)) {
                	broken=msg.split(" ");
                	for (int i=0;i<broken.length;i++ ) {
                		if (broken[i].equals("gossips:")){
                			player=broken[i-1];
                		}
                	}
                    return buffer.toString();
                }
        	}
            if (chk.endsWith(hangup)){
            	GosLink.dw.append("BBS shutdown detected!");
            	loggedin=0;
            	return "!OffLINE+02";
            }
            ch = (char) dataIn.read();
        }
        return null;
	}
}