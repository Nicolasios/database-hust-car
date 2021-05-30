package DataBase;

import Util.Msg;
import Util.databaseUtil;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Database implements databasemethod {

    public static final String URL = "jdbc:mysql://localhost:3306/lab3?serverTimezone=UTC&useSSL=false";
    public static final String USER = "root";
    public static final String PASSWORD = "728827";

    private Connection connection;

    private static class InnerHelper {
        private final static Database dataBase = new Database();
    }

    private Database() {
    }

    public static Database getInstance() {
        return InnerHelper.dataBase;
    }

    private Vector<Vector<String>> pullVectors(ResultSet resultSet, int length) throws SQLException {
        Vector<Vector<String>> vectors = new Vector<>();
        while (resultSet.next()) {
            Vector<String> tempVec = new Vector<>();
            for (int i = 1; i <= length; i++) {//从1开始
                tempVec.add(resultSet.getString(i));
            }
            vectors.add(tempVec);
        }
        return vectors;
    }

    public boolean initConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//注册Driver类
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getWhereClause(HashMap<String, String> map, boolean havewhere) {
        StringBuilder sql = new StringBuilder();
        boolean isFirst = true;
        if (havewhere) {
            isFirst = false;
        }
        Set<String> keySet = map.keySet();

        for (String key : keySet) {
            String value = map.get(key);
            if (value.equals("")) continue;
            if (isFirst) {
                sql.append(" where ");
                isFirst = false;
            } else {
                sql.append("and ");
            }
            sql.append(key + " = '" + value + "' ");
        }
        return String.valueOf(sql);
    }

    @Override
    public int checkUser(String name, String psw) throws SQLException {
        boolean flagname = true;
        boolean flagpws = true;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from user");
        while (rs.next()) {
            flagname = rs.getString("name").equals(name);
            flagpws = rs.getString("password").equals(psw);

            if (flagname && flagpws) {
                return rs.getInt("authority");
            }
        }
        statement.close();
        return -1;
    }

    @Override
    public Vector<Vector<String>> getCarLists() throws SQLException {
        String sql = "select * from car";
        return getCarLists(sql);
    }

    @Override
    public Vector<Vector<String>> getCarLists(HashMap<String, String> map) throws SQLException {
        String sql = "select * from car";
        sql = sql + getWhereClause(map, false);
        return getCarLists(sql);
    }

    @Override
    public Vector<Vector<String>> getCarLists(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);

        Vector<Vector<String>> vectors = pullVectors(resultSet, databaseUtil.carCol.length);
        return vectors;
    }

    @Override
    public Vector<Vector<String>> getCustomerLists() throws SQLException {
        String sql = "select * from customer";
        return getCustomerLists(sql);
    }

    @Override
    public Vector<Vector<String>> getCustomerLists(HashMap<String, String> map) throws SQLException {
        String sql = "select * from customer";
        sql = sql + getWhereClause(map, false);
        return getCustomerLists(sql);
    }

    @Override
    public Vector<Vector<String>> getCustomerLists(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        Vector<Vector<String>> vectors = pullVectors(resultSet, databaseUtil.customerCol.length);
        statement.close();
        return vectors;
    }

    @Override
    public Vector<Vector<String>> getStuffLists() throws SQLException {
        String sql = "select * from stuff";
        return getStuffLists(sql);
    }

    @Override
    public Vector<Vector<String>> getStuffLists(HashMap<String, String> map) throws SQLException {
        String sql = "select * from stuff";
        sql = sql + getWhereClause(map, false);
        return getStuffLists(sql);
    }

    @Override
    public Vector<Vector<String>> getStuffLists(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        Vector<Vector<String>> vectors = pullVectors(resultSet, databaseUtil.stuffCol.length);
        statement.close();
        return vectors;
    }

    @Override
    public Vector<Vector<String>> getUserLists(int authority, String userName) throws SQLException {
        String sql = null;
        if (authority == 1) {
            sql = "select * from user ";
        } else if (authority == 2) {
            sql = "select * from user where authority <> 1 ";
        } else if (authority == 3) {
            sql = "select * from user where name = '" + userName + "' ";
        }
        return getUserLists(sql);
    }

    @Override
    public Vector<Vector<String>> getUserLists(HashMap<String, String> map, int authority, String userName) throws SQLException {
        String sql = null;
        if (authority == 1) {
            sql = "SELECT * FROM user ";
            sql = sql + getWhereClause(map, false);
        } else if (authority == 2) {
            sql = "SELECT * FROM user WHERE authority <> 1 ";
        } else if (authority == 3) {
            sql = "SELECT * FROM user WHERE name = '" + userName + "' ";
            sql = sql + getWhereClause(map, true);
        }
        return getUserLists(sql);
    }

    @Override
    public Vector<Vector<String>> getUserLists(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        Vector<Vector<String>> vectors = pullVectors(resultSet, databaseUtil.userCol.length);
        statement.close();
        return vectors;
    }

    @Override
    public Vector<Vector<String>> getInfoLists(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);

        Vector<Vector<String>> vectors = new Vector<>();
        while (resultSet.next()) {
            Vector<String> tempVec = new Vector<>();
            for (int i = 1; i <= databaseUtil.infoCol.length; i++) {//从1开始
                if (i == 6) {
                    switch (resultSet.getInt(i)) {//eventid to event
                        case 1:
                            tempVec.add("损坏维修");
                            break;
                        case 2:
                            tempVec.add("罚款");
                            break;
                        case 3:
                            tempVec.add("借车");
                            break;
                        case 4:
                            tempVec.add("还车");
                            break;
                    }
                } else {
                    tempVec.add(resultSet.getString(i));
                }
            }
            vectors.add(tempVec);
        }

        statement.close();
        return vectors;
    }

    @Override
    public Vector<Vector<String>> getInfoLists(HashMap<String, String> map, int authority, String userName) throws SQLException {
        String sql = "select event.id,event.moychange,car.lisence,customer.id,customer.name,event.event,event.detailevent,event.time,stuff.id,stuff.name \n" +
                "from event,car,customer,stuff\n" +
                "where event.lisence=car.lisence and event.stuffid=stuff.id and customer.id=event.customerid ";
        sql = sql + getWhereClause(map,true);
        return getInfoLists(sql);
    }

    @Override
    public Vector<Vector<String>> getInfoLists(int authority, String userName) throws SQLException {
        String sql = "select event.id,event.moychange,car.lisence,customer.id,customer.name,event.event,event.detailevent,event.time,stuff.id,stuff.name \n" +
                "from event,car,customer,stuff\n" +
                "where event.lisence=car.lisence and event.stuffid=stuff.id and customer.id=event.customerid ";
        if (authority == 3) {
            sql = sql + "AND customer.id = " + getIDbyUserName(userName) + " ";
        }
        return getInfoLists(sql);
    }

    @Override
    public String getIDbyUserName(String name) throws SQLException {
        String sql = String.format("SELECT customerid FROM user WHERE NAME = '%s'", name);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Vector<String> vector = new Vector<>();
        while (resultSet.next()) {
            vector.add(resultSet.getString(1));
        }
        statement.close();
        return vector.get(0);
    }

    @Override
    public void deleteRow(String tableMode, String primaryKey) throws SQLException {
        String tableName = databaseUtil.table2name.get(tableMode);
        String primarykeyName = databaseUtil.primaryKeyMap.get(tableMode);
        if (primarykeyName == null) primarykeyName = "id";
        String sql = String.format("delete from %s where %s = ?", tableName, primarykeyName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, primaryKey);
        System.out.println(preparedStatement.toString());
        preparedStatement.execute();
    }

    @Override
    //tableMode -> 表名  name-> 属性名  value-> 属性值  primarykey->主键值
    public void updateData(String tableMode, String name, String value, String primaryKey) throws SQLException {
        String tableName = databaseUtil.table2name.get(tableMode);
        String primaryKeyName = databaseUtil.primaryKeyMap.get(tableMode);
        if (primaryKeyName == null) primaryKeyName = "id";
        String sql = String.format("update %s set %s =? where %s = ?", tableName, name, primaryKeyName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (value.equals("")) {
            preparedStatement.setNull(1, Types.CHAR);
        } else {
            preparedStatement.setString(1, value);
        }
        preparedStatement.setString(2, primaryKey);
        System.out.println(preparedStatement.toString());
        preparedStatement.execute();
    }

    @Override
    public void addRow(String tableMode, HashMap<String, String> data) throws SQLException {
        Statement statement = connection.createStatement();
        String tableName = databaseUtil.table2name.get(tableMode);
        if (tableName != null) {
            StringBuilder columns = new StringBuilder("");
            StringBuilder values = new StringBuilder("");
            Set<String> keySet = data.keySet();
            Iterator<String> iterator = keySet.iterator();
            boolean isFirst = true;
            while (iterator.hasNext()) {
                String s = iterator.next();
                System.out.println(s);
                String value = data.get(s);
                if (databaseUtil.eventName.contains(value))
                    value = String.valueOf(databaseUtil.eventName.indexOf(value) + 1);
                if (value != null && !value.equals("")) {
                    if (isFirst) {//第一个不加逗号
                        isFirst = false;
                        columns.append(s);
                        values.append("'" + value + "'");
                    } else {
                        columns.append("," + s);
                        values.append(",'" + value + "'");
                    }
                }
            }
            String sql = String.format("insert into %s (%s) values (%s)", tableName, columns.toString(), values.toString());
            System.out.println(sql);
            statement.execute(sql);
            statement.close();
        }
    }
}
