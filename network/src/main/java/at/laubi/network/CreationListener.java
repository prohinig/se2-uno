package at.laubi.network;

import at.laubi.network.session.Session;

public interface CreationListener<T extends Session>{
    void onSuccess(T session);
    void onException(Exception e);
}
