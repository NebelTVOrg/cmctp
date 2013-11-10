package tv.nebel.cmctp;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

/**
 * CMCTP
 *
 * @author Dmitry Mykhaylov <Dmitry@Mykhaylov.info>
 * @version 1.0
 * @since 10.11.2013 17:15:00
 */
@Test (groups = "CMCTP.Main")
public class MainTest {
    public static final boolean IS_PARALLEL_DATA_PROVIDER = true;

    public static final int THREAD_POOL_SIZE = 10;

    public static String GEARMAN_HOST = "ec2-54-194-30-221.eu-west-1.compute.amazonaws.com";
    public static int    GEARMAN_PORT = 4730;

    private TestService testService;

    @BeforeTest
    public void testSetUp() {
        testService = TestServiceBuilder.build(GEARMAN_HOST, GEARMAN_PORT);
    }

    @AfterTest
    public void tearDown() throws Exception {
        testService.close();
        testService = null;
    }

    @Test (dataProvider = "dataProviderTestCases", invocationCount = 1, threadPoolSize = THREAD_POOL_SIZE)
    public void testCases(final String url, final String hashCode) throws InterruptedException {
        assertTrue(testService.check(url, hashCode), String.format("Test case for `%s' FAILED", url));
    }

    @DataProvider (parallel = IS_PARALLEL_DATA_PROVIDER)
    private Object[][] dataProviderTestCases() {
        return new Object[][] {
            { "http://en.wikipedia.org/wiki/Algorithm", "10800d8562db0bc2f6a267c18672b680" },
            { "http://en.wikipedia.org/wiki/Computer_science", "83275d168634f10a8e96a9e9f4d826cc" },
            { "http://www.ukr.net", "b314b1e38ae9fd1e1c85aab376be316d" },
        };
    }
}
