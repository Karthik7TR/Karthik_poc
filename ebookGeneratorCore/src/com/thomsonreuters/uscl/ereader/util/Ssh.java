/*
* Copyright 2012: Thomson Reuters Global Resources.
* All Rights Reserved.  Proprietary and Confidential information of TRGR.
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.util;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Ssh {
	static JSch jsch = new JSch();
	private static Session openConnection(String serverName, String userName, String password){
		try {

			Session session = jsch.getSession(userName, serverName, 22);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			return session;
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String executeCommand(String serverName, String username, String password, String command){
		String s = "";
		try{
			Session session = Ssh.openConnection(serverName, username, password);
			Channel channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			channel.setInputStream(null);
			InputStream in=channel.getInputStream();
			channel.connect();
			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					s = s + new String(tmp, 0, i);
				}
				if(channel.isClosed()){
					if(channel.getExitStatus() != 0){
						//System.out.println("exit-status: "+channel.getExitStatus());
					}
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			in.close();
			channel.disconnect();
			session.disconnect();
		}catch (Exception e){
			e.printStackTrace();
		}
		return s;
	}



}

