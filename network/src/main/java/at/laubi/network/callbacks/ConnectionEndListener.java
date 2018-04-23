package at.laubi.network.callbacks;

import at.laubi.network.session.Session;

@FunctionalInterface
public interface ConnectionEndListener {
    void onConnectionEnd(Session socket);
}
