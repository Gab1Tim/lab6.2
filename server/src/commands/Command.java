package server.commands;

import common.network.Request;
import common.network.Response;

public interface Command {
    String getName();
    String getDescription();
    Response execute(Request request);
}