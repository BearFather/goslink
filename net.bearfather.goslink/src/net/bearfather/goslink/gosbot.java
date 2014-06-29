package net.bearfather.goslink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
public class gosbot {
	private TelnetService TN;
	public ArrayList<String> enters =new ArrayList<String>();
	
	public void tele(String plr,int num) throws InterruptedException, IOException{
		if (num==1){TN=GosLink.TC1;}
		else{TN=GosLink.TC2;}
		String msg=TN.readUntil("\n");
		String cmd="";
		String rtn;
		String tmsg[]=msg.split("<4;2>0:");
		msg = tmsg[1];
		String broken[]=msg.trim().split(" ");
		String chk=broken[0].trim().toLowerCase();
		
		if (!Boolean.valueOf(GosLink.prps("bot"))){
			cmd="/"+plr+" Sorry GosBot is disabled.";
		}
		else if (chk.equals("@abils")){
			TN.write("sys god "+plr+" abil");
			TN.readit("\n");
			rtn=TN.readit("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@help")){
			TN.write("/"+plr+" Hello "+plr+" Commands are:");
			cmd="/"+plr+" @abils,@good,@neutral,@evil,@retrain, and @home room# map#";
		}
		else if (chk.equals("@neutral")){
			TN.write("sys god "+plr+" neutral");
			TN.readit("\n");
			rtn=TN.readit("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@good")){
			TN.write("sys god "+plr+" good");
			TN.readit("\n");
			rtn=TN.readit("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@evil")){
			TN.write("sys god "+plr+" neutral");
			TN.readit("\n");
			TN.write("sys god "+plr+" add evil 150");
			TN.readit("\n");
			rtn=TN.readit("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if (chk.equals("@retrain")){
			cmd="sys god "+plr+" retrain";
			GosLink.dw.append(cmd);
			TN.write(cmd);
			TN.readit("\n");
			rtn=TN.readit("\n");
			cmd="/"+plr+" "+rtn;
		}
		else if(chk.equals("@home")){
			String line= "blank";
			String line2 = "blank";
			int alive=0;
			if (broken.length < 3) {
				cmd="/"+plr+" not enough info";
			}else{
				TN.write("sys st room "+broken[1]+" "+broken[2]);
				rtn=TN.readit("Monsters:");
				if (rtn.equals("Room error")){
					TN.write("/"+plr+" Room error!");
					return;
				}
				String broken2[]=rtn.split("\n");
				for (int i=0;i<broken2.length;i++){
					String mmsg=broken2[i];
					if (mmsg.contains("Specific Monster:")){
						line=mmsg;
					}
					else if (mmsg.contains("Specific Monster is Alive")){
						line2=mmsg;
						alive=1;
					}
				}
				if (alive==1){cmd="/"+plr+" "+line2;}
				else if (!line.equals("blank")){cmd="/"+plr+" "+line;}
				else{cmd="/"+plr+" You have an invalid room. Room:"+broken[1]+" Map:"+broken[2];}
			}
		}
		else{cmd="/"+plr+" Invalid command:"+msg;}
		if (GosLink.TC1.ghost ==1 || GosLink.TC2.ghost == 1){cmd=cmd+"\n";}
		TN.write(cmd);
	}
	public void enter(String plr,int num) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
		String gname;
		int found=0;
		int amt=0;
		int write=0;
		int cnt=0;
		String nme;
		if (!Boolean.valueOf(GosLink.prps("bot"))){found=255;}
		while (found==0){
			cnt=GosLink.names.size();
			for (int i=0;i<cnt;i++){
				if(GosLink.names.get(i)!=null){
					nme=GosLink.names.get(i);
					if (plr.equals(nme.substring(1))){
						amt=Integer.parseInt(nme.substring(0,1));
						if (amt<5){
							amt=amt+1;
							GosLink.names.remove(i);
							GosLink.names.add(i,amt+plr);
							write=1;
							found=1;
						}
						else{found=2;}
						i=cnt+1200;
					}	
				}	
			}
			if (found==0){found=3;}
		}
		
		if (found==3){
			GosLink.names.add("1"+plr);
			write=1;
			found=1;
		}
		if (write==1){GosLink.filewrt();}
		if (num==1){TN=GosLink.TC1;gname=GosLink.prps("muser1");}
		else{TN=GosLink.TC2;gname=GosLink.prps("muser2");}
		String blah=num+",/"+plr+" Hello "+plr+".  My name is "+gname+".  I am a GosLink Bot.  Please Telepath me @help for commands.";
		if (found==1){enters.add(blah);}
	}
	public static void enterchk(){
		for (String value:GosLink.gb.enters){
			if (value.startsWith("1")){
				GosLink.TC1.write(value.substring(2));
			}
			else{
				GosLink.TC2.write(value.substring(2));
			}
		}
		GosLink.gb.enters.clear();
	}
}
