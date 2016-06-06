package client.model.listeners;

/**
 * Created by Yana on 14.12.15.
 */
public interface ClientAuthorizeListener {

    void handleCompleteRegistration();

    void handleCompleteAuthorization();

    void handleAnswerAboutError(String reason);
}
