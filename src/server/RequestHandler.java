package server;

import common.network.Request;
import common.network.Response;
import server.managers.CommandManager;

public class RequestHandler {

    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        return commandManager.execute(request);
    }
}