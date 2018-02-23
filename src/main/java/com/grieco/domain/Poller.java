package com.grieco.domain;

import com.grieco.service.SSHManager;
import com.grieco.domain.model.Attributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Poller
{
    @Autowired
    private SSHManager sshManager;

    public String poll(Attributes attributes)
    {
        String error = sshManager.connect();
        if (error != null)
        {
            return error;
        }

        String result = sshManager.sendCommand(attributes.getCommands());
        sshManager.close();
        return result;
    }
}
