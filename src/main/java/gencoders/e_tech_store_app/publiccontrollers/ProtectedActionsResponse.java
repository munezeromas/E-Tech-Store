package gencoders.e_tech_store_app.publiccontrollers;

import java.util.List;

public class ProtectedActionsResponse {
    private final List<String> actions;

    public ProtectedActionsResponse(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getActions() {
        return actions;
    }
}