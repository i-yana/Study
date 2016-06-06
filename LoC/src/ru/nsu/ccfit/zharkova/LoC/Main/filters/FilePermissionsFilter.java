package ru.nsu.ccfit.zharkova.LoC.Main.filters;
import ru.nsu.ccfit.zharkova.LoC.Main.WrongConfigFormat;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yana on 26.03.15.
 */
public class FilePermissionsFilter implements Filter {

    final private char READ_IDENTIFIER = 'r';
    final private char WRITE_IDENTIFIER = 'w';
    final private char EXECUTE_IDENTIFIER = 'x';

    String nameFilter;
    private int numOfLines = 0;
    private int numOfFiles = 0;
    boolean read = false;
    boolean write = false;
    boolean execute = false;

    public FilePermissionsFilter(String rights) throws WrongConfigFormat {
        nameFilter = rights;
        Pattern p = Pattern.compile("[r-][w-][x-]");
        Matcher m = p.matcher(rights);
        if(m.matches()) {
            if (rights.charAt(0) == READ_IDENTIFIER) {
                read = true;
            }
            if (rights.charAt(1) == WRITE_IDENTIFIER) {
                write = true;
            }
            if (rights.charAt(2) == EXECUTE_IDENTIFIER) {
                execute = true;
            }
        }
        else{
            throw new WrongConfigFormat(rights);
        }
    }

    @Override
    public boolean isGoodFile(File path) {
        return path.canRead() == read && path.canWrite() == write && path.canExecute() == execute;
    }

    @Override
    public String getFilterName() {
        return nameFilter;
    }

    @Override
    public long getNumOfLines() {
        return numOfLines;
    }

    @Override
    public long getNumOfFiles() {
        return numOfFiles;
    }

    @Override
    public void addLines(long num) {
        numOfLines+= num;
    }

    @Override
    public void addFiles() {
        numOfFiles++;
    }
}
