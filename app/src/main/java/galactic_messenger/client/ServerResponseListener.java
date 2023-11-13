package galactic_messenger.client;
import galactic_messenger.utils.StyledMessage;

public interface ServerResponseListener {
    void updateChat(StyledMessage message);
}
