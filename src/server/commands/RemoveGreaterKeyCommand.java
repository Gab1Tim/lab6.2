package server.commands;

import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class RemoveGreaterKeyCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveGreaterKeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_greater_key";
    }

    @Override
    public String getDescription() {
        return "Removes all elements with key greater than the given key";
    }

    @Override
    public Response execute(Request request) {
        try {
            Integer key = request.getKey();

            if (key == null) {
                return new Response(false, "Reference key is required.");
            }

            collectionManager.removeGreaterKey(key);
            return new Response(true, "Elements with greater key removed successfully.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}