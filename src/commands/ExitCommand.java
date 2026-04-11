package commands;

/**
 * Команда {@code exit}: завершает программу.
 */
public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }
    @Override
    public String getDescription() {
        return "Exit the program";
    }

    @Override
    /** Выполняет выход. */
    public void execute(String[] commands) {
        System.out.println("Exiting program...");
        System.exit(0);
    }
}
