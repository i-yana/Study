package ru.nsu.ccfit.zharkova.LoC.Main;
/**
 * Created by Yana on 16.03.15.
 */
public class PathOfDirectoryException extends Exception{
    private final String incorrectPath;

    public PathOfDirectoryException(String path) {
        this.incorrectPath = path;
    }

    @Override
    public String toString() {
        return "Wrong path to directory: " + incorrectPath;
    }
}
