package at.laubi.network.callbacks;

import at.laubi.network.session.Session;

@FunctionalInterface
public interface CreationListener<T extends Session>{
    void createdSession(T session);
}
