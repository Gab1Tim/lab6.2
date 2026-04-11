package commands;

import managers.CollectionManager;
import managers.InputManager;

/**
 * Команда {@code remove_greater_key}: удаляет элементы с ключом больше заданного.
 */
public class RemoveGreaterKeyCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public RemoveGreaterKeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_greater_key";
    }

    @Override
    public String getDescription() {
        return "Removes all organizations with key greater than the given key";
    }

    @Override
    /** Выполняет удаление. */
    public void execute(String[] args) {
        try {
            Integer key = InputManager.readInt("Enter the key to compare: ");
            collectionManager.removeGreaterKey(key);
            System.out.println("All organizations with key greater than " + key + " have been removed!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}