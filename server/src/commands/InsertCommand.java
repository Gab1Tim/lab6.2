package server.commands;

import common.models.Organization;
import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class InsertCommand implements Command {

    private final CollectionManager collectionManager;

    public InsertCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "Inserts a new Organization with a unique key";
    }

    @Override
    public Response execute(Request request) {
        try {
            Integer key = request.getKey();
            Organization organization = request.getOrganization();

            if (key == null) {
                return new Response(false, "Key is required.");
            }
            if (organization == null) {
                return new Response(false, "Organization is required.");
            }

            collectionManager.insert(key, organization);
            return new Response(true, "Organization added successfully.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}