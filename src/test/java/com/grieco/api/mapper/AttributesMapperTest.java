package com.grieco.api.mapper;

import com.grieco.api.model.Parameters;
import com.grieco.domain.model.Attributes;
import com.grieco.domain.model.Command;
import org.junit.Before;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class AttributesMapperTest
{
    private static final String INVALID_INPUT = "invalid_input";

    private Parameters parameters;
    private Attributes actualResult;

    @Before
    public void setUp()
    {
        parameters = null;
        actualResult = null;
    }

    @DataProvider
    public static Object[][] parameterProvider()
    {
        return new Object[][]{
            {"Target is local", "local", new Attributes(Command.POLL_LOCAL)},
            {"Target is remote", "remote", new Attributes(Command.POLL_REMOTE)}
        };
    }

    @Test(dataProvider = "parameterProvider")
    public void shouldBeMappedCorrectly(String testCaseName, String target, Attributes expectedResult)
    {
        givenParameters(target);
        whenMapperIsCalled();
        thenResultIs(expectedResult, testCaseName);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionOnInvalidInput()
    {
        givenParameters(INVALID_INPUT);
        whenMapperIsCalled();
    }

    private void givenParameters(String target)
    {
        this.parameters = new Parameters();
        this.parameters.setTarget(target);
    }

    private void whenMapperIsCalled()
    {
        AttributesMapper mapper = new AttributesMapper();
        this.actualResult = mapper.map(parameters);
    }

    private void thenResultIs(Attributes expectedResult, String testCaseName)
    {
        assertEquals(actualResult, expectedResult, "Result should be as expected for " + testCaseName);
    }
}
