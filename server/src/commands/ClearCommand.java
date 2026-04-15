package server.commands;

import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class ClearCommand implements Command {

    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears the collection";
    }

    @Override
    public Response execute(Request request) {
        try {
            collectionManager.clear();
            return new Response(true, "Collection cleared successfully.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}