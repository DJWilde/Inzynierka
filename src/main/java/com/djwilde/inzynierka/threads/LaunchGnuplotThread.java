package com.djwilde.inzynierka.threads;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.terminal.FileTerminal;
import com.panayotis.gnuplot.terminal.ImageTerminal;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LaunchGnuplotThread implements Runnable {
    private String gnuplotCommand;

    public LaunchGnuplotThread(String gnuplotCommand) {
        this.gnuplotCommand = gnuplotCommand;
    }

    @Override
    public void run() {
        FileTerminal terminal = new FileTerminal("png", "test.png");
        String gnuplotCommandsStr = gnuplotCommand.replaceAll("\\s+", "");
        String[] gnuplotCommandsArray = gnuplotCommandsStr.split(";");
        JavaPlot javaPlot = new JavaPlot();
        javaPlot.setTerminal(terminal);
        for (String command : gnuplotCommandsArray) {
            javaPlot.addPlot(command);
        }
        javaPlot.setKey(JavaPlot.Key.OFF);
        javaPlot.setTitle("Plot");
        javaPlot.plot();
    }
}
