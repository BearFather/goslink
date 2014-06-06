package net.bearfather.goslink;

import java.io.IOException;

public class gosbot {
	private TelnetService TN;
	public void tele(String plr,int num) throws InterruptedException, IOException{
		if (num==1){TN=GosLink.TC1;}
		else{TN=GosLink.TC2;}
		String msg=TN.readUntil("\n");
		String cmd="";
		String rtn;
		String broken[]=msg.trim().split(" ");
		String chk=broken[0].trim();
		if (chk.equals("@abils")){
			TN.write("sys god "+plr+" abil");
			TN.readUntil("\n");
			rtn=TN.readUntil("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@help")){
			TN.write("/"+plr+" Hello "+plr+" Commands are:");
			cmd="/"+plr+" @abils,@good,@neutral,@evil,@retrain, and @home room# map#";
		}
		else if (chk.equals("@neutral")){
			TN.write("sys god "+plr+" neutral");
			TN.readUntil("\n");
			rtn=TN.readUntil("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@good")){
			TN.write("sys god "+plr+" good");
			TN.readUntil("\n");
			rtn=TN.readUntil("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@evil")){
			TN.write("sys god "+plr+" neutral");
			TN.write("sys god "+plr+" add evil 150");
			TN.readUntil("\n");
			TN.readUntil("\n");
			rtn=TN.readUntil("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@retrain")){
			cmd="sys god "+plr+" retrain";
			GosLink.dw.append(cmd);
			TN.write(cmd);
			TN.readUntil("\n");
			rtn=TN.readUntil("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if(chk.equals("@home")){
			TN.write("sys st room "+broken[1]+" "+broken[2]);
			TN.readUntil("\n");
			rtn=TN.readUntil("Monsters:");
			if (rtn.equals("Room error")){
				TN.write("/"+plr+" Room error!");
				return;
			}
			String broken2[]=rtn.split("\n");
			if (broken2[6].equals("Monsters:")){cmd="/"+plr+" You have the wrong room number. Room:"+broken[1]+" Map:"+broken[2];}
			else{
				if (broken2[7].equals("Monsters:")){cmd="/"+plr+" Monster is Dead: "+broken2[6];				}
				else{cmd="/"+plr+" "+broken2[7];}
			}
		}
		else{cmd="/"+plr+" Invalid command:"+msg;}
		TN.write(cmd);
	}
	public void enter(String plr,int num){
		String gname;
		if (num==1){TN=GosLink.TC1;gname=GosLink.prps("muser1");}
		else{TN=GosLink.TC2;gname=GosLink.prps("muser2");}
		TN.write("/"+plr+" Hello "+plr+".  My name is "+gname+".  I am a GosLink Bot.  Please Telepath me @help for commands.");
	}
}
