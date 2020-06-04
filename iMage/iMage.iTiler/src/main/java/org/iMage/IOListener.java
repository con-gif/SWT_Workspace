package org.iMage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IOListener implements ActionListener {

    private final JFileChooser fileChooser = new JFileChooser();
    private int tileWidth;
    private int tileHeight;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == GUI.loadInput) {
            fileChooser.showOpenDialog(GUI.loadInput);
        } else if (e.getSource() == GUI.saveResult) {
            fileChooser.showSaveDialog(GUI.saveResult);
        } else if (e.getSource() == GUI.loadTiles) {
            try {
                tileWidth = Integer.parseInt(GUI.tileWidth.getText());
                tileHeight = Integer.parseInt(GUI.tileHeight.getText());
                fileChooser.showOpenDialog(GUI.loadTiles);
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Illegal tile size specified!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            GUI.tileWidth.setText("");
            GUI.tileHeight.setText("");
        }

    }
}
