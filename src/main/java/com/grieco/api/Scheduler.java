package com.grieco.api;

import com.grieco.api.mapper.OutputMapper;
import com.grieco.domain.Poller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler
{
    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    private final Poller poller;
    private final OutputMapper outputMapper;

    @Autowired
    public Scheduler(Poller poller, OutputMapper outputMapper)
    {
        this.poller = poller;
        this.outputMapper = outputMapper;
    }

    @Scheduled(fixedDelay = 15000)
    public void scheduler()
    {
        LOGGER.info("Sync started");
        String[] poll = poller.sync();
        String output = outputMapper.mapSyncedFiles(poll);
        LOGGER.info("Sync result:\n" + output);
    }
}
