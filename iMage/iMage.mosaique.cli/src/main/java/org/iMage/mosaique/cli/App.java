package org.iMage.mosaique.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.apache.commons.io.FilenameUtils;
import org.iMage.mosaique.rectangle.RectangleArtist;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class parses all command line parameters and creates a mosaique.
 */
public final class App {
  private App() {
    throw new IllegalAccessError();
  }

  private static final String CMD_OPTION_INPUT_IMAGE = "i";
  private static final String CMD_OPTION_INPUT_TILES_DIR = "t";
  private static final String CMD_OPTION_OUTPUT_IMAGE = "o";

  private static final String CMD_OPTION_TILE_W = "w";
  private static final String CMD_OPTION_TILE_H = "h";

  private static final String MISSING = null;

  private static BufferedArtImage input;
  private static Collection<BufferedArtImage> tiles;
  private static File outputDir;
  private static BufferedImage output;
  private static int tilesWidth = 0;
  private static int tilesHeight = 0;
  private static RectangleArtist artist;
  private static MosaiqueEasel easel;

  public static void main(String[] args) {
    // Don't touch...
    CommandLine cmd = null;
    try {
      cmd = App.doCommandLineParsing(args);
    } catch (ParseException e) {
      System.err.println("Wrong command line arguments given: " + e.getMessage());
      System.exit(1);
    }
    // ...this!

    input = fetchInput(cmd);
    tiles = fetchTiles(cmd);
    outputDir = fetchOutputDir(cmd);
    tilesWidth += fetchTilesWidth(cmd);
    tilesHeight += fetchTilesHeight(cmd);
    verifyDimensions(tilesWidth, tilesHeight);

    artist = new RectangleArtist(tiles, tilesWidth, tilesHeight);
    easel = new MosaiqueEasel();
    output = easel.createMosaique(input.toBufferedImage(), artist);

  }


  /**
   * Parse and check command line arguments
   *
   * @param args
   *          command line arguments given by the user
   * @return CommandLine object encapsulating all options
   * @throws ParseException
   *           if wrong command line parameters or arguments are given
   */
  private static CommandLine doCommandLineParsing(String[] args) throws ParseException {
    Options options = new Options();
    Option opt;

    /*
     * Define command line options and arguments
     */
    opt = new Option(App.CMD_OPTION_INPUT_IMAGE, "input-images", true, "path to input image");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_INPUT_TILES_DIR, "tiles-dir", true, "path to tiles directory");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_OUTPUT_IMAGE, "image-output", true, "path to output image");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_TILE_W, "tile-width", true, "the width of a tile");
    opt.setRequired(false);
    opt.setType(Integer.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_TILE_H, "tile-height", true, "the height of a tile");
    opt.setRequired(false);
    opt.setType(Integer.class);
    options.addOption(opt);

    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

  private static BufferedArtImage fetchInput(CommandLine cmd) {
    BufferedArtImage input = null;
    try {
      final URL imageResource = new URL(cmd.getOptionValue(CMD_OPTION_INPUT_IMAGE, MISSING));
      try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
        ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
        reader.setInput(iis, true);
        ImageReadParam params = reader.getDefaultReadParam();
        input = new BufferedArtImage(reader.read(0, params));
        reader.dispose();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }catch (MalformedURLException e) {
      System.err.println("Invalid or missing image origin path passed!");
      System.exit(1);
    }
    return input;
  }

  private static Collection<BufferedArtImage> fetchTiles(CommandLine cmd) {
    Collection<BufferedArtImage> tiles = new ArrayList<>();
    try {
      File tilesDir = new File(cmd.getOptionValue(CMD_OPTION_INPUT_TILES_DIR));
      for (final File fileEntry : tilesDir.listFiles()) {
        if (FilenameUtils.getExtension(fileEntry.getAbsolutePath()).endsWith("jpg")) {
          final URL tileSource = new URL(fileEntry.getAbsolutePath());
          try (ImageInputStream iis = ImageIO.createImageInputStream(tileSource.openStream())) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
            reader.setInput(iis, true);
            ImageReadParam params = reader.getDefaultReadParam();
            tiles.add(new BufferedArtImage(reader.read(0, params)));
            reader.dispose();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      if (tiles.size() < 10) {
        throw new IllegalAccessException();
      }
    } catch (MalformedURLException e) {
      System.err.println("Invalid or missing tiles path passed!");
      System.exit(1);
    } catch (IllegalAccessException e) {
      System.err.println("Too few tile images provided!");
      System.exit(1);
    }
    return tiles;
  }

  private static File fetchOutputDir(CommandLine cmd) {
    File outputDir = new File(cmd.getOptionValue(CMD_OPTION_OUTPUT_IMAGE));
    if (outputDir.isDirectory() || outputDir.isFile()) {
      return outputDir;
    } else {
      System.err.println("Invalid or missing output path passed!");
      System.exit(1);
      return null;
    }
  }

  private static int fetchTilesWidth(CommandLine cmd) {
    int tilesWidth = 0;
    try {
      tilesWidth = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_W));

    } catch (NumberFormatException e) {
      System.err.println("Illegal number format for tiles width passed!");
      System.exit(1);
    }
    return tilesWidth;
  }

  private static int fetchTilesHeight(CommandLine cmd) {
    int tilesHeight = 0;
    try {
      tilesHeight = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_H));
    } catch (NumberFormatException e) {
      System.err.println("Illegal number format for tiles height passed!");
      System.exit(1);
    }
    return tilesHeight;
  }

  private static void verifyDimensions(int tilesWidth, int tilesHeight) {
    try {
      if (tilesWidth != Math.round(input.getWidth() / 10.0) && tilesWidth != 0) {
        throw new NumberFormatException();
      }
      if (tilesHeight != Math.round(input.getHeight() / 10.0) && tilesHeight != 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      System.err.println("Invalid value for tiles width or/and height passed!");
      System.exit(1);
    }
  }
}
