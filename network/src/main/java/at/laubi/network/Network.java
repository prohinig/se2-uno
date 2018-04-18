package at.laubi.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.callbacks.CreationListener;
import at.laubi.network.callbacks.ExceptionListener;
import at.laubi.network.callbacks.MessageListener;
import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;

public class Network {
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final NetworkOptions options;
    private Session currentSession;

    private ExceptionListener fallbackExceptionListener;
    private MessageListener messageListener;

    public Network() {
        this(null);
    }

    public Network(NetworkOptions options){
        this.options = options == null ? new NetworkOptions() : options;
    }

    public void addTask(Runnable runnable){
        this.executor.submit(runnable);
    }

    public NetworkOptions getOptions() {
        return options;
    }

    public void broadcastMessageReceived(Message m, Session s){
        if(messageListener != null)
            messageListener.onMessageReceived(m, s);
    }

    public void createHost(final CreationListener<HostSession> creationListener, final ExceptionListener exceptionListener){
        if(creationListener == null)
            throw new IllegalArgumentException("creation listener must not be null");

        this.addTask(() -> {
            try {
                HostSession session = HostSession.open(Network.this);
                Network.this.currentSession = session;
                creationListener.createdSession(session);
            }catch (Exception e){
                callException(exceptionListener, e, null);
            }
        });
    }

    public void createClient(final String host, final CreationListener<ClientSession> creationListener, final ExceptionListener exceptionListener) {
        if(host == null) throw new IllegalArgumentException("Host must not be null");
        if(creationListener == null) throw new IllegalArgumentException("Listener must not be null");

        this.addTask(() -> {
            try{
                ClientSession session = ClientSession.open(host, Network.this);
                Network.this.currentSession = session;
                creationListener.createdSession(session);
            }catch (Exception e) {
                callException(exceptionListener, e, null);
            }
        });
    }

    /**
     * <p>Calls exceptions in their correct order, if they are not null.
     * First, the nearest exception is called, then, the module wide exception is called.<br>
     * <b>If no layer has been set, no exception is passed.</b></p>
     *
     * <p>To pass exceptions directly to the module layer, pass the first argument which null,
     * which defaults to a usual module wide call.</p>
     *
     * @param currentExceptionListener The exception handler may be passed to a function. May be null.
     * @param exception The exception which was thrown.
     * @param session The session in which the exception occured. May be null.
     */
    public void callException(ExceptionListener currentExceptionListener, Exception exception, Session session){
        if(currentExceptionListener != null)
            currentExceptionListener.onException(exception, session);

        else if(fallbackExceptionListener != null)
            fallbackExceptionListener.onException(exception, session);

        // Ignore if no callback has been set
    }

    public Session getCurrentSession(){
        return this.currentSession;
    }

    public void setFallbackExceptionListener(ExceptionListener fallbackExceptionListener) {
        this.fallbackExceptionListener = fallbackExceptionListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
