package games.winchester.unodeluxe.network.tasks;

import java.io.ObjectOutputStream;

import games.winchester.unodeluxe.messages.Message;
import games.winchester.unodeluxe.network.ClientSession;

public class MessageSendTask implements Runnable {
    private Message message;
    private ClientSession session;
    private MessageSendListener listener;

    public interface MessageSendListener{
        void onException(Exception e);
    }

    public MessageSendTask(Message message, ClientSession session, MessageSendListener listener) {
        this.message = message;
        this.session = session;
        this.listener = listener;
    }

    @Override
    public void run() {
        ObjectOutputStream out = session.getOut();

        try {
            out.writeObject(message);
        }catch(Exception e){
            if(listener != null) listener.onException(e);
            else session.getNetwork().callFallbackException(e, session);
        }

    }
}
