package org.iMage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {

    private JFrame frame;
    private JPanel panel;

    private JLabel inputImage;
    private JLabel preview;

    private JButton loadInput;
    private JButton saveResult;
    private JButton loadTiles;
    private JButton showTiles;
    private JButton run;

    private JLabel tileSize;
    private JLabel times;
    private JLabel artist;

    private JTextField tileWidth;
    private JTextField tileHeight;

    private JComboBox artistType;


    private GUI() {
        frame = new JFrame();
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30));
        panel.setLayout(new GridBagLayout());

        loadInput = new JButton("Load input");
        saveResult = new JButton("Save result");
        loadTiles = new JButton("Load tiles");
        showTiles = new JButton("Show tiles");
        run = new JButton("Run");

        loadInput.addActionListener(this);
        saveResult.addActionListener(this);
        loadTiles.addActionListener(this);
        showTiles.addActionListener(this);
        run.addActionListener(this);

        panel.add(loadInput);
        panel.add(saveResult);
        panel.add(loadTiles);
        panel.add(showTiles);
        panel.add(run);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));

        tileSize = new JLabel("Tile size");
        times = new JLabel("X");
        artist = new JLabel("Artist");

        tileWidth = new JTextField("", 4);
        tileHeight = new JTextField("", 4);

        artistType = new JComboBox(new String[] {"Rectangle", "Triangle"});
        artistType.setSelectedIndex(0);


        panel.add(tileSize);
        panel.add(tileWidth);
        panel.add(times);
        panel.add(tileHeight);
        panel.add(artist);
        panel.add(artistType);



        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("iTiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(800, 450);


    }
    public static void main(String[] args) {
        new GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
