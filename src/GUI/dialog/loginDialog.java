package GUI.dialog;

import Util.Msg;
import DataBase.Database;
import GUI.frame.loginFrame;
import Util.backgroundPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class loginDialog {
    private JDialog loginDailog;
    private JLabel nameLabel;
    private JLabel pswLabel;
    private JTextField nameDialogText;
    private JTextField pswDialogText;
    private JButton loginButton;
    private JPanel namePanel;
    private JPanel pswPanel;
    private String username;
    private String password;
    private int authority = 1;
    private Database database;

    public loginDialog(){
    }



    public void initDialog(){

        database = Database.getInstance();
        if(database.initConnection()) {
            loginDailog = new JDialog();
            JPanel bgp = new JPanel();
            loginDailog.setContentPane(bgp);
            loginDailog.setTitle("汽车租借信息系统");
            loginDailog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            nameLabel = new JLabel("用户名:");
            pswLabel = new JLabel("密码 :");
            nameLabel.setForeground(Color.black);
            pswLabel.setForeground(Color.black);

            nameDialogText = new JTextField(17);
            pswDialogText = new JPasswordField(10);
            loginButton = new JButton("登录");

            namePanel = new JPanel();
            namePanel.add(nameLabel, BorderLayout.WEST);
            namePanel.add(nameDialogText, BorderLayout.EAST);
            pswPanel = new JPanel();
            pswPanel.add(pswLabel, BorderLayout.WEST);
            pswPanel.add(pswDialogText, BorderLayout.EAST);

            loginDailog.getContentPane().setLayout(new BorderLayout());
            loginDailog.getContentPane().add(namePanel, BorderLayout.NORTH);
            loginDailog.getContentPane().add(pswPanel, BorderLayout.CENTER);
            loginDailog.getContentPane().add(loginButton, BorderLayout.SOUTH);
            loginButton.addActionListener(loginListener);

            loginDailog.setSize(new Dimension(270, 200));
            loginDailog.setResizable(false);
            Msg.setCenter(loginDailog);
        }
        else{
            Msg.noticeMsg("数据库连接失败",new JFrame());
        }
    }

    private ActionListener loginListener = (ActionEvent e) -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        username = nameDialogText.getText();
        password = pswDialogText.getText();
        System.out.println("name:" + username + "\npassword:" + password);
        try{
            authority = database.checkUser(username,password);
            System.out.println("authority:"+authority);

            if(authority!=-1){
                loginDailog.setVisible(false);
                new loginFrame(username,password,authority,loginDailog).initFrame();
            }
            else{
                Msg.noticeMsg("用户名或密码错误",new JFrame());
            }
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    };


}
