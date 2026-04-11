package commands;

import managers.CollectionManager;
import managers.InputManager;
import models.Address;
import models.Coordinates;
import models.Organization;
import models.OrganizationType;

/**
 * Команда {@code update}: обновляет организацию по id.
 */
public class UpdateCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Updates an existing Organization by its id";
    }

    @Override
    /** Выполняет обновление. */
    public void execute(String[] args) {
        try {

            Long id = Long.valueOf(InputManager.readInt("Enter the id of the organization to update: "));

            String name = InputManager.readLine("Enter new name: ");

            double x = InputManager.readDouble("Enter new X coordinate (double): ");
            Integer y = InputManager.readInt("Enter new Y coordinate (integer): ");
            Coordinates coordinates = new Coordinates(x, y);
            int turnover = InputManager.readInt("Enter new annual turnover (int > 0): ");
            OrganizationType type = null;
            while (true) {
                String typeStr = InputManager.readLine("Enter Organization Type (COMMERCIAL, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY, OPEN_JOINT_STOCK_COMPANY): "
                );
                if (typeStr.isEmpty()) break;
                try {
                    type = OrganizationType.valueOf(typeStr);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid OrganizationType. Try again.");
                }
            }

            String zip = InputManager.readLine("Enter new zip code (max 19 chars, can be empty): ");
            Address address = new Address(zip.isEmpty() ? null : zip);

            Organization newOrg = new Organization(name, coordinates, turnover, type, address);
            collectionManager.update(id, newOrg);

            System.out.println("Organization updated successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}