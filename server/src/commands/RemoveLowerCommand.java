package server.commands;

import common.models.Organization;
import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class RemoveLowerCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveLowerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "Removes all elements lower than the given element";
    }

    @Override
    public Response execute(Request request) {
        try {
            Organization reference = request.getOrganization();

            if (reference == null) {
                return new Response(false, "Reference organization is required.");
            }

            int removed = collectionManager.removeLower(reference);
            return new Response(true, removed + " elements removed.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}