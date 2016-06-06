package ru.nsu.ccfit.zharkova.LoC.Tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nsu.ccfit.zharkova.LoC.Main.*;
import ru.nsu.ccfit.zharkova.LoC.Main.filters.*;
import ru.nsu.ccfit.zharkova.LoC.Main.system.ParseConfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yana on 18.03.15.
 */
public class ParseConfigTest {

    private static final String TEST_DIR = "./ParseConfigTest";
    private static final String TEST_FILE = "test";
    private static final char SLASH = '/';
    private static final String PATH = TEST_DIR+SLASH+TEST_FILE;

    @BeforeClass
    public static void createDirectoryWithConfig() throws IOException {
        CreateDeleteFilesClass.newDirectory(TEST_DIR);
    }

    @Test
    public void emptyConfigTest() throws IOException, WrongConfigFormat {
        CreateDeleteFilesClass.newFile(new String[0], PATH);
        List<Filter> filterList = ParseConfig.read(PATH);
        Assert.assertTrue(filterList.isEmpty());
    }

    @Test(expected = WrongConfigFormat.class)
    public void WrongFilterNumberInConfigFileTest() throws IOException, WrongConfigFormat {
        String[] filters = new String[1];
        filters[0] = "<abc    ";
        CreateDeleteFilesClass.newFile(filters, PATH);
        ParseConfig.read(PATH);
    }

    @Test(expected = WrongConfigFormat.class)
    public void WrongFilterPrefixInConfigFileTest() throws IOException, WrongConfigFormat {
        String[] filters = new String[1];
        filters[0] = " cpp    ";
        CreateDeleteFilesClass.newFile(filters, PATH);
        ParseConfig.read(PATH);
    }

    @Test(expected = WrongConfigFormat.class)
    public void WrongPermissionFilterPrefixInConfigFileTest() throws IOException, WrongConfigFormat {
        String[] filters = new String[1];
        filters[0] = " +qwe";
        CreateDeleteFilesClass.newFile(filters, PATH);
        ParseConfig.read(PATH);
    }

    @Test(expected = WrongConfigFormat.class)
    public void WrongMaskFilterPrefixInConfigFileTest() throws IOException, WrongConfigFormat {
        String[] filters = new String[1];
        filters[0] = "@???";
        CreateDeleteFilesClass.newFile(filters, PATH);
        ParseConfig.read(PATH);
    }

    @Test
    public void GoodConfigFileTest() throws IOException, WrongConfigFormat {
        String[] filters = new String[6];
        filters[0] = ".cpp    ";
        filters[1] = "  .h    ";
        filters[2] = "<1000000";
        filters[3] = ">1000000";
        filters[4] = "@f.+";
        filters[5] = "+rwx";
        CreateDeleteFilesClass.newFile(filters, PATH);
        List<Filter> filterList = ParseConfig.read(PATH);
        Assert.assertEquals(6, filterList.size());
        Assert.assertEquals(new ExtensionFilter("cpp").getFilterName(), filterList.get(0).getFilterName());
        Assert.assertEquals(new ExtensionFilter("h").getFilterName(), filterList.get(1).getFilterName());
        Assert.assertEquals(new BeforeThanFilter(1000000).getFilterName(), filterList.get(2).getFilterName());
        Assert.assertEquals(new LaterThanFilter(1000000).getFilterName(), filterList.get(3).getFilterName());
        Assert.assertEquals(new MaskFilter("f.+").getFilterName(), filterList.get(4).getFilterName());
        Assert.assertEquals(new FilePermissionsFilter("rwx").getFilterName(), filterList.get(5).getFilterName());
    }

    @AfterClass
    public static void removeAll() {
        CreateDeleteFilesClass.deleteDirectory(TEST_DIR);
    }
}