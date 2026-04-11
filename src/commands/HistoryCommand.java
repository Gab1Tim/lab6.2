package commands;

import managers.CommandManager;

/**
 * Команда {@code history}: показывает историю команд.
 */
public class HistoryCommand implements Command {

    private CommandManager  commandManager;

    /** Создаёт команду. */
    public HistoryCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName () {
        return "history";
    }
    @Override
    public String getDescription () {
        return "Shows last 12 commands";
    }
    @Override
    /** Выполняет вывод истории. */
    public void execute(String [] args) {
        commandManager.showHistory();
    }
}


