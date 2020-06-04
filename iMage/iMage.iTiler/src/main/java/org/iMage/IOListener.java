package org.iMage;

import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.rectangle.RectangleArtist;
import org.iMage.mosaique.triangle.TriangleArtist;

import javax.imageio.ImageIO;
import javax.swing.*;
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
        }

    }


    private void handleLoadInput() {
        fileChooser.showOpenDialog(GUI.loadInput);

        // read URL, check file format and save imported image in GUI.input
        File selection = fileChooser.getSelectedFile();
        if (selection != null && selection.getAbsolutePath().endsWith("jpg")
                || Objects.requireNonNull(selection).getAbsolutePath().endsWith("jpeg")
                || selection.getAbsolutePath().endsWith("png")) {

            try {
                GUI.input = ImageIO.read(selection);
                GUI.populatePreviewLabels();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if (selection != null) {
            JOptionPane.showMessageDialog(null, "File not an image!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveResult() {

        fileChooser.showSaveDialog(GUI.saveResult);
    }

    private void handleLoadTiles() {
        // read URL, initialize GUI.artist. Min 10 images required.
        fileChooser.setMultiSelectionEnabled(true);
        try {
            tileWidth = Integer.parseInt(GUI.tileWidth.getText());
            tileHeight = Integer.parseInt(GUI.tileHeight.getText());
            fileChooser.showOpenDialog(GUI.loadTiles);
            File[] tiles = fileChooser.getSelectedFiles();

            JFrame progress = new JFrame();
            JPanel panel = new JPanel();
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

            for (File tile : tiles) {
                if (tile.getAbsolutePath().endsWith("jpg")
                        || tile.getAbsolutePath().endsWith("jpeg")
                        || tile.getAbsolutePath().endsWith("png")) {
                    GUI.tiles.add(new BufferedArtImage(ImageIO.read(tile)));
                }
                count++;
                progressBar.setValue(count);
                progressBar.setString(String.format("Importing file %d of %d", count, tiles.length));
                progressBar.setStringPainted(true);
                panel.add(progressBar);
                progress.add(panel);
            }
            progress.dispose();

            if (GUI.tiles.size() < 10) {
                JOptionPane.showMessageDialog(null, "At least 10 images required for this task!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            GUI.artist = new RectangleArtist(GUI.tiles, tileWidth, tileHeight);
            GUI.tiles = new ArrayList<>();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Illegal tile size specified!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

        GUI.tileWidth.setText("");
        GUI.tileHeight.setText("");
        fileChooser.setMultiSelectionEnabled(false);
    }

    private void handleShowTiles() {

        JFrame showFrame = new JFrame();
        showFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // creating a 7 x 7 grid.
        List<BufferedImage> thumbnails;
        thumbnails = GUI.artist.getThumbnails();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        int col = 0;
        int row = 0;
        for (BufferedImage image : thumbnails) {
            if (row >= 7) {
                break;
            }
            constraints.gridx = col % 7;
            constraints.gridy = row;

            panel.add(new JLabel(new ImageIcon(image.getScaledInstance(70, 70, 0))), constraints);
            col++;
            if (col >= 7) {
                col %= 7;
                row ++;
            }
        }

        JScrollPane scroller = new JScrollPane(panel);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        showFrame.add(panel, FlowLayout.LEFT);
        showFrame.pack();
        showFrame.setLocationByPlatform(true);
        showFrame.setVisible(true);
        showFrame.setResizable(false);
        showFrame.setTitle("Your selection");
        showFrame.setSize(530, 530);
    }

    private void handleRun() {

        // GUI.artist ist not null precisely when tiles have already been successfully imported.
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
        }
    }
}
