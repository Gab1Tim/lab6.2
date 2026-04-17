package server.commands;

import common.models.Organization;
import common.network.Request;
import server.managers.CollectionManager;

public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;

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
    public CommandResult execute(Request request) {
        try {
            Long id = request.getId();
            Organization organization = request.getOrganization();

            if (id == null) {
                return new CommandResult(false, "Id is required.");
            }
            if (organization == null) {
                return new CommandResult(false, "Organization is required.");
            }

            collectionManager.update(id, organization);
            return new CommandResult(true, "Organization updated successfully.");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}