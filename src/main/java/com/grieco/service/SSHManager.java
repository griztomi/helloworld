package com.grieco.service;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class SSHManager
{
    private String userName;
    private String connectionIP;
    private int connectionPort;
    private String password;
    private Session session;
    private int connectionTimeOut;

    public SSHManager()
    {
        this.password = System.getProperty("password");
        String[] connection = System.getProperty("connection").split("[@:]");
        this.userName = connection[0];
        this.connectionIP = connection[1];
        this.connectionPort = Integer.parseInt(connection[2]);
        this.connectionTimeOut = 60000;
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

    public String sendCommand(String[] commands)
    {
        Channel channel = null;
        try
        {
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(String.join("", commands));
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
        catch(IOException ioX)
        {
            return null;
        }
        catch(JSchException jschX)
        {
            return null;
        }
        finally
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
