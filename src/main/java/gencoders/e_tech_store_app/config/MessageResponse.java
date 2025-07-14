package gencoders.e_tech_store_app.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {

        this.message = message;
    }
}