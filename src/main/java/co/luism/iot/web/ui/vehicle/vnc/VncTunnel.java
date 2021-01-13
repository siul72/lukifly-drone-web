package co.luism.iot.web.ui.vehicle.vnc;

import co.luism.iot.web.common.WebConfig;
import org.apache.log4j.Logger;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.net.GuacamoleSocket;
import org.glyptodon.guacamole.net.GuacamoleTunnel;
import org.glyptodon.guacamole.net.InetGuacamoleSocket;
import org.glyptodon.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;
import org.glyptodon.guacamole.servlet.GuacamoleHTTPTunnelServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by luis on 24.11.14.
 */
public class VncTunnel extends GuacamoleHTTPTunnelServlet {

    private static final Logger LOG = Logger.getLogger(VncTunnel.class);

    private InetGuacamoleSocket inet;
    private GuacamoleSocket socket;
    private GuacamoleTunnel tunnel;
    private Integer port;

    protected GuacamoleTunnel doConnect(HttpServletRequest request, GuacamoleConfiguration config) throws GuacamoleException {
        // Create our configuration

        config.setProtocol(WebConfig.VNC_PROTOCOL);
        config.setParameter("hostname", WebConfig.VNC_SERVER_ADDRESS);
        //config.setParameter("hostname", "192.168.0.31");
        //config.setParameter("hostname", "127.0.0.1");
        //config.setParameter("port", "5901");
        //config.setParameter("port", port);
        //config.setParameter("password", "admadm");
        config.setParameter("password", WebConfig.VNC_SERVER_PASSWORD);

        port = Integer.parseInt(config.getParameter("port"));

        LOG.info(String.format("Tunnel to VNC %s:%d", config.getParameter("hostname"),
                port));

        VncConnectionManager.getInstance().register(this);


        if (inet != null) {
            inet.close();
        }

        inet = new InetGuacamoleSocket("localhost", 4822);

        if (socket != null) {
            socket.close();
        }

        socket = new ConfiguredGuacamoleSocket(inet, config);

        if (tunnel != null) {
            tunnel.close();
        }

        tunnel = new MyGuacamoleTunnel(socket);

        // Return pre-attached tunnel
        return tunnel;
    }

    @Override
    protected void handleTunnelRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        super.handleTunnelRequest(request, response);

        //LOG.debug("new request " + request.getQueryString());
    }

    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest httpServletRequest) throws GuacamoleException {
        return null;
    }


    @Override
    protected void doRead(HttpServletRequest request, HttpServletResponse response, String uuid) {

        try {
            super.doRead(request, response, uuid);
        } catch (GuacamoleException e) {
            LOG.error(e);
            close();
        }

    }

    @Override
    protected void doWrite(HttpServletRequest request, HttpServletResponse response, String uuid) {

        try {
            super.doWrite(request, response, uuid);
        } catch (GuacamoleException e) {
            LOG.error(e);
            close();
        }

    }

    private void close() {

        LOG.debug("about to close");

        VncConnectionManager.getInstance().unregister(this);
        if (tunnel != null) {
            try {
                if (tunnel.isOpen()) {
                    //super.close(tunnel);
                    tunnel.close();
                }
                tunnel = null;

            } catch (GuacamoleException e) {
                LOG.error(e);
            }
        }

        if (socket != null) {
            if (socket.isOpen()) {
                try {
                    socket.close();
                } catch (GuacamoleException e) {
                    LOG.error(e);
                }

                socket = null;
            }

        }

        if (inet != null) {
            try {
                if (inet.isOpen()) {
                    inet.close();
                }

                inet = null;

            } catch (GuacamoleException e) {
                LOG.error(e);
            }
        }

    }


    public Integer getPort() {
        return port;
    }
//
//    @Override
//    protected void doWrite(HttpServletRequest request, HttpServletResponse response, String uuid) throws GuacamoleException {
//        super.doWrite(request, response, uuid);
//        System.out.println("write request " + request.getQueryString());
//        System.out.println("write response " + response.toString());
//    }

}
