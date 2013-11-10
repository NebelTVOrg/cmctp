package tv.nebel.cmctp;

import java.util.concurrent.atomic.AtomicBoolean;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobEventCallback;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;

/**
 * CMCTP
 *
 * @author Dmitry Mykhaylov <Dmitry@Mykhaylov.info>
 * @version 1.0
 * @since 10.11.2013 4:36 AM
 */
class TestServiceImpl implements TestService {

    public static final String GERAMAN_JOB_NAME = "cmctp-job";

    public static final String SUCCESS_CODE = "OK";
    public static final String FAILURE_CODE = "NOK";

    private final Gearman gearman;

    private final GearmanServer gearmanServer;

    TestServiceImpl(final String host, final int port) {
        gearman = Gearman.createGearman();

        gearmanServer = gearman.createGearmanServer(host, port);
    }

    @Override
    public boolean check(final String url, final String hash) throws InterruptedException {
        final GearmanClient gearmanClient = gearman.createGearmanClient();
        gearmanClient.addServer(gearmanServer);

        final AtomicBoolean result = new AtomicBoolean(false);
        final GearmanJoin<String> join = gearmanClient.submitJob(
            GERAMAN_JOB_NAME,
            buildRawJob(url, hash),

            GERAMAN_JOB_NAME,
            new GearmanJobEventCallback<String>() {
                @Override
                public void onEvent(final String attachment, final GearmanJobEvent event) {
                    switch (event.getEventType()) {
                        case GEARMAN_JOB_SUCCESS:
                            final String response = new String(event.getData()).trim();
                            result.set(response.equals(SUCCESS_CODE));
                            break;
                        case GEARMAN_EOF:
                            break;
                        default:
                            result.set(false);
                    }
                }
            }
        );

        join.join();

        return result.get();
    }

    @Override
    public void close() {
//        gearman.shutdown();
    }

    private static byte[] buildRawJob(final String url, final String hash) {
        return String.format(
            "wget -q -O /dev/stdout -- %s | md5sum | awk '{ if ($1 == \"%s\") {print \"%s\"} else { print \"%s\"} }%n'",
            url, hash, SUCCESS_CODE, FAILURE_CODE
        ).getBytes();
    }
}
