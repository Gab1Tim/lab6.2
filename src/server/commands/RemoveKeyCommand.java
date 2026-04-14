package server.commands;

import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class RemoveKeyCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveKeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getDescription() {
        return "Removes an element by key";
    }

    @Override
    public Response execute(Request request) {
        try {
            Integer key = request.getKey();

            if (key == null) {
                return new Response(false, "Key is required.");
            }

            collectionManager.remove(key);
            return new Response(true, "Element with key " + key + " removed successfully.");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}