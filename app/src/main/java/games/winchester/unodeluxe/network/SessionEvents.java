package games.winchester.unodeluxe.network;

import at.laubi.proofofconcept.messages.Message;

public interface SessionEvents {
    void connected(Session session);
    void newMessage(Session session, Message message);
    void closed(Session session);
}
