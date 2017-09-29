package com.thomsonreuters.uscl.ereader.util;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Ssh
{
    private static JSch jsch = new JSch();

    private static Logger LOG = LogManager.getLogger(Ssh.class);

    private static Session openConnection(final String serverName, final String userName, final String password)
        throws EBookServerException
    {
        try
        {
            final Session session = jsch.getSession(userName, serverName, 22);
            session.setPassword(password);
            final java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            return session;
        }
        catch (final JSchException e)
        {
            LOG.error(e.getMessage(), e);
            throw new EBookServerException(
                "Failed to connect server with given credentials serverName="
                    + serverName
                    + " userName ="
                    + userName
                    + " password ="
                    + password);
        }
    }

    public static String executeCommand(final String serverName, final String username, final String password, final String command)
        throws EBookServerException
    {
        String s = "";
        try
        {
            final Session session = Ssh.openConnection(serverName, username, password);
            final Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            final InputStream in = channel.getInputStream();
            channel.connect();
            final byte[] tmp = new byte[1024];
            while (true)
            {
                while (in.available() > 0)
                {
                    final int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    s = s + new String(tmp, 0, i);
                }
                if (channel.isClosed())
                {
                    if (channel.getExitStatus() != 0)
                    {
                        //System.out.println("exit-status: "+channel.getExitStatus());
                    }
                    break;
                }
                try
                {
                    Thread.sleep(1000);
                }
                catch (final Exception ee)
                {
                    //Intentionally left blank
                }
            }
            in.close();
            channel.disconnect();
            session.disconnect();
        }
        catch (final Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw new EBookServerException(
                "Failed to connect server with given credentials serverName="
                    + serverName
                    + " userName ="
                    + username
                    + " password ="
                    + password);
        }
        return s;
    }
}
