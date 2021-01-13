package co.luism.iot.web.interfaces;

/**
 * Created by luis on 11.11.14.
 */
public interface ICloseDownTrigger {

    public void addListener(ICloseDownEventHandler listener);
    public void removeListener(ICloseDownEventHandler listener);
}
