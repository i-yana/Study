package client.controller;

import client.Config;
import client.model.Client;
import client.view.authorization.AuthorizationView;
import client.view.listeners.AuthorizeViewListener;

/**
 * Created by Yana on 13.12.15.
 */
public class AuthorizationController implements AuthorizeViewListener {

    private Client client;
    private AuthorizationView authorizationView;

    public AuthorizationController(){
        authorizationView = new AuthorizationView();
        authorizationView.subscribeToChange(this);
        authorizationView.setVisible(true);
    }

    @Override
    public void handleLogin(String login, String password) {
        client = new Client(Config.HOST, Config.PORT);
        client.subscribeToChanging(authorizationView);
        client.authorization(login, password);
    }

    @Override
    public void handleRegistration(String login, String password) {
        client = new Client(Config.HOST, Config.PORT);
        client.subscribeToChanging(authorizationView);
        client.registration(login, password);
    }

    @Override
    public void handleOpenMainMenu() {
        MenuController controller = new MenuController(client);
        authorizationView.setVisible(false);
    }
}
