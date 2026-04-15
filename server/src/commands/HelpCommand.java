package server.commands;

import common.network.Request;
import common.network.Response;
import server.managers.CommandManager;

public class HelpCommand implements Command {

    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of available server commands";
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, commandManager.getAllCommandsInfo());
    }
}