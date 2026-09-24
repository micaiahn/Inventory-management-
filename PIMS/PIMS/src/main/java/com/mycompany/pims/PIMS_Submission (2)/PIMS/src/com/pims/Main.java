package com.pims;

import com.pims.ui.LoginFrame;
import com.pims.util.UIStyle;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UIStyle.applyGlobalLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
