package commands;

import managers.CollectionManager;
import managers.InputManager;

/**
 * Команда {@code remove_key}: удаляет элемент по ключу.
 */
public class RemoveKeyCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public RemoveKeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getDescription() {
        return "Removes an element by key";
    }

    @Override
    /** Выполняет удаление по ключу. */
    public void execute(String[] args) {
        try {
            Integer key = InputManager.readInt("Enter key to remove: ");
            collectionManager.remove(key);
            System.out.println("Element with key " + key + " removed successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}