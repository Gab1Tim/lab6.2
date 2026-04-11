package managers;

import commands.Command;
import java.util.HashMap;
import java.util.Map;

/**
 * Регистрирует и выполняет команды, а также хранит историю.
 */
public class CommandManager {

    private Map<String, Command> commands = new HashMap<>();
    private java.util.LinkedList<String> history = new java.util.LinkedList<>();
    private final int HISTORY_LIMIT = 12;


    /** Регистрирует команду по её имени. */
    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    /** Разбирает строку ввода и выполняет соответствующую команду. */
    public void executeCommand(String inputLine) {
        if (inputLine == null || inputLine.isEmpty()) return;

        String[] parts = inputLine.trim().split("\\s+", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        Command command = commands.get(commandName);

        if (command != null) {

            history.add(commandName);

            if (history.size() > HISTORY_LIMIT) {
                history.removeFirst();
            }
            command.execute(args);
        } else {
            System.out.println("Unknown command: " + commandName);
        }
    }

    /** Печатает список всех доступных команд. */
    public void showAllCommands() {
        for (Command command : commands.values()) {
            System.out.println(command.getName() + " - " + command.getDescription());
        }
    }

    /** Печатает историю последних выполненных команд. */
    public void showHistory() {
        for (String cmd : history) {
            System.out.println(cmd);
        }
    }
}