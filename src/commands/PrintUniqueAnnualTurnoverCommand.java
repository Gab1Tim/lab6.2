package commands;

import managers.CollectionManager;

/**
 * Команда {@code print_unique_annual_turnover}: печатает уникальные annualTurnover.
 */
public class PrintUniqueAnnualTurnoverCommand implements Command {

    private CollectionManager collectionManager;

    /** Создаёт команду. */
    public PrintUniqueAnnualTurnoverCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "print_unique_annual_turnover";
    }

    @Override
    public String getDescription() {
        return "Prints all unique annualTurnover values of the organizations in the collection";
    }

    @Override
    /** Выполняет вывод. */
    public void execute(String[] args) {
        collectionManager.printUniqueAnnualTurnover();
    }
}