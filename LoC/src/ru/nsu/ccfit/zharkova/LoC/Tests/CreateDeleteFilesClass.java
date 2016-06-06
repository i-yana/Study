package ru.nsu.ccfit.zharkova.LoC.Tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Yana on 08.07.15.
 */
public class CreateDeleteFilesClass {

    public static void newDirectory(String dirName) throws IOException {
        File directory = new File(dirName);
        if(directory.exists()){
            deleteDirectory(dirName);
        }
        directory.mkdir();
    }

    public static void deleteDirectory(String filename)
    {
        File file = new File(filename);
        if(!file.exists())
            return;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                deleteDirectory(f.getAbsolutePath());
            file.delete();
        }
        else
        {
            file.delete();
        }
    }

    public static void newFile(String[] lines, String pathToFile) throws IOException {
        File file = new File(pathToFile);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        PrintStream ps = new PrintStream(file);
        for (String str : lines) {
            ps.println(str);
        }
        ps.close();
    }


}