package ru.nsu.ccfit.zharkova.LoC.Main.system;
import ru.nsu.ccfit.zharkova.LoC.Main.WrongConfigFormat;
import ru.nsu.ccfit.zharkova.LoC.Main.filters.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 18.03.15.
 */
public class ParseConfig {
    private final static String SEPARATOR = "\\s";
    private final static String EMPTY = "";
    private final static char BEFORE_THAN_FILTER_IDENTIFIER = '<';
    private final static char LATER_THAN_FILTER_IDENTIFIER = '>';
    private final static char EXTENSION_FILTER_IDENTIFIER = '.';
    private final static char FILE_PERMISSION_FILTER_IDENTIFIER = '+';
    private final static char MASK_FILTER_IDENTIFIER = '@';
    private final static int FIRST_SYMBOL = 0;
    private final static int FILE_EXTENSION = 1;
    private final static int TIME_OF_MODIFIED = 1;
    private final static int PERMISSION = 1;
    private final static int MASK = 1;

    public static List<Filter> read(String config) throws IOException, WrongConfigFormat {

        List<Filter> filters;

        BufferedReader fin = new BufferedReader(new FileReader(config));
        filters = new ArrayList<Filter>();
        String str;
        while ((str = fin.readLine()) != null) {
            if (str.length() != 0) {
                str = str.replaceAll(SEPARATOR, EMPTY);
                Filter filter;
                switch (str.charAt(0)){
                    case EXTENSION_FILTER_IDENTIFIER:
                        filter = createExtensionFilter(str);
                        break;
                    case BEFORE_THAN_FILTER_IDENTIFIER:
                        filter = createTimeFilter(str);
                        break;
                    case LATER_THAN_FILTER_IDENTIFIER:
                        filter = createTimeFilter(str);
                        break;
                    case FILE_PERMISSION_FILTER_IDENTIFIER:
                        filter = createPermissionFilter(str);
                        break;
                    case MASK_FILTER_IDENTIFIER:
                        filter = createMaskFilter(str);
                        break;
                    default:
                        throw new WrongConfigFormat(str);
                }
                filters.add(filter);
            }
        }
        return filters;
    }

    private static Filter createMaskFilter(String str) throws WrongConfigFormat {
        return new MaskFilter(str.substring(MASK));
    }

    private static Filter createPermissionFilter(String str) throws WrongConfigFormat {
        if(str.length() != 4){
            throw new WrongConfigFormat(str);
        }
        return new FilePermissionsFilter(str.substring(PERMISSION));
    }


    private static long getTime(String str) throws WrongConfigFormat {
        String tmp = str.substring(TIME_OF_MODIFIED);
        long timeStamp;
        try {
            timeStamp = Long.parseLong(tmp);
        }
        catch (NumberFormatException e){
            throw new WrongConfigFormat(str);
        }
        return timeStamp;
    }

    private static Filter createTimeFilter(String str) throws WrongConfigFormat {
        if (BEFORE_THAN_FILTER_IDENTIFIER == str.charAt(FIRST_SYMBOL)) {
            return new BeforeThanFilter(getTime(str));
        }
        if (LATER_THAN_FILTER_IDENTIFIER == str.charAt(FIRST_SYMBOL)) {
            return new LaterThanFilter(getTime(str));
        }
        return null;
    }

    private static Filter createExtensionFilter(String str) {
        if (EXTENSION_FILTER_IDENTIFIER != str.charAt(FIRST_SYMBOL)) {
            return null;
        }
        return new ExtensionFilter(str.substring(FILE_EXTENSION));
    }

}
