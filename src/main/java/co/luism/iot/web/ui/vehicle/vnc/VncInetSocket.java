package co.luism.iot.web.ui.vehicle.vnc;

import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.net.InetGuacamoleSocket;

/**
 * Created by luis on 01.12.14.
 */
public class VncInetSocket extends InetGuacamoleSocket {
    /**
     * Creates a new InetGuacamoleSocket which reads and writes instructions
     * to the Guacamole instruction stream of the Guacamole proxy server
     * running at the given hostname and port.
     *
     * @param hostname The hostname of the Guacamole proxy server to connect to.
     * @param port     The port of the Guacamole proxy server to connect to.
     * @throws org.glyptodon.guacamole.GuacamoleException If an error occurs while connecting to the
     *                            Guacamole proxy server.
     */
    public VncInetSocket(String hostname, int port) throws GuacamoleException {
        super(hostname, port);
    }
}
