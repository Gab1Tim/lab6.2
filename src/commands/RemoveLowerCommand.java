package commands;

import managers.CollectionManager;
import managers.InputManager;
import models.Organization;

/**
 * Команда {@code remove_lower}: удаляет элементы меньше заданного.
 */
public class RemoveLowerCommand implements Command {

    private CollectionManager collectionManager;
    private InputManager inputManager;

    /** Создаёт команду. */
    public RemoveLowerCommand(CollectionManager collectionManager, InputManager inputManager) {
        this.collectionManager = collectionManager;
        this.inputManager = inputManager;
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "Removes all elements lower than the given element (controls by annual turnover)";
    }

    @Override
    /** Выполняет удаление. */
    public void execute(String[] args) {
        Organization reference = inputManager.readOrganization();
        int removed = collectionManager.removeLower(reference);
        System.out.println(removed + " elements removed.");
    }
}