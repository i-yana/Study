package ru.nsu.ccfit.zharkova.LoC.Main;
import ru.nsu.ccfit.zharkova.LoC.Main.filters.Filter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yana on 17.03.15.
 */
public class FileStatistic {

    private final List<Filter> filters;
    private final ForEachIterate fileList;
    private final String rootDir;
    private long total = 0;
    private long numFiles = 0;

    public FileStatistic(List<Filter> f, String dirName) throws PathOfDirectoryException {
        filters = f;
        fileList = new ForEachIterate(dirName);
        rootDir = dirName;
    }

    public List<Filter> getList() throws IOException {
        calculateStatistic(rootDir);
        Collections.sort(filters, new Comparator<Filter>() {
            public int compare(Filter o1, Filter o2) {
                if (o1.getNumOfLines() == o2.getNumOfLines()) {
                    return 0;
                }
                return o2.getNumOfLines() > o1.getNumOfLines()? 1 : -1;
            }
        });
        return filters;
    }

    public long getTotalLines(){
        return total;
    }

    public long getTotalFiles(){
        return numFiles;
    }

    public void calculateStatistic(String rootDir) throws IOException {
        boolean wasAdded;
        for(File file: fileList){
            long count = 0;
            wasAdded = false;
            for (Filter filter : filters) {
                if (filter.isGoodFile(file)) {
                    count = Counter.countLines(file);
                    filter.addLines(count);
                    filter.addFiles();
                    wasAdded = true;
                }
            }
            total +=count;
            if(wasAdded) {
                numFiles++;
            }
        }
    }
}
