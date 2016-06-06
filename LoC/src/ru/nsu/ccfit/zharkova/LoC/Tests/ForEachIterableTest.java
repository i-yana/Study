package ru.nsu.ccfit.zharkova.LoC.Tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nsu.ccfit.zharkova.LoC.Main.ForEachIterate;
import ru.nsu.ccfit.zharkova.LoC.Main.PathOfDirectoryException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yana on 19.03.15.
 */
public class ForEachIterableTest {

    private static final String TEST_DIR = "./ForEachIterableTest";
    private static final String TEST_FILE = "test";
    private static final String SECOND_DIR = "secondDirectory/";
    private static final char SLASH = '/';
    private static final String PATH = TEST_DIR+SLASH+TEST_FILE;
    private static final String DIR_PREFIX = "dir";
    private  static final int TEST_FILE_COUNT = 10;

    @BeforeClass
    public static void createDirectory() throws IOException {
        CreateDeleteFilesClass.newDirectory(TEST_DIR);
    }

    @AfterClass
    public static void deleteDirectory() throws IOException {
        CreateDeleteFilesClass.deleteDirectory(TEST_DIR);
    }

    @Test(expected = PathOfDirectoryException.class)
    public void wrongPathToDirectory() throws IOException, PathOfDirectoryException {
        CreateDeleteFilesClass.newFile(new String[0], PATH);
        ForEachIterate filesIterator = new ForEachIterate(PATH);
    }

    @Test
    public void hasNextShouldReturnFalseIfDirectoryIsEmpty() throws PathOfDirectoryException, IOException {
        String pathToFile = TEST_DIR + SLASH + DIR_PREFIX;
        CreateDeleteFilesClass.newDirectory(pathToFile);
        CreateDeleteFilesClass.newDirectory(pathToFile + SLASH + SECOND_DIR);
        ForEachIterate filesIterator = new ForEachIterate(pathToFile);
        Assert.assertFalse(filesIterator.iterator().hasNext());
    }


    @Test
    public void findFileInDirectory() throws IOException, PathOfDirectoryException {
        String pathToFile = TEST_DIR + SLASH;
        CreateDeleteFilesClass.newDirectory(pathToFile);
        for(int i=0; i<TEST_FILE_COUNT;i++) {
            CreateDeleteFilesClass.newFile(new String[0], pathToFile + i);
        }
        ForEachIterate fileIterator = new ForEachIterate(TEST_DIR+SLASH);
        int count = 0;
        for(File path : fileIterator) {
            ++count;
        }
        Assert.assertEquals(TEST_FILE_COUNT, count);
    }

    @Test
    public void findFilesInSecondDirectories() throws IOException, PathOfDirectoryException {
        String pathToFile = TEST_DIR + SLASH;
        CreateDeleteFilesClass.newDirectory(pathToFile);
        for(int i=0; i<TEST_FILE_COUNT;i++) {
            CreateDeleteFilesClass.newFile(new String[0], pathToFile + i);
        }
        CreateDeleteFilesClass.newDirectory(pathToFile + SECOND_DIR);
        for(int i=0; i<TEST_FILE_COUNT;i++) {
            CreateDeleteFilesClass.newFile(new String[0], pathToFile + SECOND_DIR + i);
        }
        ForEachIterate fileIterator = new ForEachIterate(TEST_DIR+SLASH);
        int count = 0;
        for(File path : fileIterator) {
            ++count;
        }
        Assert.assertEquals(TEST_FILE_COUNT + TEST_FILE_COUNT, count);
    }
}

