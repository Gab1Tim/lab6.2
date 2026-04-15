package server;

import common.network.CommandType;

import server.commands.*;
import server.managers.CollectionManager;
import server.managers.CommandManager;

public class ServerMain {

    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        String fileName = System.getenv("ORGANIZATION_FILE");

        CollectionManager collectionManager = new CollectionManager(fileName);
        CommandManager commandManager = new CommandManager();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                collectionManager.saveSafely();
                System.out.println("Collection saved. Server is shutting down.");
            } catch (Exception e) {
                System.out.println("Shutdown save error: " + e.getMessage());
            }
        }));

        commandManager.registerCommand(CommandType.INSERT, new InsertCommand(collectionManager));
        commandManager.registerCommand(CommandType.SHOW, new ShowCommand(collectionManager));
        commandManager.registerCommand(CommandType.INFO, new InfoCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_KEY, new RemoveKeyCommand(collectionManager));
        commandManager.registerCommand(CommandType.CLEAR, new ClearCommand(collectionManager));
        commandManager.registerCommand(CommandType.UPDATE, new UpdateCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_GREATER_KEY, new RemoveGreaterKeyCommand(collectionManager));
        commandManager.registerCommand(CommandType.MIN_BY_NAME, new MinByNameCommand(collectionManager));
        commandManager.registerCommand(CommandType.FILTER_GREATER_THAN_TYPE, new FilterGreaterThanTypeCommand(collectionManager));
        commandManager.registerCommand(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER, new PrintUniqueAnnualTurnoverCommand(collectionManager));
        commandManager.registerCommand(CommandType.REMOVE_LOWER, new RemoveLowerCommand(collectionManager));
        commandManager.registerCommand(CommandType.HELP, new HelpCommand(commandManager));

        RequestHandler requestHandler = new RequestHandler(commandManager);
        UdpServer udpServer = new UdpServer(DEFAULT_PORT, requestHandler);

        System.out.println("Server started on port " + DEFAULT_PORT);
        udpServer.start();
    }
}