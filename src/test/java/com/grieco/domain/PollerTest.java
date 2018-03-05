package com.grieco.domain;

import com.grieco.domain.model.Attributes;
import com.grieco.domain.model.Command;
import com.grieco.mockbuilder.SSHManagerMockBuilder;
import com.grieco.service.SSHManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class PollerTest
{
    private static final String FILE_NAME_1 = "file.txt";
    private static final String FILE_NAME_2 = "other.txt";
    private static final String[] FILE_1 = {FILE_NAME_1};
    private static final String[] FILE_2 = {FILE_NAME_2};
    private static final String[] FILE_1_AND_2 = {FILE_NAME_1, FILE_NAME_2};
    private static final String[] EMPTY = {};
    private static final boolean SUCCESS = true;
    private static final boolean FAIL = false;
    private static final boolean INDIFFERENT = false;

    private String[] pollLocalResult;
    private String[] pollRemoteResult;
    private SSHManager sshManager;
    private String[] actualResult;

    @DataProvider
    public static Object[][] syncProvider()
    {
        return new Object[][]{
                {"No files", EMPTY, EMPTY, SUCCESS, EMPTY},
                {"No remote file", EMPTY, FILE_1, SUCCESS, EMPTY},
                {"No local file", FILE_1, EMPTY, SUCCESS, FILE_1},
                {"Same files", FILE_1, FILE_1, SUCCESS, EMPTY},
                {"Different files", FILE_1, FILE_2, SUCCESS, FILE_1},
                {"More local files", FILE_1, FILE_1_AND_2, SUCCESS, EMPTY},
                {"More remote files", FILE_1_AND_2, FILE_1, SUCCESS, FILE_2},
                {"Error occurred during sync", FILE_1_AND_2, FILE_1, FAIL, null}
        };
    }

    @Test(dataProvider = "syncProvider")
    public void shouldSyncCorrectly(String testCaseName, String[] pollRemoteResult, String[] pollLocalResult,
            boolean isSuccess, String[] expectedResult)
    {
        givenAPollLocalResult(pollLocalResult);
        givenAPollRemoteResult(pollRemoteResult);
        givenAnSshManager(isSuccess);

        whenSyncIsCalled();

        thenResultIs(expectedResult, testCaseName);
    }

    @DataProvider
    public static Object[][] pollProvider()
    {
        return new Object[][]{
                {"Poll local", new Attributes(Command.POLL_LOCAL), FILE_1, FILE_2, FILE_2},
                {"Poll remote", new Attributes(Command.POLL_REMOTE), FILE_1, FILE_2, FILE_1}
        };
    }

    @Test(dataProvider = "pollProvider")
    public void shouldPollCorrectly(String testCaseName, Attributes attributes, String[] pollRemoteResult,
            String[] pollLocalResult, String[] expectedResult)
    {
        givenAPollLocalResult(pollLocalResult);
        givenAPollRemoteResult(pollRemoteResult);
        givenAnSshManager(INDIFFERENT);

        whenPollIsCalled(attributes);

        thenResultIs(expectedResult, testCaseName);
    }

    private void givenAPollLocalResult(String[] pollLocalResult)
    {
        this.pollLocalResult = pollLocalResult;
    }

    private void givenAPollRemoteResult(String[] pollRemoteResult)
    {
        this.pollRemoteResult = pollRemoteResult;
    }

    private void givenAnSshManager(boolean isSuccess)
    {
        this.sshManager = SSHManagerMockBuilder.aManager().withSyncResult(isSuccess).withPollLocalResult(pollLocalResult)
                .withPollRemoteResult(pollRemoteResult).build();
    }

    private void whenSyncIsCalled()
    {
        Poller poller = new Poller(sshManager);
        this.actualResult = poller.sync();
    }

    private void whenPollIsCalled(Attributes attributes)
    {
        Poller poller = new Poller(sshManager);
        this.actualResult = poller.poll(attributes);
    }

    private void thenResultIs(String[] expectedResult, String testCaseName)
    {
        assertEquals(actualResult, expectedResult, "Result should be as expected for " + testCaseName);
    }
}
