package server.commands;

import common.network.Request;
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
    public CommandResult execute(Request request) {
        try {
            Integer key = request.getKey();
            if (key == null) {
                return new CommandResult(false, "Key is required.");
            }

            collectionManager.remove(key);
            return new CommandResult(true, "Element with key " + key + " removed successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}