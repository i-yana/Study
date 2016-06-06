package ru.nsu.ccfit.zharkova.LoC.Main;
import java.io.File;
import java.util.*;
/**
 * Created by Yana on 16.03.15.
 */
public class ForEachIterate implements Iterable<File> {

    private Queue<File> folderQueue = new ArrayDeque<File>();
    private List<File> files = new ArrayList<File>();

    public ForEachIterate(String rootDir)throws PathOfDirectoryException {
        File dir = new File(rootDir);
        if (dir.isDirectory()) {
            folderQueue.add(dir);
        }
        else {
            throw new PathOfDirectoryException(rootDir);
        }
    }

    @Override
    public Iterator<File> iterator() {
        return new FileIterator();
    }

    public class FileIterator implements Iterator<File>{

        public void updateList(File dir){
            File[] curFiles = dir.listFiles();
            if (null == curFiles) {
                return;
            }
            for (File f : curFiles) {
                if (f.isDirectory()) {
                    folderQueue.add(f);
                } else if (f.isFile()) {
                    files.add(f);
                }
            }
        }

        @Override
        public boolean hasNext() {
            if(!files.isEmpty()) {
                return true;
            }
            if(folderQueue.isEmpty()){
                return false;
            }
            while(!folderQueue.isEmpty()) {
                updateList(folderQueue.poll());
                if (!files.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public File next() {
            return files.remove(0);
        }

        @Override
        public void remove() {
        }
    }
}
