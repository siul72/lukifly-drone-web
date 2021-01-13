package co.luism.iot.web.interfaces;

/**
 * Created by luis on 07.11.14.
 */
public interface OnDiagCustomComponent{

    public void buildMainLayout();
    public void setCaptionNames(String currentLanguage);
    public void startUp();
    public void closeDown();

}
