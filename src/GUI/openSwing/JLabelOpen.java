package GUI.openSwing;

import javax.swing.*;

public class JLabelOpen extends JLabel {
    public JLabelOpen() {
        this.setBackground(null);
        this.setOpaque(false);
    }

    public JLabelOpen(String in) {
        this.setBackground(null);
        this.setOpaque(false);
        this.setText(in);
    }
}