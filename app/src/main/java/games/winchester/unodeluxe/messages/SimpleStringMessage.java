package at.laubi.proofofconcept.messages;

public class SimpleStringMessage implements Message {
    private String message;

    public SimpleStringMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SimpleStringMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
