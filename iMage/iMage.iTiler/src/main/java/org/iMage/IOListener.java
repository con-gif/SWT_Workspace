package org.iMage;

import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.rectangle.RectangleArtist;
import org.iMage.mosaique.triangle.TriangleArtist;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action handling class for iMage GUI.
 *
 * @author Kaloyan Draganov
 * @version 1.0
 */
public class IOListener implements ActionListener {

    private final JFileChooser fileChooser = new JFileChooser();
    private static int tileWidth;
    private static int tileHeight;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == GUI.loadInput) {

            handleLoadInput();
        } else if (e.getSource() == GUI.saveResult) {

            handleSaveResult();
        } else if (e.getSource() == GUI.loadTiles) {

            handleLoadTiles();
        } else if (e.getSource() == GUI.showTiles) {

            handleShowTiles();
        } else if (e.getSource() == GUI.run) {

            handleRun();
        } else if (e.getSource() == GUI.tileWidth || e.getSource() == GUI.tileHeight) {

            handleTileDimensionsValidation();
        }

    }

    private void handleLoadInput() {
        fileChooser.showOpenDialog(GUI.loadInput);

        // read URL, check file format and save imported image in GUI.input
        File selection = fileChooser.getSelectedFile();
        if (selection == null) {
            return;
        }
        if (selection.getAbsolutePath().endsWith("jpg")
                || Objects.requireNonNull(selection).getAbsolutePath().endsWith("jpeg")
                || selection.getAbsolutePath().endsWith("png")) {

            try {
                GUI.input = ImageIO.read(selection);
                GUI.populatePreviewLabels();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "File not an image!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveResult() {
        // save GUI.output to selected filepath
        try {
            if (GUI.output == null) {
                throw new IllegalArgumentException();
            }

            fileChooser.showSaveDialog(GUI.saveResult);
            File destination = fileChooser.getSelectedFile();
            if (destination == null) {
                throw new IOException();
            }
            
            ImageIO.write(GUI.output, "png", destination);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Please, try again!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException illegalArgumentException) {
            JOptionPane.showMessageDialog(null, "Try selecting tile images and clicking run first ;-)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoadTiles() {
        // read URL, initialize GUI.artist. Min 10 images required.
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
            tileWidth = Integer.parseInt(GUI.tileWidth.getText());
            tileHeight = Integer.parseInt(GUI.tileHeight.getText());
            fileChooser.showOpenDialog(GUI.loadTiles);
            File[] tiles = fileChooser.getSelectedFile().listFiles();

            JFrame progress = new JFrame();
            JPanel panel = new JPanel();
            panel.setBackground(new Color(253, 253, 253));
            JProgressBar progressBar = new JProgressBar();
            int count = 0;

            progress.setLayout(new FlowLayout());
            progressBar.setMinimum(count);
            progressBar.setMaximum(tiles.length);

            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            panel.add(progressBar);
            progress.add(panel);
            progress.pack();
            progress.setVisible(true);
            progress.setResizable(false);
            progress.setTitle("Loading images...");
            progress.setSize(300, 100);

            new Thread();
            for (File tile : tiles) {
                if (tile.getAbsolutePath().endsWith("jpg")
                        || tile.getAbsolutePath().endsWith("jpeg")
                        || tile.getAbsolutePath().endsWith("png")) {
                    GUI.tiles.add(new BufferedArtImage(ImageIO.read(tile)));
                }
                count++;
                progressBar.setValue(count);
                progressBar.setString(String.format("Importing file %d of %d", count, tiles.length));
            }
            progress.dispose();

            if (GUI.tiles.size() < 10) {
                JOptionPane.showMessageDialog(null, "At least 10 images required for this task!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException();
            }

            GUI.artist = new RectangleArtist(GUI.tiles, tileWidth, tileHeight);
            GUI.tiles = new ArrayList<>();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Illegal tile size specified!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            // all fine
        }

        GUI.tileWidth.setText("");
        GUI.tileHeight.setText("");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    private void handleShowTiles() {
        if (GUI.artist == null) {
            return;
        }
        JFrame showFrame = new JFrame();
        showFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(253, 253, 253));
        panel.setLayout(new GridBagLayout());

        // creating a 7 column grid.
        List<BufferedImage> thumbnails;
        thumbnails = GUI.artist.getThumbnails();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        int col = 0;
        int row = 0;
        for (BufferedImage image : thumbnails) {
            constraints.gridx = col % 7;
            constraints.gridy = row;

            panel.add(new JLabel(new ImageIcon(image.getScaledInstance(70, 70, 0))), constraints);
            col++;
            if (col >= 7) {
                col %= 7;
                row++;
            }
        }
        constraints.gridx = 7;
        constraints.gridy = 0;
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setBounds(520, 0, 10, 530);
        //panel.add(scrollBar, constraints);

        showFrame.add(panel, FlowLayout.LEFT);
        showFrame.pack();
        showFrame.setLocationByPlatform(true);
        showFrame.setVisible(true);
        showFrame.setResizable(false);
        showFrame.setTitle("Your selection");
        showFrame.setSize(530, 530);
    }

    private void handleRun() {

        // GUI.artist is not null precisely when tiles have already been successfully imported.
        if (GUI.artist == null) {
            JOptionPane.showMessageDialog(null, "Please select tile images first!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            List<BufferedArtImage> thumbnails = new ArrayList<>();
            for (Object thumbnail : GUI.artist.getThumbnails()) {
                thumbnails.add(new BufferedArtImage((BufferedImage) thumbnail));
            }

            switch (Objects.requireNonNull(GUI.artistType.getSelectedItem()).toString()) {
                case "Rectangle":
                    GUI.artist = new RectangleArtist(thumbnails, tileWidth, tileHeight);
                    break;
                case "Triangle":
                    GUI.artist = new TriangleArtist(thumbnails, tileWidth, tileHeight);
                    break;
                default:
                    break;
            }
            GUI.easel = new MosaiqueEasel();
            GUI.output = GUI.easel.createMosaique(GUI.input, GUI.artist);
            GUI.populatePreviewLabels();

            // Preparing for a new cycle.
            GUI.artist = null;

            GUI.saveResult.setEnabled(true);
        }
    }

    private void handleTileDimensionsValidation() {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(GUI.tileWidth.getText() + GUI.tileHeight.getText());
        if (matcher.find()) {
            GUI.tileWidth.setForeground(Color.RED);
            GUI.tileHeight.setForeground(Color.RED);
            JOptionPane.showMessageDialog(null,
                    "If you run the loader with these values, iTiler will not be able to "
                            + "set a proper dimension to your photos!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            GUI.tileWidth.setForeground(Color.BLACK);
            GUI.tileHeight.setForeground(Color.BLACK);
        }
    }
}
