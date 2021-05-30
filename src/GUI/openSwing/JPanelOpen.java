package GUI.openSwing;

import javax.swing.*;
import java.awt.*;

public class JPanelOpen extends JPanel {
    public JPanelOpen() {
        this.setBackground(null);
        this.setOpaque(false);
    }

    public JPanelOpen(LayoutManager layout) {
        this.setBackground(null);
        this.setOpaque(false);
        this.setLayout(layout);
    }
}


