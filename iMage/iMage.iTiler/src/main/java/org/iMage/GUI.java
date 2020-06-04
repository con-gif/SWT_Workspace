package org.iMage;

import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * iMage GUI class.
 *
 * @author Kaloyan Draganov
 * @version 1.0
 *
 */
public final class GUI {

    private JFrame frame;
    private JPanel panel;

    private static JLabel inputImage;
    private static JLabel previewImage;
    private GridBagConstraints constraints;

    protected static JButton loadInput;
    protected static JButton saveResult;
    protected static JButton loadTiles;
    protected static JButton showTiles;
    protected static JButton run;

    private JLabel tileSize;
    private JLabel times;
    private JLabel artistLabel;

    protected static JTextField tileWidth;
    protected static JTextField tileHeight;

    protected static JComboBox artistType;

    protected static BufferedImage input;
    protected static BufferedImage output;
    protected static List<BufferedArtImage> tiles;

    protected static IMosaiqueArtist artist;
    protected static MosaiqueEasel easel;


    private GUI() {

        tiles = new ArrayList<>();
        initializeComponents();
        initializeListeners();
    }

    public static void main(String[] args) {
        new GUI();
    }

    public static void populatePreviewLabel() {
        inputImage.setIcon(new ImageIcon(input.getScaledInstance(350, 250, 0)));
        previewImage.setIcon(new ImageIcon(output.getScaledInstance(350, 250, 0)));
    }

    private void initializeComponents() {
        frame = new JFrame();
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();

        URL inputUrl = getClass().getResource("/input.jpg");
        URL previewURL = getClass().getResource("/preview.png");
        try {
            input = ImageIO.read(inputUrl);
            output = ImageIO.read(previewURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputImage = new JLabel();
        inputImage.setIcon(new ImageIcon(input.getScaledInstance(350, 250, 0)));
        previewImage = new JLabel();
        previewImage.setIcon(new ImageIcon(output.getScaledInstance(350, 250, 0)));

        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridwidth = 5;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(inputImage, constraints);
        constraints.gridwidth = 5;
        constraints.gridx = 6;
        constraints.gridy = 0;
        panel.add(previewImage, constraints);

        loadInput = new JButton("Load input");
        saveResult = new JButton("Save result");
        loadTiles = new JButton("Load tiles");
        showTiles = new JButton("Show tiles");
        run = new JButton("Run");

        constraints.gridwidth = 5;
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(loadInput, constraints);
        constraints.gridwidth = 5;
        constraints.gridx = 6;
        constraints.gridy = 1;
        panel.add(saveResult, constraints);

        tileSize = new JLabel("Tile size");
        times = new JLabel("X");
        artistLabel = new JLabel("Artist");

        tileWidth = new JTextField("", 4);
        tileHeight = new JTextField("", 4);

        artistType = new JComboBox(new String[] {"Rectangle", "Triangle"});
        artistType.setSelectedIndex(0);

        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(tileSize, constraints);
        constraints.gridx = 2;
        constraints.gridy = 2;
        panel.add(tileWidth, constraints);
        constraints.gridx = 3;
        constraints.gridy = 2;
        panel.add(times, constraints);
        constraints.gridx = 4;
        constraints.gridy = 2;
        panel.add(tileHeight, constraints);
        constraints.gridx = 5;
        constraints.gridy = 2;
        panel.add(loadTiles, constraints);
        constraints.gridx = 6;
        constraints.gridy = 2;
        panel.add(showTiles, constraints);
        constraints.gridx = 7;
        constraints.gridy = 2;
        panel.add(artistLabel, constraints);
        constraints.gridx = 8;
        constraints.gridy = 2;
        panel.add(artistType, constraints);
        constraints.gridx = 9;
        constraints.gridy = 2;
        panel.add(run, constraints);

        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("iTiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(800, 450);
    }

    private void initializeListeners() {

        IOListener listener = new IOListener();

        loadInput.addActionListener(listener);
        saveResult.addActionListener(listener);
        loadTiles.addActionListener(listener);
        showTiles.addActionListener(listener);
        run.addActionListener(listener);

    }
}
