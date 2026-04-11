package commands;

import managers.CommandManager;

/**
 * Команда {@code help}: печатает список команд.
 */
public class HelpCommand implements Command {

    private CommandManager commandManager;

    /** Создаёт команду. */
    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }
    @Override
    public String getDescription() {
        return "Displays a list of available commands";
    }

    @Override
    public void execute(String[] commands) {
        commandManager.showAllCommands();
    }

}
