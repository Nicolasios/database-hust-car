package Util;

import DataBase.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//本类用来保存数据库表属性
public class databaseUtil {
    public static String[] customerCol = new String[]{"id", "name", "age", "isVip"};
    public static String[] carCol = new String[]{"lisence", "brand", "rent", "status", "deposit"};
    public static String[] stuffCol = new String[]{"id", "name", "age"};
    public static String[] userCol = new String[]{"name", "password", "authority"};
    public static String[] infoCol = new String[]{"id", "moychange", "lisence", "customerid", "customerName",
            "event", "detailEvent", "time", "stuffID", "stuffName"};

    public static HashMap<String, String> primaryKeyMap = new HashMap<String, String>() {{
        put("User", "name");
        put("Car", "lisence");
        put("Event", "id");
    }};
    public static HashMap<String, String> table2name = new HashMap<String, String>() {{
        put("User", "user");
        put("Car", "car");
        put("Event", "event");
        put("Customer", "customer");
        put("Stuff","stuff");
    }};

    public static String[] getColNameByChoice(String choice) {
        String[] colName = new String[0];
        switch (choice) {
            case "User":
                colName = databaseUtil.userCol;
                break;
            case "Customer":
                colName = databaseUtil.customerCol;
                break;
            case "Event":
                colName = databaseUtil.infoCol;
                break;
            case "Car":
                colName = databaseUtil.carCol;
                break;
            case "Stuff":
                colName = databaseUtil.stuffCol;
                break;
        }
        return colName;
    }

    public static ArrayList<String> eventName = new ArrayList<>(Arrays.asList("损坏维修", "罚款", "借车", "还车"));


}
