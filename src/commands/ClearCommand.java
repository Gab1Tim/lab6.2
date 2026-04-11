package commands;

import managers.CollectionManager;

/**
 * Команда {@code clear}: очищает коллекцию.
 */
public class ClearCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears the entire collection";
    }

    @Override
    /** Выполняет очистку. */
    public void execute(String[] args) {
        collectionManager.clear();
        System.out.println("Collection cleared successfully!");
    }
}