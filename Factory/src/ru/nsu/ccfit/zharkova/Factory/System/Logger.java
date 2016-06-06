package ru.nsu.ccfit.zharkova.Factory.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yana on 12.05.15.
 */
public class Logger {

    private static Map<String,Logger> classLoggers = new HashMap<String, Logger>();
    private String className;
    private static boolean isCreate = false;
    private static File logDir = new File("./logging");
    private static String debugFile = "./logging/debug.log";
    private static String errorFile = "./logging/error.log";
    private static String infoFile = "./logging/info.log";
    private static final String LVL_INFO = "INFO";
    private static final String LVL_DEBUG = "DEBUG";
    private static final String LVL_ERROR = "ERROR";
    private static PrintWriter out_i;
    private static PrintWriter out_d;
    private static PrintWriter out_e;

    private Logger(){
        try {
            if(!isCreate) {
                logDir.mkdirs();
                isCreate = true;
            }
            out_i = new PrintWriter(new File(infoFile));
            out_d = new PrintWriter(new File(debugFile));
            out_e = new PrintWriter(new File(errorFile));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Logger getLogger(String className){
        if(!classLoggers.containsKey(className)){
            classLoggers.put(className, new Logger());
            classLoggers.get(className).className = className;
        }
        return classLoggers.get(className);
    }

    public void info(String msg){
        System.out.println(generateDate() + " " + LVL_INFO + " " + className + " " + msg);
        out_i.println(generateDate()+ " " + LVL_INFO+ " " + className + " " + msg);
        out_i.flush();
    }

    public void error(String msg){
        out_e.println(generateDate()+ " " + LVL_ERROR+ " " + className + " " + msg);
        out_e.flush();
    }

    public void debug(String msg){
        out_d.println(generateDate()+ " " + LVL_DEBUG+ " " + className + " " + msg);
        out_d.flush();
    }

    private String generateDate() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void closeLogFiles(){
        out_i.close();
        out_d.close();
        out_e.close();
    }
}
