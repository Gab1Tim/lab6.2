package commands;

/**
 * Интерфейс команды консольного приложения.
 */
public interface Command {

    /** @return имя команды (токен ввода) */
    String getName();
    /** @return краткое описание команды */
    String getDescription();
    /** Выполняет команду. */
    void execute(String[] args);
}
