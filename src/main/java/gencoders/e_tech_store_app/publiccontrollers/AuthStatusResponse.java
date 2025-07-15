package gencoders.e_tech_store_app.publiccontrollers;

public class AuthStatusResponse {
    private final boolean authenticated;
    private final String message;
    private final String accessLevel;

    public AuthStatusResponse(boolean authenticated, String message, String accessLevel) {
        this.authenticated = authenticated;
        this.message = message;
        this.accessLevel = accessLevel;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessLevel() {
        return accessLevel;
    }
}