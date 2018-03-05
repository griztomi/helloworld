package com.grieco.mockbuilder;

import com.grieco.service.SSHManager;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class SSHManagerMockBuilder
{
    private String[] pollLocalResult;
    private String[] pollRemoteResult;
    private boolean syncResult;

    private SSHManagerMockBuilder()
    {
    }

    public static SSHManagerMockBuilder aManager()
    {
        return new SSHManagerMockBuilder();
    }

    public SSHManagerMockBuilder withPollLocalResult(String[] pollLocalResult)
    {
        this.pollLocalResult = pollLocalResult;
        return this;
    }

    public SSHManagerMockBuilder withPollRemoteResult(String[] pollRemoteResult)
    {
        this.pollRemoteResult = pollRemoteResult;
        return this;
    }

    public SSHManagerMockBuilder withSyncResult(boolean syncResult)
    {
        this.syncResult = syncResult;
        return this;
    }

    public SSHManager build()
    {
        SSHManager mock = createMock(SSHManager.class);
        expect(mock.pollLocal()).andStubReturn(pollLocalResult);
        expect(mock.pollRemote()).andStubReturn(pollRemoteResult);
        expect(mock.saveFiles(EasyMock.anyObject(String[].class))).andStubReturn(syncResult);
        mock.connect();
        mock.close();

        replay(mock);

        return mock;
    }
}
