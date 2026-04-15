package client;

import client.commands.ExecuteScriptCommand;
import client.commands.ExitCommand;
import client.commands.HistoryCommand;
import client.managers.ClientCommandManager;
import client.managers.InputManager;
import common.network.Request;
import common.network.Response;

public class Client {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private final ClientCommandManager clientCommandManager;
    private final UdpClient udpClient;

    public Client() {
        this.clientCommandManager = new ClientCommandManager();
        this.udpClient = new UdpClient(DEFAULT_HOST, DEFAULT_PORT);
        registerLocalCommands();
    }

    private void registerLocalCommands() {
        clientCommandManager.registerCommand(new HistoryCommand(clientCommandManager));
        clientCommandManager.registerCommand(new ExecuteScriptCommand(this));
        clientCommandManager.registerCommand(new ExitCommand());
    }

    public void run() {
        System.out.println("Welcome! Type commands:");

        while (true) {
            String inputLine = InputManager.readLine("> ");
            if (inputLine == null) {
                break;
            }

            processInputLine(inputLine);
        }
    }

    public void processInputLine(String inputLine) {
        if (inputLine == null) {
            return;
        }

        inputLine = inputLine.trim();
        if (inputLine.isEmpty()) {
            return;
        }

        String commandName = inputLine.split("\\s+", 2)[0];

        if (commandName.equals("help")) {
            showHelp();
            return;
        }

        if (clientCommandManager.hasCommand(commandName)) {
            clientCommandManager.executeCommand(inputLine);
        } else {
            sendCommandToServer(commandName);
        }
    }

    private void sendCommandToServer(String commandName) {
        Request request = RequestFactory.buildRequest(commandName);

        if (request == null) {
            System.out.println("Unknown command or invalid arguments.");
            return;
        }

        Response response = udpClient.sendAndReceive(request);
        System.out.println(response.getMessage());
    }

    private void showHelp() {
        System.out.println("Local commands:");
        System.out.println("help - Displays help");
        System.out.println("history - Displays last 12 commands");
        System.out.println("execute_script file_name - Executes commands from file");
        System.out.println("exit - Exits the program");

        System.out.println("\nServer commands:");
        sendCommandToServer("help");
    }
}