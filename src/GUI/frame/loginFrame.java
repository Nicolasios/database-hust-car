package GUI.frame;

import DataBase.Database;
import GUI.openSwing.*;
import Util.Msg;
import Util.backgroundPanel;
import Util.databaseUtil;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class loginFrame {
    private JFrame loginFrame;
    private JPanel loginPanel;
    private JMenuBar loginMenuBar;
    private JMenu otherMenu;
    private JMenu fileMenu;
    private JMenu statisticMenu;
    private JMenu manageMenu;
    private JScrollPane scrollPane;
    private String username = "root";
    private String password = "111111";
    private ArrayList<JMenuItem> itemsOther;
    private ArrayList<JMenuItem> itemsStatistic;
    private ArrayList<JMenuItem> itemsManage;
    private ArrayList<JMenuItem> itemsFile;
    private JDialog loginDialog;
    private DefaultTableModel tableModel;
    private JTable jTable;

    private String CurrrntEvent = "";
    private Vector<String> columns;
    private int authority;


    public loginFrame() {
        loginFrame = new JFrame("Login Frame");
    }

    public loginFrame(String username, String password, int authority, JDialog loginDialog) {
        this.username = username;
        this.password = password;
        this.loginDialog = loginDialog;
        this.authority = authority;
        loginFrame = new JFrame("Login Frame");
    }

    public void initFrame() {
        try {
            loginPanel = new backgroundPanel("res/mainBack.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginFrame.setContentPane(loginPanel);
        loginFrame.pack();
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setTitle("汽车租借信息管理系统——欢迎" + username);
        loginFrame.setSize(1025, 635);

        initMenu();
        createPopupMenu();
        Msg.setCenter(loginFrame);
        loginFrame.setVisible(true);
    }

    public void initMenu() {
        loginMenuBar = new JMenuBar();

        otherMenu = new JMenu("other");
        itemsOther = new ArrayList<>();
        itemsOther.add(new JMenuItem("switch"));
        itemsOther.add(new JMenuItem("statement"));
        for (JMenuItem i : itemsOther) {
            otherMenu.add(i);
            i.addActionListener(otherListener);
        }

        fileMenu = new JMenu("file");
        itemsFile = new ArrayList<>();
        itemsFile.add(new JMenuItem("import"));
        for (JMenuItem i : itemsFile) {
            fileMenu.add(i);
            i.addActionListener(otherListener);
        }

        statisticMenu = new JMenu("统计数据");
        itemsStatistic = new ArrayList<>();
        itemsStatistic.add(new JMenuItem("统计表"));
        itemsStatistic.add(new JMenuItem("用户信誉"));
        for (JMenuItem i : itemsStatistic) {
            statisticMenu.add(i);
            i.addActionListener(otherListener);
        }

        manageMenu = new JMenu("manage");
        itemsManage = new ArrayList<>();
        itemsManage.add(new JMenuItem("Car"));
        if(authority!=3){
            itemsManage.add(new JMenuItem("Customer"));
            itemsManage.add(new JMenuItem("Stuff"));
        }
        itemsManage.add(new JMenuItem("User"));
        itemsManage.add(new JMenuItem("Event"));
        for (JMenuItem i : itemsManage) {
            manageMenu.add(i);
            i.addActionListener(manageListener);
        }

        loginMenuBar.add(fileMenu);
        loginMenuBar.add(manageMenu);
        loginMenuBar.add(statisticMenu);
        loginMenuBar.add(otherMenu);

        loginFrame.setJMenuBar(loginMenuBar);
    }

    private ActionListener otherListener = (ActionEvent e) -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        switch (strClick) {
            case "switch":
                loginFrame.getContentPane().removeAll();
                loginFrame.setVisible(false);
                loginDialog.setVisible(true);
                break;
            case "statement":
                Msg.noticeMsg("本信息管理系统基于Java Swing进行界面开发，MySQL后台支持\n\t——Power by TongYi", new JFrame());
                break;
        }
    };

    private void setTablePanel(Vector<Vector<String>> vectors, Vector<String> columns) {
        if (scrollPane != null) {
            loginFrame.remove(scrollPane);//如果当前页面存在之前查询的数据则先得删除掉
            scrollPane = null;
        }
        tableModel = new DefaultTableModel(vectors, columns) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (updateDate(aValue, row, column)) {
                    super.setValueAt(aValue, row, column);
                }
            }
        };
        if (CurrrntEvent.equals("Car") && authority == 3) {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        } else if (CurrrntEvent.equals("User") && authority == 3) {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 2;
                }
            };
        } else if (CurrrntEvent.equals("User") && authority != 1) { //除了权限为1的用户可以修改用户表的权限和客户之外别的都不行
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 2;
                }
            };
        } else if (CurrrntEvent.equals("Event") && authority != 3) {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 4 && column != 9 && column != 0;
                }
            };
        } else if (columns.get(0).equals("id")) {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column!=0;
                }
            };
        } else {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
            };
        }

        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setModel(tableModel);
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseRightButtonClick(e, jTable);
            }
        });

        scrollPane = new JScrollPane(jTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    //设置搜索栏
    private void setBlankInSearchPanel(JPanelOpen jPanelSearch, String names[]) {
        Set<String> comboxSet = comboxMap.keySet();

        for (String name : names) {
            JPanelOpen jPanelO = new JPanelOpen();
            JLabelOpen jLO = new JLabelOpen();
            if (name.equals("customerName") || name.equals("stuffName")) continue;//dirty code

            jLO.setText(name);

            if (comboxSet.contains(name)) {
                JComboBox<String> stringJComboBox = getCombox(name);
                jPanelO.add(jLO);
                jPanelO.add(stringJComboBox);
            } else {
                JTextField jTextField = new JTextField();
                jTextField.setColumns(11);
                jPanelO.add(jLO);
                jPanelO.add(jTextField);
            }
            jPanelSearch.add(jPanelO);
        }
    }

    private HashMap<String, String> getDataMap(JPanel jPanel) {
        HashMap<String, String> map = new HashMap<>();
        Component[] components = jPanel.getComponents();//获取选择panel所有组件
        for (int i = 0; i < components.length - 1; i++) {//最后一个是按钮，不需要
            JPanel jPanelGet = (JPanel) components[i];
            String key = ((JLabel) jPanelGet.getComponents()[0]).getText();
            if (key.equals("customerName") || key.equals("stuffName")) {
                continue;
            }

            String value;
            try {
                value = ((JTextField) jPanelGet.getComponents()[1]).getText();
            } catch (Exception e) {
                value = (String) ((JComboBox) jPanelGet.getComponents()[1]).getSelectedItem();
            }
            map.put(key, value);
            System.out.println("key: " + key + "; value: " + value);
        }
        return map;
    }

    private void setSearchPanel(JPanelOpen jPanelOpen) {
        jPanelOpen.setLayout(new FlowLayout());
        if (CurrrntEvent.equals("Car")) {
            setBlankInSearchPanel(jPanelOpen, databaseUtil.carCol);
        } else if (CurrrntEvent.equals("Customer")) {
            setBlankInSearchPanel(jPanelOpen, databaseUtil.customerCol);
        }
        if (CurrrntEvent.equals("User")) {
            setBlankInSearchPanel(jPanelOpen, databaseUtil.userCol);
        }
        if (CurrrntEvent.equals("Event")) {
            setBlankInSearchPanel(jPanelOpen, databaseUtil.infoCol);
        }
        if (CurrrntEvent.equals("Stuff")) {
            setBlankInSearchPanel(jPanelOpen, databaseUtil.stuffCol);
        }
        JButton jButton = new JButton("Search");
        jButton.addActionListener(e -> {
            HashMap<String, String> datamap = getDataMap(jPanelSearch);
            Vector<Vector<String>> vectors = null;
            try {
                switch (CurrrntEvent) {
                    case "Car":
                        vectors = Database.getInstance().getCarLists(datamap);
                        break;
                    case "User":
                        vectors = Database.getInstance().getUserLists(datamap, authority, username);
                        break;
                    case "Stuff":
                        vectors = Database.getInstance().getStuffLists(datamap);
                        break;
                    case "Customer":
                        vectors = Database.getInstance().getCustomerLists(datamap);
                        break;
                    case "Event":
                        vectors = Database.getInstance().getInfoLists(datamap, authority, username);
                        break;
                }
                setTablePanel(vectors, columns);
                loginPanel.add(scrollPane, BorderLayout.SOUTH);
                loginPanel.updateUI();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        jButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        jPanelOpen.add(jButton);

        jPanelOpen.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    private int delete_row_id;

    private void mouseRightButtonClick(MouseEvent evt, JTable jTable) {
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //通过点击位置找到点击为表格中的行
            int focusedRowIndex = jTable.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            delete_row_id = focusedRowIndex;
            //将表格所选项设为当前右键点击的行
            jTable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            //弹出菜单
            if (jPopupMenu != null) {
                jPopupMenu.show(jTable, evt.getX(), evt.getY());
            }
        }
    }

    private JPopupMenu jPopupMenu;

    private void createPopupMenu() {
        if (authority == 3) return;
        jPopupMenu = new JPopupMenu();

        JMenuItem delMenuItem = new JMenuItem();
        delMenuItem.setText("delete");
        delMenuItem.addActionListener(evt -> {
            String key = (String) jTable.getValueAt(delete_row_id, 0);
            try {
                Database.getInstance().deleteRow(CurrrntEvent, key);
                tableModel.removeRow(delete_row_id);
            } catch (SQLException e) {
                e.printStackTrace();
                Msg.noticeMsg("delete failed", new JFrame());
            }
        });

        JMenuItem addMenuItem = new JMenuItem();
        addMenuItem.setText("add");
        addMenuItem.addActionListener(evt -> setAddRowDialog());

        jPopupMenu.add(delMenuItem);
        jPopupMenu.add(addMenuItem);
    }

    private HashMap<String, String[]> comboxMap = new HashMap<String, String[]>() {{
        put("车况", new String[]{"", "1", "2", "3", "4", "5"});
        put("是否会员", new String[]{"", "Y", "N"});
        put("权限等级", new String[]{"", "1", "2", "3"});
        put("事件", new String[]{"", "broken-fix", "fire", "rent", "return"});
    }};

    private JComboBox<String> getCombox(String name) {
        JComboBox<String> comboBox = null;
        String[] context = comboxMap.get(name);
        if (context != null) {
            comboBox = new JComboBox<>(context);
        }
        return comboBox;
    }

    private JDialog addDialog;

    private void setAddRowDialog() {
        addDialog = new JDialog(loginFrame);
        addDialog.setTitle("add");
        addDialog.setLayout(new FlowLayout());
        JPanel contentPanel = new JPanel();

        addDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        String[] columnName = databaseUtil.getColNameByChoice(CurrrntEvent);
        System.out.println(CurrrntEvent);
        System.out.println(columnName.length);
        Set<String> comboxSet = comboxMap.keySet();

        for (String s : columnName) {
            System.out.println(s);
            if (!s.equals("customerName") && !s.equals("stuffName")) {
                JPanel jPanel = new JPanel();
                JLabel jLabel = new JLabel(s);
                jPanel.add(jLabel, BorderLayout.WEST);
                if (comboxSet.contains(s)) {
                    jPanel.add(getCombox(s), BorderLayout.WEST);
                } else {
                    JTextField jTextField = new JTextField(10);
                    jPanel.add(jTextField, BorderLayout.EAST);
                }
                contentPanel.add(jPanel);
            }
        }
        JButton jButton = new JButton("confirm");

        jButton.addActionListener(e -> {
            HashMap<String, String> map = getDataMap((JPanel) addDialog.getContentPane());
            try {
                Database.getInstance().addRow(CurrrntEvent, map);
                tableModel.addRow(map2vector(map));
                addDialog.dispose();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        contentPanel.add(jButton);
        addDialog.setContentPane(contentPanel);
        addDialog.pack();
        Msg.setCenter(addDialog);
    }

    private Vector<String> map2vector(HashMap<String, String> map) {
        Vector<String> vector = new Vector<>();

        String[] columnNames = databaseUtil.getColNameByChoice(CurrrntEvent);
        for (String s : columnNames) {
            String v = map.get(s);
            if (v != null) {
                vector.add(v);
            } else {
                vector.add("");
            }
        }

        return vector;
    }

    private JPanelOpen jPanelSearch;

    private ActionListener manageListener = (ActionEvent e) -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        //当前界面需要改变
        if (!CurrrntEvent.equals(strClick)) {
            CurrrntEvent = strClick;
            loginFrame.getContentPane().removeAll();

            Vector<Vector<String>> vectors = null;
            //
            try {
                switch (strClick) {
                    case "Car":
                        columns = new Vector<>(Arrays.asList(databaseUtil.carCol));
                        vectors = Database.getInstance().getCarLists();//初始化，显示所有的数据
                        break;
                    case "User":
                        columns = new Vector<>(Arrays.asList(databaseUtil.userCol));
                        vectors = Database.getInstance().getUserLists(authority, username);//需要权限
                        break;
                    case "Stuff":
                        columns = new Vector<>(Arrays.asList(databaseUtil.stuffCol));
                        vectors = Database.getInstance().getStuffLists();
                        break;
                    case "Event":
                        columns = new Vector<>(Arrays.asList(databaseUtil.infoCol));
                        vectors = Database.getInstance().getInfoLists(authority, username);//需要权限
                        break;
                    case "Customer":
                        columns = new Vector<>(Arrays.asList(databaseUtil.customerCol));
                        vectors = Database.getInstance().getCustomerLists();
                        break;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (vectors != null && columns != null) {
                setTablePanel(vectors, columns);
                jPanelSearch = new JPanelOpen();
                jPanelSearch.setPreferredSize(new Dimension(1000, 100));
                setSearchPanel(jPanelSearch);
                loginPanel.add(jPanelSearch, BorderLayout.CENTER);
                loginPanel.add(scrollPane, BorderLayout.SOUTH);
                loginPanel.updateUI();
            }

        }
    };

    private boolean updateDate(Object aValue, int row, int column) {
        String[] columnNames = databaseUtil.getColNameByChoice(CurrrntEvent);
        try {
            Database.getInstance().updateData(CurrrntEvent, columnNames[column], aValue.toString(), (String) jTable.getValueAt(row, 0));
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
