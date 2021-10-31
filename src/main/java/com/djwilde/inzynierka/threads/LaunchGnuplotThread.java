package com.djwilde.inzynierka.threads;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.terminal.FileTerminal;

public class LaunchGnuplotThread implements Runnable {
    private String gnuplotCommand;

    public LaunchGnuplotThread(String gnuplotCommand) {
        this.gnuplotCommand = gnuplotCommand;
    }

    @Override
    public void run() {
//        FileTerminal terminal = new FileTerminal("png", "test.png");
//        String[] gnuplotCommandsArray = gnuplotCommand.split(";");
//        JavaPlot javaPlot = new JavaPlot();
//        javaPlot.setTerminal(terminal);
//        for (String command : gnuplotCommandsArray) {
//            javaPlot.addPlot(command);
//        }
//        javaPlot.setKey(JavaPlot.Key.OFF);
//        javaPlot.setTitle("Plot");
//        javaPlot.plot();
        FileTerminal terminal = new FileTerminal("png", "test.png");
        JavaPlot javaPlot = new JavaPlot();
        javaPlot.setTerminal(terminal);
        javaPlot.addPlot("sin(x)");
        javaPlot.setKey(JavaPlot.Key.OFF);
        javaPlot.setTitle("Plot");
        javaPlot.plot();
    }
}
