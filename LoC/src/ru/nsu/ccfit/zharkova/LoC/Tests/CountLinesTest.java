package ru.nsu.ccfit.zharkova.LoC.Tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nsu.ccfit.zharkova.LoC.Main.Counter;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yana on 08.07.15.
 */
public class CountLinesTest {

    private static final String TEST_DIR = "./CountLinesTest";
    private static final String TEST_FILE = "test";
    private static final char SLASH = '/';
    private static final String PATH = TEST_DIR+SLASH+TEST_FILE;

    @BeforeClass
    public static void createDirectory() throws IOException {
        CreateDeleteFilesClass.newDirectory(TEST_DIR);
    }

    @Test
    public void CountLinesInEmptyFileTest() throws IOException {
        String[] nullLine = new String[0];
        CreateDeleteFilesClass.newFile(nullLine, PATH);
        File f = new File(PATH);
        long count = Counter.countLines(f);
        Assert.assertEquals(count, 0);
    }

    @Test
    public void CountLinesInFileTest() throws IOException {
        String[] nullLine = new String[10000];
        CreateDeleteFilesClass.newFile(nullLine, PATH);
        File f = new File(PATH);
        long count = Counter.countLines(f);
        Assert.assertEquals(count,10000);
    }

    @AfterClass
    public static void removeAll(){
        CreateDeleteFilesClass.deleteDirectory(TEST_DIR);
    }
}
