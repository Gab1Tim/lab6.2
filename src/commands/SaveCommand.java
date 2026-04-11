package commands;

import managers.CollectionManager;

/**
 * Команда {@code save}: сохраняет коллекцию в файл.
 */
public class SaveCommand implements Command {

    private final CollectionManager collectionManager;

    /** Создаёт команду. */
    public SaveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "save : save the collection to file";
    }

    @Override
    /** Выполняет сохранение. */
    public void execute(String[] args) {
        collectionManager.save();
        System.out.println("Collection successfully saved.");
    }
}