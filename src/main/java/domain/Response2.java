package domain;

public class Response2 {

    private final String responseText;
    private Integer index;

    public Response2(String responseText) {
        this.responseText = responseText;
    }

    public Response2(String responseText, Integer index) {
        this.responseText = responseText;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Response2{" +
                "responseText='" + responseText + '\'' +
                ", index=" + index +
                '}';
    }
}
