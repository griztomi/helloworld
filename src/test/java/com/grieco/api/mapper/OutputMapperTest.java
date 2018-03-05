package com.grieco.api.mapper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.grieco.api.mapper.OutputMapper.*;
import static org.testng.Assert.assertEquals;

@Test
public class OutputMapperTest
{
    private static final String FILE_NAME_1 = "file.txt";
    private static final String FILE_NAME_2 = "other.txt";

    private String actualResult;

    @BeforeMethod
    public void setUp()
    {
        actualResult = null;
    }

    @DataProvider
    public static Object[][] syncedFilesProvider()
    {
        return new Object[][]{
                {"Sync null", null, SYNC_ERROR_MESSAGE},
                {"Sync no file", new String[0], SYNC_EMPTY_MESSAGE},
                {"Sync single file", new String[]{FILE_NAME_1}, SYNC_SUCCESS_MESSAGE + NEW_LINE + FILE_NAME_1},
                {"Sync multiple files", new String[]{FILE_NAME_1, FILE_NAME_2},
                        SYNC_SUCCESS_MESSAGE + NEW_LINE + FILE_NAME_1 + NEW_LINE + FILE_NAME_2}
        };
    }

    @Test(dataProvider = "syncedFilesProvider")
    public void shouldMapSyncedFilesCorrectly(String testCaseName, String[] syncedFiles, String expectedResult)
    {
        whenMapSyncedFilesIsCalled(syncedFiles);
        thenResultIs(expectedResult, testCaseName);
    }

    @DataProvider
    public static Object[][] pollResultProvider()
    {
        return new Object[][]{
                {"Poll no file", new String[0], POLL_EMPTY_MESSAGE},
                {"Poll single file", new String[]{FILE_NAME_1}, POLL_SUCCESS_MESSAGE + NEW_LINE + FILE_NAME_1},
                {"Poll multiple files", new String[]{FILE_NAME_1, FILE_NAME_2},
                        POLL_SUCCESS_MESSAGE + NEW_LINE + FILE_NAME_1 + NEW_LINE + FILE_NAME_2}
        };
    }

    @Test(dataProvider = "pollResultProvider")
    public void shouldMapPollResultCorrectly(String testCaseName, String[] pollResult, String expectedResult)
    {
        whenMapPollResultIsCalled(pollResult);
        thenResultIs(expectedResult, testCaseName);
    }

    private void whenMapSyncedFilesIsCalled(String[] syncedFiles)
    {
        OutputMapper mapper = new OutputMapper();
        this.actualResult = mapper.mapSyncedFiles(syncedFiles);
    }

    private void whenMapPollResultIsCalled(String[] syncedFiles)
    {
        OutputMapper mapper = new OutputMapper();
        this.actualResult = mapper.mapPollResult(syncedFiles);
    }

    private void thenResultIs(String expectedResult, String testCaseName)
    {
        assertEquals(actualResult, expectedResult, "Result should be as expected for " + testCaseName);
    }
}
