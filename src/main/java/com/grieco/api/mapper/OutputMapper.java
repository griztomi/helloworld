package com.grieco.api.mapper;

import org.springframework.stereotype.Component;

@Component
public class OutputMapper
{
    static final String NEW_LINE = "\n";
    static final String SYNC_ERROR_MESSAGE = "Error occurred during synchronization. See logs!";
    static final String SYNC_EMPTY_MESSAGE = "No file to be synchronized.";
    static final String SYNC_SUCCESS_MESSAGE = "Files successfully synchronized:";
    static final String POLL_EMPTY_MESSAGE = "Polled directory is empty.";
    static final String POLL_SUCCESS_MESSAGE = "Files in polled directory:";

    public String mapSyncedFiles(String[] syncedFiles)
    {
        if (syncedFiles == null)
        {
            return SYNC_ERROR_MESSAGE;
        }

        if (syncedFiles.length == 0)
        {
            return SYNC_EMPTY_MESSAGE;
        }

        return String.join(NEW_LINE, SYNC_SUCCESS_MESSAGE, String.join(NEW_LINE, syncedFiles));
    }

    public String mapPollResult(String[] pollResult)
    {
        if (pollResult.length == 0)
        {
            return POLL_EMPTY_MESSAGE;
        }

        return String.join(NEW_LINE, POLL_SUCCESS_MESSAGE, String.join(NEW_LINE, pollResult));
    }
}
