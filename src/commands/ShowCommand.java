package commands;

import managers.CollectionManager;

/**
 * Команда {@code show}: выводит элементы коллекции.
 */
public class ShowCommand  implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Shows the the all elements in the collection";
    }

    @Override
    /** Выполняет показ коллекции. */
    public void execute(String[] args) {
        collectionManager.show();
    }
}
