package ru.nsu.ccfit.zharkova.LoC.Tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nsu.ccfit.zharkova.LoC.Main.*;
import ru.nsu.ccfit.zharkova.LoC.Main.filters.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 18.03.15.
 */
public class FileStatisticTest {

    private static final String TEST_DIR = "./FileStatisticTest";
    private static final String TEST_FILE = "test";
    private static final char SLASH = '/';
    private static final String PATH = TEST_DIR+SLASH+TEST_FILE;
    private static List<Filter> filters = null;

    @BeforeClass
    public static void createFiltersAndDirectory() throws IOException, WrongConfigFormat {
        if(filters == null){
            filters = new ArrayList<Filter>();
        }
        else {
            filters.clear();
        }
        filters.add(new ExtensionFilter("cpp"));
        filters.add(new ExtensionFilter("java"));
        filters.add(new BeforeThanFilter(1000));
        filters.add(new LaterThanFilter(1000));
        filters.add(new FilePermissionsFilter("r-x"));
        filters.add(new MaskFilter("^t.+"));
        CreateDeleteFilesClass.newDirectory(TEST_DIR);
    }

    @Test
    public void getTotalStatisticTest() throws IOException, PathOfDirectoryException {
        for(int i = 0; i<3; i++){
            CreateDeleteFilesClass.newFile(new String[60], PATH + i + ".cpp");
        }
        for(int i = 0; i<2; i++){
            CreateDeleteFilesClass.newFile(new String[10], PATH + i + ".java");
        }
        for(int i = 0; i<2; i++) {
            CreateDeleteFilesClass.newFile(new String[8], PATH + i + ".c");
        }
        for(int i = 0; i<2; i++) {
            CreateDeleteFilesClass.newFile(new String[8], PATH + i + ".txt");
        }
        FileStatistic fs = new FileStatistic(filters,TEST_DIR);
        List<Filter> lf = fs.getList();
        for (Filter f: lf){
            System.out.println(f.getFilterName() + " - " + f.getNumOfFiles()+ " files, " + f.getNumOfLines()+ " lines");
        }
        Assert.assertEquals(fs.getTotalFiles(), 9);
        Assert.assertEquals(fs.getTotalLines(), 232);
        Assert.assertEquals(lf.get(0).getNumOfFiles(), 9);//>1000
        Assert.assertEquals(lf.get(0).getNumOfLines(), 232);//>1000
        Assert.assertEquals(lf.get(1).getNumOfFiles(), 9);//^t.+
        Assert.assertEquals(lf.get(1).getNumOfLines(), 232);//^t.+
        Assert.assertEquals(lf.get(2).getNumOfFiles(), 3);//cpp
        Assert.assertEquals(lf.get(2).getNumOfLines(), 180);//cpp
        Assert.assertEquals(lf.get(3).getNumOfLines(), 20);//java
        Assert.assertEquals(lf.get(3).getNumOfFiles(), 2);//java
        Assert.assertEquals(lf.get(4).getNumOfLines(), 0);//<1000
        Assert.assertEquals(lf.get(4).getNumOfFiles(), 0);//<1000
        Assert.assertEquals(lf.get(5).getNumOfLines(), 0);//r_x
        Assert.assertEquals(lf.get(5).getNumOfFiles(), 0);//r_x
        Assert.assertEquals(lf.size(), filters.size());
    }


    @AfterClass
    public static void removeAll(){
        CreateDeleteFilesClass.deleteDirectory(TEST_DIR);
        filters.clear();
    }
}
