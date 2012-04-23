/*
* Copyright 2012: Thomson Reuters Global Resources.
* All Rights Reserved.  Proprietary and Confidential information of TRGR.
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.jcraft.jsch.*;

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
	public static String executeCommand(Server server, String command)
	{
		return executeCommand(server.getServerName(), server.getUserName(), server.getPassword(), command);
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
	public static void transferLocalFile(String startFileName, String destinationServerName, String destinationUserName, String destinationPassword, String destinationLocation) throws IOException, JSchException, SftpException
	{
		InputStream stream = new FileInputStream(startFileName);
		ChannelSftp writeChannel = getSftpChannel(openConnection(destinationServerName, destinationUserName, destinationPassword));
		writeChannel.put(stream, destinationLocation + startFileName.substring(startFileName.lastIndexOf("/") + 1));
		stream.close();
		writeChannel.disconnect();
	}
	public static void transferFile(String startServerName, String startFileName, String destinationServerName, String destinationFileName) throws JSchException, SftpException, IOException
	{
		transferFile(Server.getValue(startServerName), startFileName, Server.getValue(destinationServerName), destinationFileName);
	}
	public static void transferFile(Server startServer, String startFileName, Server destinationServer, String destinationFileName ) throws JSchException, SftpException, IOException
	{
		ChannelSftp readChannel = getSftpChannel(startServer);
		ChannelSftp writeChannel = getSftpChannel(destinationServer);
		InputStream in = readChannel.get(startFileName);
		writeChannel.put(in, destinationFileName);
		in.close();
		writeChannel.disconnect();
		readChannel.disconnect();
	}
	public static void transferFile(String startServerName, String startServerUser, String startServerPass, String startFile, String destServerName, String destServerUser, String destServerPass, String destFile) throws JSchException, SftpException, IOException
	{
		Server startServer = Server.getValue(startServerName);
		startServer.setPassword(startServerPass);
		startServer.setUserName(startServerUser);
		startServer.setServerName(startServerName);
		Server destinationServer = Server.getValue(destServerName);
		destinationServer.setPassword(destServerPass);
		destinationServer.setUserName(destServerUser);
		destinationServer.setServerName(destServerName);
		transferFile(startServer, startFile, destinationServer, destFile);
	}
	private static Session getConnectedSession(Server server) throws JSchException
	{
		JSch readJsch = new JSch();
		Session readSession = readJsch.getSession(server.getUserName(), server.getServerName());
		readSession.setConfig("StrictHostKeyChecking", "no");
		readSession.setPassword(server.getPassword());
		readSession.connect();
		return readSession;
	}
	private static ChannelSftp getSftpChannel(Session session) throws JSchException
	{
		Channel readChannel= session.openChannel("sftp");
		readChannel.connect();
		ChannelSftp readSftpChannel=(ChannelSftp)readChannel;
		return readSftpChannel;
	}
	public static ChannelSftp getSftpChannel(Server server) throws JSchException
	{
		return getSftpChannel(getConnectedSession(server));
	}
	/**
	 * Enum containing pertinent information on servers.
	 * 
	 * @author u0115302
	 */
	public enum Server
	{
		CB8024("cb8024", "asadmin", "east"), 
		TANTALUM("tantalum", "asadmin", "east"), 
		CB8170("cb8170", "asadmin", "east"),
		CB8172("cb8172", "asadmin", "east"), 
		CTCO000208("ctco0002-08", "clustering", "be@Ch12"), 
		CTCO000209("ctco0002-09", "clustering", "be@Ch12"), 
		CTCO000210("ctco0002-10", "clustering", "be@Ch12"), 
		CTCO000211("ctco0002-11", "clustering", "be@Ch12"), 
		CTCO001409("ctco0014-09", "clustering", "be@Ch12"),
		CTCO001609("ctco0016-09", "clustering", "be@Ch12"),
		CTNG00214("ctng0002-14", "asadmin", "east"),
		CTNG00414("ctng0004-14", "asadmin", "east"),
		CB8010("cb8010", "asadmin", "east"), 
		CB8012("cb8012", "asadmin", "east"), 
		CB8018("cb8018", "asadmin", "east"),
		CB8020("cb8020", "asadmin", "east"), 
		CB8022("cb8022", "asadmin", "east"), 
		CB8026("cb8026", "asadmin", "east"), 
		CB8126("cb8126", "asadmin", "east"), 
		CB8162("cb8162", "asadmin", "east"), 
		CB8164("cb8164", "asadmin", "east"), 
		CB8166("cb8166", "asadmin", "east"), 
		CB8168("cb8168", "asadmin", "east"), 
		CB9010("cb9010", "asadmin", "east"), 
		CB000416("cb0004-16", "asadmin", "east"),
		CB000415("cb0004-15", "asadmin", "east"),
		C111BHZCTASDE("c111bhzctasde", "asadmin", "east"),
		UNKNOWN("","","");

		private String userName;
		private String password;
		private String serverName;
		Server(String serverName, String userName, String password)
		{
			this.userName = userName;
			this.password = password;
			this.serverName = serverName;
		}
		public String getUserName()
		{
			return this.userName;
		}
		public void setUserName(String userName)
		{
			this.userName = userName;
		}
		public String getPassword()
		{
			return this.password;
		}
		public void setPassword(String password)
		{
			this.password = password;
		}
		public String getServerName()
		{
			return this.serverName;
		}
		public void setServerName(String serverName)
		{
			this.serverName = serverName;
		}
		public static Server getValue(String serverName)
		{
			try
			{
				return Server.valueOf(serverName.toUpperCase().replaceAll("-", ""));
			}
			catch(Exception e)
			{
				e.printStackTrace();
//				if(serverName.contains("-"))
//				{
//					return getValue(serverName.replaceAll("-", ""));
//				}
//				else
				{
					return Server.UNKNOWN;
				}
			}
		}
	}

}

