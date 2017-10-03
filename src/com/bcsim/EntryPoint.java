package com.bcsim;

import com.bcsim.gui.MainForm;

import javax.swing.*;

public class EntryPoint {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });
    }
}
