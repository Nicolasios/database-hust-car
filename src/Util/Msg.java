package Util;

import javax.swing.*;
import java.awt.*;

public class Msg {
    public static void noticeMsg(String in,JFrame frame) {//make code elegant
        JOptionPane.showMessageDialog(frame, in);
    }

    public static void setCenter(Component c) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int SCREEN_WIDTH = dim.width;
        int SCREEN_HEIGHT = dim.height;
        c.setLocation((SCREEN_WIDTH - c.getWidth()) / 2, (SCREEN_HEIGHT - c.getHeight()) / 2);
        c.setVisible(true);
    }
}
