package co.luism.iot.web.ui.vehicle.vnc;

/**
 * Created by luis on 27.11.14.
 */
class VncPort implements Comparable {

    private final Integer port;
    private String vehicleId;
    private Integer numberOfClients;
    private boolean free = true;

    public VncPort(int i) {
        this.port = i;
    }

    public Integer getPort() {
        return port;
    }


    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Integer getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(Integer numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof VncPort) {
            return this.port.compareTo(((VncPort) o).getPort());
        }

        return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VncPort) {
            if (this.port.equals(((VncPort) other).getPort())){
                return true;
            }

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.port.hashCode();

        return hash;

    }


}
