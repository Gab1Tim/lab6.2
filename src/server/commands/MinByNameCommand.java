package server.commands;

import common.models.Organization;
import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class MinByNameCommand implements Command {

    private final CollectionManager collectionManager;

    public MinByNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "min_by_name";
    }

    @Override
    public String getDescription() {
        return "Shows the organization with the minimum name";
    }

    @Override
    public Response execute(Request request) {
        Organization org = collectionManager.getMinByName();
        if (org == null) {
            return new Response(true, "Collection is empty.");
        }
        return new Response(true, org.toString());
    }
}