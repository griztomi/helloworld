package com.grieco.domain;

import com.grieco.service.SSHManager;
import com.grieco.domain.model.Attributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Poller
{
    private final SSHManager sshManager;

    @Autowired
    public Poller(SSHManager sshManager)
    {
        this.sshManager = sshManager;
    }

    public String[] sync()
    {
        sshManager.connect();

        String[] filesToSync = getFilesToSync(sshManager.pollRemote(), sshManager.pollLocal());

        boolean isSuccess = sshManager.saveFiles(filesToSync);

        sshManager.close();

        if (isSuccess)
        {
            return filesToSync;
        }

        return null;
    }

    public String[] poll(Attributes attributes)
    {
        sshManager.connect();

        String[] result = new String[0];
        switch (attributes.getCommand())
        {
            case POLL_LOCAL:
                result = sshManager.pollLocal();
                break;
            case POLL_REMOTE:
                result = sshManager.pollRemote();
                break;
        }

        sshManager.close();
        return result;
    }

    private String[] getFilesToSync(String[] pollRemote, String[] pollLocal)
    {
        List locals = Arrays.asList(pollLocal);
        return Arrays.stream(pollRemote)
                .filter(remote -> !locals.contains(remote))
                .toArray(String[]::new);
    }
}
