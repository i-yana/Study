package client.view.observers;

import client.view.listeners.AuthorizeViewListener;

/**
 * Created by Yana on 13.12.15.
 */
public interface ObservableAuthorizeView {

    void subscribeToChange(AuthorizeViewListener listener);

    void notifyAboutAuthorization(String login, String password);

    void notifyAboutRegistration(String login, String password);

    void notifyAboutOpenMainMenu();
}
