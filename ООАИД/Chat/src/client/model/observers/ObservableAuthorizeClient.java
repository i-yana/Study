package client.model.observers;

import client.model.listeners.ClientAuthorizeListener;

/**
 * Created by Yana on 14.12.15.
 */
public interface ObservableAuthorizeClient {

    void subscribeToChanging(ClientAuthorizeListener listener);
    public void notifyAboutAnswerAboutCompleteRegistration();
    public void notifyAboutAnswerAboutError(String reason);
    public void notifyAboutAnswerAboutCompleteAuthorization();
}
