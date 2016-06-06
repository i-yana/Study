package ru.nsu.ccfit.zharkova.LoC.Main.filters;

import ru.nsu.ccfit.zharkova.LoC.Main.WrongConfigFormat;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Yana on 26.03.15.
 */
public class MaskFilter implements Filter {

    private final Pattern pattern;
    private int numOfLines = 0;
    private int numOfFiles = 0;

    public MaskFilter(String regexp) throws WrongConfigFormat {
        try {
            pattern = Pattern.compile(regexp);
        }
        catch (PatternSyntaxException pse){
            throw new WrongConfigFormat(regexp + " (" + pse.getDescription()+")");
        }
    }

    @Override
    public boolean isGoodFile(File path) {
        Matcher m = pattern.matcher(path.getName());
        return m.matches();
    }

    @Override
    public String getFilterName() {
        return pattern.toString();
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