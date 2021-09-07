package com.djwilde.inzynierka.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class ScriptHelper {
    public static void saveScript(File file, String scriptString) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(scriptString);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void executeScriptFromAnotherProcess(String filename) {
        String command = "gnuplot -e \"load '" + filename + "'\"";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
