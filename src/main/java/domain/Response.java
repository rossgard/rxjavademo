package domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class Response {

    @JsonProperty("responseText")
    public final String responseText;

    public Response(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseText='" + responseText + '\'' +
                '}';
    }
}