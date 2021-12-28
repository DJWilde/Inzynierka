package com.djwilde.inzynierka.helpers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InternetConnectorCommand {
    private static final Map<String, InternetCommand> COMMAND_MAP;

    static {
        COMMAND_MAP = Map.of("Google Drive", GoogleDriveConnector::new, "Dropbox", DropboxConnector::new);
    }

    public InternetConnector createConnector(String serviceName) {
        InternetCommand internetCommand = COMMAND_MAP.get(serviceName);

        if (internetCommand == null) {
            throw new IllegalArgumentException("Invalid service name");
        }

        return internetCommand.createInternetConnector();
    }
}
