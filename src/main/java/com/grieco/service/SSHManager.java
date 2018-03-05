package com.grieco.service;

import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Component
public class SSHManager
{
    private static final Logger LOGGER = LogManager.getLogger(SSHManager.class);
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

    public void connect()
    {
        try
        {
            session = new JSch().getSession(userName, connectionIP, connectionPort);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(connectionTimeOut);
        }
        catch(JSchException e)
        {
            LOGGER.error("Could not connect to remote", e);
        }
    }

    public String[] pollRemote()
    {
        return sendCommand(String.format(POLL_CMD, remoteBaseDir)).trim().split("\n");
    }

    public String[] pollLocal()
    {
        return sendCommand(String.format(POLL_CMD, localBaseDir)).trim().split("\n");
    }

    private String sendCommand(String command)
    {
        Channel channel = null;
        try
        {
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.connect();
            try (InputStream commandOutput = channel.getInputStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream())
            {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = commandOutput.read(buffer)) != -1)
                {
                    result.write(buffer, 0, length);
                }

                return result.toString(StandardCharsets.UTF_8.name());
            } catch (IOException e)
            {
                LOGGER.error("Read/Write error during poll", e);
            }
        } catch (JSchException e)
        {
            LOGGER.error("Connection error during poll", e);
        } finally
        {
            if (channel != null)
            {
                channel.disconnect();
            }
        }
        return null;
    }

    public boolean saveFiles(String[] remoteFiles)
    {
        boolean isSuccess = true;
        Channel channel = null;
        try
        {
            channel = session.openChannel("sftp");
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channel.connect();
            for (String file : remoteFiles)
            {
                try
                {
                    createSubDirs(channelSftp, file);
                    channelSftp.get(remoteBaseDir + file, localBaseDir + file);
                } catch (SftpException e)
                {
                    LOGGER.error("Could not save file", e);
                    isSuccess = false;
                }
            }
        } catch (JSchException e)
        {
            LOGGER.error("Connection error during save", e);
            isSuccess = false;
        } finally
        {
            if (channel != null)
            {
                channel.disconnect();
            }
        }

        return isSuccess;
    }

    private void createSubDirs(ChannelSftp channelSftp, String file)
    {
        String[] subDirs = file.split("/");
        try
        {
            String[] localBaseSubDirs = localBaseDir.split("/");
            channelSftp.cd("/");
            LOGGER.info("cd /");
            for(int i = 1; i < subDirs.length-1; i++)
            {
                channelSftp.cd(localBaseSubDirs[i]);
                LOGGER.info("cd " + localBaseSubDirs[i]);
            }
        } catch (SftpException e)
        {
            LOGGER.error("Local base directory does not exists: " + file, e);
        }
        for(int i = 0; i < subDirs.length-1; i++)
        {
            try
            {
                channelSftp.mkdir(subDirs[i]);
                LOGGER.info("mkdir " + subDirs[i]);
            } catch (SftpException e)
            {
                // Directory already exists
            }
            try
            {
                channelSftp.cd(subDirs[i]);
                LOGGER.info("cd " + subDirs[i]);
            } catch (SftpException e)
            {
                LOGGER.error("Could not create local directory for: " + file, e);
            }
        }
    }

    public void close()
    {
        session.disconnect();
    }
}
