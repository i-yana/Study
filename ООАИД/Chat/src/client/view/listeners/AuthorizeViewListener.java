package client.view.listeners;

/**
 * Created by Yana on 13.12.15.
 */
public interface AuthorizeViewListener {

    void handleLogin(String login, String password);

    void handleRegistration(String login, String password);

    void handleOpenMainMenu();
}
