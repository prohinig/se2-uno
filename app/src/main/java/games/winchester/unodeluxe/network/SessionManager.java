package at.laubi.proofofconcept.network;

import at.laubi.proofofconcept.messages.Message;

import java.util.*;

@SuppressWarnings("unused")
public class SessionManager {
    private final static SessionManager instance = new SessionManager();
    private final List<SessionEvents> listeners = new ArrayList<>();
    private final HashMap<Long, Session> sessions = new LinkedHashMap<>();

    private SessionManager() { }


    void register(Session session){
        sessions.put(session.getSessionId(), session);
        emitConnected(session);
    }

    private void emitConnected(Session session){
        listeners.forEach(l -> l.connected(session));
    }

    void emitNewMessage(Session session, Message message){
        listeners.forEach(l -> l.newMessage(session, message));
    }

    void emitClosed(Session session){
        listeners.forEach(l -> l.closed(session));
    }

    public void addListener(SessionEvents listener) {
        this.listeners.add(listener);
    }

    public void removeListener(SessionEvents listener){
        this.listeners.remove(listener);
    }

    public void clear(){
        this.listeners.clear();
        this.sessions.clear();
    }

    public Collection<Session> getSessions(){
        return this.sessions.values();
    }

    public static SessionManager getInstance(){
        return instance;
    }
}
