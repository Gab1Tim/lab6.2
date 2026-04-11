package commands;

import managers.CollectionManager;
import models.Organization;

/**
 * Команда {@code min_by_name}: выводит организацию с минимальным именем.
 */
public class MinByNameCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public MinByNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "min_by_name";
    }

    @Override
    public String getDescription() {
        return "Prints the organization with the smallest name (alphabetically)";
    }

    @Override
    /** Выполняет поиск и вывод. */
    public void execute(String[] args) {
        Organization minOrg = collectionManager.getMinByName();
        if (minOrg != null) {
            System.out.println("Organization with the smallest name:");
            System.out.println(minOrg);
        } else {
            System.out.println("Collection is empty!");
        }
    }
}