package commands;

import managers.CollectionManager;

/**
 * Команда {@code info}: печатает информацию о коллекции.
 */
public class InfoCommand implements Command {

    private CollectionManager  collectionManager;

    /** Создаёт команду. */
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;

    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Shows the collection";
    }

    @Override
    /** Выполняет вывод информации. */
    public void execute(String[] args){
        collectionManager.info();
    }

}
