package server.commands;

import common.network.Request;
import common.network.Response;
import server.managers.CollectionManager;

public class ShowCommand implements Command {

    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Shows all elements in the collection";
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, collectionManager.getShowData());
    }
}