package com.djwilde.inzynierka.config;

import java.util.*;

public class GnuplotConstants {
    public static final String[] GNUPLOT_KEYWORDS = new String[] {
            "break", "cd", "call", "clear", "continue", "do", "evaluate", "exit", "fit", "help", "history", "if", "for",
            "import", "load", "pause", "plot", "print", "printerr", "pwd", "quit", "raise", "refresh", "replot", "reread",
            "reset", "save", "set", "shell", "splot", "stats", "system", "test", "toggle", "undefine", "unset", "update",
            "while"
    };

    public static Map<String, List<String>> GNUPLOT_COMMANDS_AND_ARGUMENTS;
    public static final Map<String, List<String>> GNUPLOT_COMMANDS_AND_KEYWORDS = new HashMap<>();
    public static final Map<String, List<String>> GNUPLOT_TEMPLATES = new HashMap<>();

    static {
        Map<String, List<String>> commandsAndArguments = new LinkedHashMap<>();
        commandsAndArguments.put("break", null);
        commandsAndArguments.put("cd", List.of("directoryName"));
        commandsAndArguments.put("call", List.of("inputFile", "param1","param2","param3","param4","param5","param6","param7",
                "param8","param9"));
        commandsAndArguments.put("clear", null);
        commandsAndArguments.put("continue", null);
        commandsAndArguments.put("do", null);
        commandsAndArguments.put("eval", List.of("expression"));
        commandsAndArguments.put("exit", null);
        commandsAndArguments.put("exit message", List.of("message"));
        commandsAndArguments.put("exit status", List.of("errorCode"));
        commandsAndArguments.put("for", List.of("intvar", "stringvar"));
        commandsAndArguments.put("fit", List.of("ranges", "expression", "datafile", "datafileModifiers", "unitweights", "parameterFile", "var1"));
        commandsAndArguments.put("help", List.of("topic"));
        commandsAndArguments.put("history", List.of("lastNEntries", "file", "expression"));
        commandsAndArguments.put("if", List.of("condition"));
        commandsAndArguments.put("import", List.of("func", "sharedObj", "sharedSymbol"));
        commandsAndArguments.put("load", List.of("inputFile"));
        commandsAndArguments.put("lower", List.of("plotWindowId"));
        commandsAndArguments.put("pause", List.of("time", "string"));
        commandsAndArguments.put("pause mouse", List.of("endCondition", "string"));
        commandsAndArguments.put("pause mouse close", null);
        commandsAndArguments.put("plot", List.of("ranges", "plotElement"));
        commandsAndArguments.put("print", List.of("expression"));
        commandsAndArguments.put("printerr", List.of("expression"));
        commandsAndArguments.put("pwd", null);
        commandsAndArguments.put("quit", null);
        commandsAndArguments.put("raise", List.of("plotWindowId"));
        commandsAndArguments.put("refresh", null);
        commandsAndArguments.put("replot", null);
        commandsAndArguments.put("reread", null);
        commandsAndArguments.put("reset", null);
        commandsAndArguments.put("reset session", null);
        commandsAndArguments.put("reset errors", null);
        commandsAndArguments.put("reset bind", null);
        commandsAndArguments.put("save", List.of("filename"));
        commandsAndArguments.put("save functions", List.of("filename"));
        commandsAndArguments.put("save variables", List.of("filename"));
        commandsAndArguments.put("save terminal", List.of("filename"));
        commandsAndArguments.put("save set", List.of("filename"));
        commandsAndArguments.put("save fit", List.of("filename"));
        commandsAndArguments.put("set angles", null);
        commandsAndArguments.put("set arrow", List.of("position1", "position2", "coord", "ang", "arrow_style", "headlength",
                "headangle", "linestyle", "linetype", "linewidth", "colorspec", "dashtype", "type"));
        commandsAndArguments.put("set autoscale", List.of("axes"));
        commandsAndArguments.put("set autoscale noextend", null);
        commandsAndArguments.put("set autoscale keepfix", null);
        commandsAndArguments.put("set bmargin", List.of("margin"));
        commandsAndArguments.put("set lmargin", List.of("margin"));
        commandsAndArguments.put("set rmargin", List.of("margin"));
        commandsAndArguments.put("set tmargin", List.of("margin"));
        commandsAndArguments.put("set margins", List.of("left", "right", "bottom", "top"));
        commandsAndArguments.put("set border", List.of("integer", "lineStyle", "lineType", "lineWidth", "colorSpec", "dashType"));
        commandsAndArguments.put("set boxwidth", List.of("width"));
        commandsAndArguments.put("set boxdepth", List.of("depth"));
        commandsAndArguments.put("set colorsequence", null);
        commandsAndArguments.put("set clabel", List.of("format"));
        commandsAndArguments.put("set clip", null);
        commandsAndArguments.put("set cntrlabel", List.of("format", "font", "int"));
        commandsAndArguments.put("set cntrlabel onecolor", null);
        commandsAndArguments.put("set cntrparam", List.of("n", "z1", "z2", "z3", "N", "end"));
        commandsAndArguments.put("set colorbox", List.of("x", "y", "lineStyle"));
        commandsAndArguments.put("set contour", null);
        commandsAndArguments.put("set dashtype", List.of("N", "pattern", "s1", "e1", "s2", "e2", "s3", "e3", "s4", "e4"));
        commandsAndArguments.put("set datafile", List.of());
        commandsAndArguments.put("set decimalsign", List.of("value", "locale"));
        commandsAndArguments.put("set dgrid3d", List.of("rows", "cols", "norm", "dx", "dy"));
        commandsAndArguments.put("set dummy", List.of("dummyVar1", "dummyVar2"));
        commandsAndArguments.put("set encoding", List.of("value"));
        commandsAndArguments.put("set encoding locale", null);
        commandsAndArguments.put("set errorbars", List.of("size"));
        commandsAndArguments.put("set fit", List.of("filename", "value", "epsilon", "epsilonAbs", "command"));
        commandsAndArguments.put("set fontpath", List.of("directory"));
        commandsAndArguments.put("set format", List.of("axes", "formatString"));
        commandsAndArguments.put("set grid", List.of("angle", "linePropertiesMajor", "linePropertiesMinor"));
        commandsAndArguments.put("set hidden3d", List.of("offset", "level", "bitpattern"));
        commandsAndArguments.put("set historysize", List.of("N"));
        commandsAndArguments.put("set history", List.of("N"));
        commandsAndArguments.put("set isosamples", List.of("iso1", "iso2"));
        commandsAndArguments.put("set isosurface", List.of("n"));
        commandsAndArguments.put("set jitter", List.of("yposition", "factor", "limit"));
        commandsAndArguments.put("set key", List.of("position", "sampleLength", "lineSpacing", "widthIncrement",
                "heightIncrement", "text", "face", "size", "colorspec", "style", "type", "width", "maxNoOfColumns", "maxNoOfRows"));
        commandsAndArguments.put("set label", List.of("tag", "labelText", "position", "degrees", "name", "size", "colorspec", "pointstyle", "offset", "boxstyle"));
        commandsAndArguments.put("set linetype", List.of());
        commandsAndArguments.put("set link", List.of("expression1", "expression2"));
        commandsAndArguments.put("set loadpath", List.of("pathlist1", "pathlist2"));
        commandsAndArguments.put("set locale", List.of("locale"));
        commandsAndArguments.put("set logscale", List.of("axes", "base"));
        commandsAndArguments.put("set mapping", null);
        commandsAndArguments.put("set micro", List.of("character"));
        commandsAndArguments.put("set monochrome", List.of("linetypeProperties"));
        commandsAndArguments.put("set mouse", List.of("ms", "xmultiplier", "ymultiplier", "string", "int", "f", "labeloptions"));
        commandsAndArguments.put("set multiplot", List.of("pageTitle", "fontspec", "rows", "cols", "xscale", "yscale",
                "xoff", "yoff", "left", "right", "bottom", "top", "xspacing", "yspacing"));
        commandsAndArguments.put("set mxtics", List.of("freq"));
        commandsAndArguments.put("set nonlinear", List.of("axis"));
        commandsAndArguments.put("set object", List.of("index", "objectType", "objectProperties", "colorspec", "fillStyle", "width", "dashtype"));
        commandsAndArguments.put("set offsets", List.of("left", "right", "top", "bottom"));
        commandsAndArguments.put("set origin", List.of("xOrigin", "yOrigin"));
        commandsAndArguments.put("set output", List.of("filename"));
        commandsAndArguments.put("set overflow", null);
        commandsAndArguments.put("set palette", List.of("gamma", "rgb", "gray1", "color1", "grayN", "colorN", "filename", "R", "G", "B", "startval", "cyclesval", "saturationval", "maxcolors"));
        commandsAndArguments.put("set parametric", null);
        commandsAndArguments.put("set paxis", List.of("axisno", "rangeOptions", "ticOptions", "labelOptions", "radialOffset"));
        commandsAndArguments.put("set pixmap", List.of("index", "filename", "position", "w", "h"));
        commandsAndArguments.put("set pm3d", List.of("position", "steps", "base", "z", "fraction1", "fraction2", "fraction3", "linestyleOptions"));
        commandsAndArguments.put("set pointsize", List.of("multiplier"));
        commandsAndArguments.put("set polar", null);
        commandsAndArguments.put("set print", List.of("filename", "shellCommand", "append"));
        commandsAndArguments.put("set psdir", List.of("directory"));
        commandsAndArguments.put("set rgbmax", List.of("value"));
        commandsAndArguments.put("set samples", List.of("samples1", "samples2"));
        commandsAndArguments.put("set size", List.of("r", "xscale", "yscale"));
        // Uzupełnić set style o dodatkowe dane!
        commandsAndArguments.put("set style function", List.of("style"));
        commandsAndArguments.put("set style data", List.of("style"));
        commandsAndArguments.put("set style arrow", List.of("n", "arrowstyle"));
        commandsAndArguments.put("set style boxplot", List.of("styleOptions"));
        commandsAndArguments.put("set style circle radius", List.of("size"));
        commandsAndArguments.put("set style ellipse", List.of("size"));
        commandsAndArguments.put("set style fill", List.of("fillstyle"));
        commandsAndArguments.put("set style histogram", List.of("styleOptions"));
        commandsAndArguments.put("set style line", List.of("n", "lineStyle"));
        commandsAndArguments.put("set style rectangle", List.of("objectOptions", "linestyle", "fillstyle"));
        commandsAndArguments.put("set style textbox", List.of("n"));
        commandsAndArguments.put("set surface", null);
        commandsAndArguments.put("set table", List.of("outfile", "char"));
        commandsAndArguments.put("set terminal", List.of("terminalType"));
        commandsAndArguments.put("set termoption", List.of("fontname", "fontsize", "scale", "lw"));
        commandsAndArguments.put("set theta", null);
        commandsAndArguments.put("set tics", List.of("ang", "offset", "formatstring", "size", "colorspec", "minor"));
        commandsAndArguments.put("set timestamp", List.of("format", "xoff", "yoff", "fontspec", "colorspec"));
        commandsAndArguments.put("set timefmt", List.of("formatString"));
        commandsAndArguments.put("set title", List.of("titleText", "offset", "font", "size", "colorspec"));
        commandsAndArguments.put("set vgrid", List.of("size"));
        commandsAndArguments.put("set view", List.of("rotx", "rotz", "scale", "scalez", "scale", "angle"));
        commandsAndArguments.put("set vxrange", List.of("vmin", "vmax"));
        commandsAndArguments.put("set vyrange", List.of("vmin", "vmax"));
        commandsAndArguments.put("set vzrange", List.of("vmin", "vmax"));
        commandsAndArguments.put("set wall", List.of("fillstyle", "fillcolor"));
        commandsAndArguments.put("set walls", null);
        commandsAndArguments.put("set xdata time", null);
        commandsAndArguments.put("set ydata time", null);
        commandsAndArguments.put("set zdata time", null);
        commandsAndArguments.put("set x2data time", null);
        commandsAndArguments.put("set y2data time", null);
        commandsAndArguments.put("set z2data time", null);
        commandsAndArguments.put("set xlabel", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set x2label", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set ylabel", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set y2label", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set zlabel", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set cblabel", List.of("label", "offset", "font", "size", "colorspec", "degrees"));
        commandsAndArguments.put("set xmtics", null);
        commandsAndArguments.put("set x2mtics", null);
        commandsAndArguments.put("set ymtics", null);
        commandsAndArguments.put("set y2mtics", null);
        commandsAndArguments.put("set zmtics", null);
        commandsAndArguments.put("set cbmtics", null);
        commandsAndArguments.put("set xrange", List.of("min", "max"));
        commandsAndArguments.put("set yrange", List.of("min", "max"));
        commandsAndArguments.put("set zrange", List.of("min", "max"));
        commandsAndArguments.put("set x2range", List.of("min", "max"));
        commandsAndArguments.put("set y2range", List.of("min", "max"));
        commandsAndArguments.put("set cbrange", List.of("min", "max"));
        commandsAndArguments.put("set rrrange", List.of("min", "max"));
        commandsAndArguments.put("set trange", List.of("min", "max"));
        commandsAndArguments.put("set urange", List.of("min", "max"));
        commandsAndArguments.put("set vrange", List.of("min", "max"));
        commandsAndArguments.put("xtics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("ytics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("ztics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("x2tics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("y2tics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("cbtics", List.of("major", "minor", "ang", "offset", "incr", "start", "end", "label", "level",
                "pos", "formatstring", "fontsize", "colorspec"));
        commandsAndArguments.put("set zero", List.of("expression"));
        commandsAndArguments.put("set xzeroaxis", List.of("linestyle", "linetype", "linewidth", "colorspec", "dashtype"));
        commandsAndArguments.put("set x2zeroaxis", List.of("linestyle", "linetype", "linewidth", "colorspec", "dashtype"));
        commandsAndArguments.put("set yzeroaxis", List.of("linestyle", "linetype", "linewidth", "colorspec", "dashtype"));
        commandsAndArguments.put("set y2zeroaxis", List.of("linestyle", "linetype", "linewidth", "colorspec", "dashtype"));
        commandsAndArguments.put("set zzeroaxis", List.of("linestyle", "linetype", "linewidth", "colorspec", "dashtype"));
        commandsAndArguments.put("shell", null);
        commandsAndArguments.put("splot", List.of("ranges", "iteration", "function", "filename", "datablockname", "datafileModifiers", "voxelgridname", "titlespec", "style", "definitions", "function"));
        commandsAndArguments.put("stats", List.of("ranges", "filename", "N", "M", "prefix"));
        commandsAndArguments.put("system", List.of("commandString"));
        commandsAndArguments.put("test", null);
        commandsAndArguments.put("toggle", List.of("plotno", "plottitle"));
        commandsAndArguments.put("undefine", List.of("variables"));
        commandsAndArguments.put("unset", List.of("options"));
        commandsAndArguments.put("update", null);
        commandsAndArguments.put("vclear", List.of("gridname"));
        commandsAndArguments.put("vfill", List.of("file", "x", "y", "z", "radius", "expression"));
        commandsAndArguments.put("while", List.of("expression"));
        commandsAndArguments.put("show bind", null);
        commandsAndArguments.put("show variables", null);
        commandsAndArguments.put("show version", null);

        GNUPLOT_COMMANDS_AND_ARGUMENTS = Collections.unmodifiableMap(commandsAndArguments);
    }
}
