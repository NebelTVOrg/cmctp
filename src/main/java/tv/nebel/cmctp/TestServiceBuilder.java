package tv.nebel.cmctp;

/**
 * CMCTP
 *
 * @author Dmitry Mykhaylov <Dmitry@Mykhaylov.info>
 * @version 1.0
 * @since 10.11.2013 17:31:33/Kiev Time Zone
 */
public class TestServiceBuilder {

    public static TestService build(final String host, final int port) {
        return new TestServiceImpl(host, port);
    }
}
