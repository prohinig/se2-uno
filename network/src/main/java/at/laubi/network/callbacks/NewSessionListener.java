package at.laubi.network.callbacks;

import at.laubi.network.session.ClientSession;

public interface NewSessionListener {
    void onNewSession(ClientSession s);
}
