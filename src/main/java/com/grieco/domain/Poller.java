package com.grieco.domain;

import com.grieco.service.SSHManager;
import com.grieco.domain.model.Attributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

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

        String pollResult = sshManager.poll();
        sshManager.saveFiles(pollResult.split("\n"));

        sshManager.close();
        return pollResult;
    }
}
