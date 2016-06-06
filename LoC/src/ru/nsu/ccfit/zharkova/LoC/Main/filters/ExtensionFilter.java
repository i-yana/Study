package ru.nsu.ccfit.zharkova.LoC.Main.filters;

import java.io.File;

/**
 * Created by Yana on 03.03.15.
 */
public class ExtensionFilter implements Filter {

    private final char IDENTIFIER = '.';
    private final String extension;
    private int numOfLines = 0;
    private int numOfFiles = 0;

    public ExtensionFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean isGoodFile(File path) {
        int index = path.toString().lastIndexOf(IDENTIFIER);
        return extension.equals(path.toString().substring(index + 1));
    }

    @Override
    public String getFilterName() {
        return extension;
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
