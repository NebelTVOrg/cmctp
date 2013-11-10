package tv.nebel.cmctp;

/**
 * CMCTP
 *
 * @author Dmitry Mykhaylov <Dmitry@Mykhaylov.info>
 * @version 1.0
 * @since 10.11.2013 4:34 AM
 */
public interface TestService extends AutoCloseable {

    boolean check(final String url, final String hash) throws InterruptedException;
}
