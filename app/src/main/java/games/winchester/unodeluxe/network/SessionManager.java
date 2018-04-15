package games.winchester.unodeluxe.network;

import games.winchester.unodeluxe.messages.Message;

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
        for(SessionEvents e : listeners) e.connected(session);
    }

    void emitNewMessage(Session session, Message message){
        for(SessionEvents e : listeners) e.newMessage(session, message);
    }

    void emitClosed(Session session){
        for(SessionEvents e : listeners) e.closed(session);
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
