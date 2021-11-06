package com.djwilde.inzynierka.helpers;

import com.panayotis.gnuplot.JavaPlot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

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
        String command = "gnuplot -persist -e \"load '" + filename + "'\"";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            process.waitFor(100, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
//        JavaPlot plot = new JavaPlot();
//        plot.addPlot("sin(x)");
//        plot.setPersist(true);
//        plot.plot();
    }
}
