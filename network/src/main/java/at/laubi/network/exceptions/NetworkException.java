package at.laubi.network.exceptions;

public class NetworkException extends Exception {
    public NetworkException() {
        super();
    }

    public NetworkException(String s) {
        super(s);
    }

    public NetworkException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NetworkException(Throwable throwable) {
        super(throwable);
    }
}
