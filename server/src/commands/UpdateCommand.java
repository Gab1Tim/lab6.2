package server.commands;

import common.models.Organization;
import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class UpdateCommand implements Command {

    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Updates an existing Organization by its id";
    }

    @Override
    public Response execute(Request request) {
        try {
            Long id = request.getId();
            Organization organization = request.getOrganization();

            if (id == null) {
                return new Response(false, "Id is required.");
            }
            if (organization == null) {
                return new Response(false, "Organization is required.");
            }

            collectionManager.update(id, organization);
            return new Response(true, "Organization updated successfully.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}