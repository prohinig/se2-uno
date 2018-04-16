package at.laubi.network;

import at.laubi.network.session.Session;

public interface CreationListener<T extends Session>{
    default void onSuccess(T session) {}
    default void onException(Exception e) {}
}
