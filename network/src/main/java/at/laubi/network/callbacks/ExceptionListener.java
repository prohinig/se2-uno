package at.laubi.network.callbacks;

import at.laubi.network.session.Session;

@FunctionalInterface
public interface ExceptionListener {
    void onException(Exception e, Session s);
}
