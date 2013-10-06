package domain;

public class Request {

    public final String requestText;

    public Request(String requestText) {
        this.requestText = requestText;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestText='" + requestText + '\'' +
                '}';
    }
}