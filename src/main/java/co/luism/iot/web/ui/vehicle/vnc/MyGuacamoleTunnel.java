package co.luism.iot.web.ui.vehicle.vnc;

import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.io.GuacamoleReader;
import org.glyptodon.guacamole.io.GuacamoleWriter;
import org.glyptodon.guacamole.net.GuacamoleSocket;
import org.glyptodon.guacamole.net.GuacamoleTunnel;

import java.util.UUID;

public class MyGuacamoleTunnel implements GuacamoleTunnel {
    public MyGuacamoleTunnel(GuacamoleSocket socket) {
    }

    @Override
    public GuacamoleReader acquireReader() {
        return null;
    }

    @Override
    public void releaseReader() {

    }

    @Override
    public boolean hasQueuedReaderThreads() {
        return false;
    }

    @Override
    public GuacamoleWriter acquireWriter() {
        return null;
    }

    @Override
    public void releaseWriter() {

    }

    @Override
    public boolean hasQueuedWriterThreads() {
        return false;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public GuacamoleSocket getSocket() {
        return null;
    }

    @Override
    public void close() throws GuacamoleException {

    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
