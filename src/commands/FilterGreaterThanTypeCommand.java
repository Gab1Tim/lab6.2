package commands;

import managers.CollectionManager;
import managers.InputManager;
import models.OrganizationType;
import models.Organization;

/**
 * Команда {@code filter_greater_than_type}: печатает организации с типом больше заданного.
 */
public class FilterGreaterThanTypeCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public FilterGreaterThanTypeCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_greater_than_type";
    }

    @Override
    public String getDescription() {
        return "Prints organizations whose type is greater than the given type";
    }

    @Override
    /** Выполняет фильтрацию и вывод. */
    public void execute(String[] args) {
        try {
            String typeStr = InputManager.readLine("Enter Organization Type (COMMERCIAL, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY, OPEN_JOINT_STOCK_COMPANY): ");

            OrganizationType type = OrganizationType.valueOf(typeStr);

            collectionManager.filterGreaterThanType(type);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid OrganizationType. Try again.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}