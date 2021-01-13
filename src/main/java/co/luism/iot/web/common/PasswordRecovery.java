package co.luism.iot.web.common;

/**
 * Created by luis on 15.01.15.
 */
public class PasswordRecovery {

    private Boolean enabled = false;
    private String login;
    private String userName;
    private String firstName;


    public PasswordRecovery() {

    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
