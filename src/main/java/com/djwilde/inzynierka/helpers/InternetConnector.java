package com.djwilde.inzynierka.helpers;

import java.io.IOException;
import java.security.GeneralSecurityException;

public abstract class InternetConnector {
    protected static final String APPLICATION_NAME = "JPlotter";

    public abstract void initialize();
}
