package com.grieco.service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class SSHManager
{
    private static final String POLL_CMD = "(cd %s && find . -type f | cut -c 3-)" ;

    private String userName;
    private String connectionIP;
    private int connectionPort;
    private String password;
    private Session session;
    private int connectionTimeOut;
    private String remoteBaseDir;
    private String localBaseDir;

    public SSHManager()
    {
        this.password = new String(Base64.getDecoder().decode(System.getProperty("password")));
        String[] connection = System.getProperty("connection").split("[@:]");
        this.userName = connection[0];
        this.connectionIP = connection[1];
        this.connectionPort = Integer.parseInt(connection[2]);
        this.connectionTimeOut = 60000;
        this.remoteBaseDir = System.getProperty("remoteBaseDir");
        this.localBaseDir = System.getProperty("localBaseDir");
    }

    public String connect()
    {
        String errorMessage = null;

        try
        {
            session = new JSch().getSession(userName, connectionIP, connectionPort);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(connectionTimeOut);
        }
        catch(JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    public String poll()
    {
        return sendCommand(String.format(POLL_CMD, remoteBaseDir)).trim();
    }

    private String sendCommand(String command)
    {
        Channel channel = null;
        try
        {
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            try (InputStream commandOutput = channel.getInputStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream())
            {
                channel.connect();

                byte[] buffer = new byte[1024];
                int length;
                while ((length = commandOutput.read(buffer)) != -1)
                {
                    result.write(buffer, 0, length);
                }

                return result.toString(StandardCharsets.UTF_8.name());
            }
        }
        catch(IOException | JSchException e)
        {
            return null;
        } finally
        {
            if (channel != null)
            {
                channel.disconnect();
            }
        }
    }

    public void saveFiles(String[] remoteFiles)
    {
        Channel channel = null;
        try
        {
            channel = session.openChannel("sftp");
            ChannelSftp channelSftp = (ChannelSftp) channel;
            for (String file : remoteFiles)
            {
                try
                {
                    channelSftp.get(remoteBaseDir + file, localBaseDir + file);
                } catch (SftpException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (JSchException e)
        {
            e.printStackTrace();
        } finally
        {
            if (channel != null)
            {
                channel.disconnect();
            }
        }
    }

    public void close()
    {
        session.disconnect();
    }
}
