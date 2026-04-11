package commands;

import managers.CollectionManager;
import managers.InputManager;
import models.Organization;

/**
 * Команда {@code insert}: добавляет новую организацию по ключу.
 */
public class InsertCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public InsertCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "insert : Inserts a new Organization with a unique key";
    }

    @Override
    /** Выполняет добавление организации. */
    public void execute(String[] args) {
        Integer key;

        if (InputManager.isScriptMode()) {
            key = InputManager.readInt("Enter key (integer): ");
            if (collectionManager.containsKey(key)) {
                throw new InputManager.ScriptInputException(
                        "Key " + key + " already exists in the collection. Please check your script."
                );
            }
        } else {
            while (true) {
                key = InputManager.readInt("Enter key (integer): ");
                if (!collectionManager.containsKey(key)) break;
                System.out.println("Key already exists. Enter a different key.");
            }
        }

        Organization org = InputManager.readOrganization();
        collectionManager.insert(key, org);
        System.out.println("Organization added successfully!");
    }
}