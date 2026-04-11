import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import managers.InputManager;

/**
 * Точка входа приложения.
 */
public class Main {

    /**
     * Запускает интерактивный цикл чтения и выполнения команд.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {

        String fileName = System.getenv("ORGANIZATION_FILE");

        CollectionManager collectionManager =
                new CollectionManager(fileName);

        CommandManager commandManager = new CommandManager();

        commandManager.registerCommand(new InsertCommand(collectionManager));
        commandManager.registerCommand(new ShowCommand(collectionManager));
        commandManager.registerCommand(new InfoCommand(collectionManager));
        commandManager.registerCommand(new RemoveKeyCommand(collectionManager));
        commandManager.registerCommand(new ClearCommand(collectionManager));
        commandManager.registerCommand(new HelpCommand(commandManager));
        commandManager.registerCommand(new ExitCommand());
        commandManager.registerCommand(new HistoryCommand(commandManager));
        commandManager.registerCommand(new UpdateCommand(collectionManager));
        commandManager.registerCommand(new RemoveGreaterKeyCommand(collectionManager));
        commandManager.registerCommand(new MinByNameCommand(collectionManager));
        commandManager.registerCommand(new FilterGreaterThanTypeCommand(collectionManager));
        commandManager.registerCommand(new PrintUniqueAnnualTurnoverCommand(collectionManager));
        commandManager.registerCommand(new SaveCommand(collectionManager));
        commandManager.registerCommand(new RemoveLowerCommand(collectionManager, new InputManager()));
        commandManager.registerCommand(new ExecuteScriptCommand(commandManager));

        System.out.println("Welcome! Type commands:");

        while (true) {
            String inputLine = InputManager.readLine("> ");
            if (inputLine == null) break;
            commandManager.executeCommand(inputLine);
        }
    }
}