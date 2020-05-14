package org.iMage.mosaique.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
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
import java.util.Objects;

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
  private static final int REQUIRED_IMAGE_COUNT = 10;
  private static final double REQUITED_SIZE_REDUCTION_RATIO = 10.0;

  private static BufferedArtImage input;
  private static Collection<BufferedArtImage> tiles;
  private static File outputDir;
  private static BufferedImage output;
  private static int tilesWidth = 0;
  private static int tilesHeight = 0;
  private static RectangleArtist artist;
  private static MosaiqueEasel easel;

  public static void main(String[] args) {

    CommandLine cmd = null;
    try {
      cmd = App.doCommandLineParsing(args);
    } catch (ParseException e) {
      System.err.println("Wrong command line arguments given: " + e.getMessage());
      System.exit(1);
    }

    input = fetchInput(cmd);
    tiles = fetchTiles(cmd);
    outputDir = fetchOutputDir(cmd);
    tilesWidth += fetchTilesWidth(cmd);
    tilesHeight += fetchTilesHeight(cmd);
    verifyDimensions(tilesWidth, tilesHeight);

    artist = new RectangleArtist(tiles, tilesWidth, tilesHeight);
    easel = new MosaiqueEasel();
    output = easel.createMosaique(input.toBufferedImage(), artist);
    writeOutput(output, outputDir);

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
      if (imageResource.toString().endsWith("jpg")) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
          ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
          reader.setInput(iis, true);
          ImageReadParam params = reader.getDefaultReadParam();
          input = new BufferedArtImage(reader.read(0, params));
          reader.dispose();
        } catch (IOException e) {
          throw new IllegalArgumentException();
        }
      } else if (imageResource.toString().endsWith("png")) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
          ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
          reader.setInput(iis, true);
          ImageReadParam params = reader.getDefaultReadParam();
          input = new BufferedArtImage(reader.read(0, params));
          reader.dispose();
        } catch (IOException e) {
          throw new IllegalArgumentException();
        }
      }

    } catch (MalformedURLException e) {
      System.err.println("Invalid or missing image origin path passed!");
      System.exit(1);
    } catch (IllegalArgumentException e) {
      System.err.println("Unsupported image format specified!");
      System.exit(1);
    }
    return input;
  }

  private static Collection<BufferedArtImage> fetchTiles(CommandLine cmd) {
    Collection<BufferedArtImage> tiles = new ArrayList<>();
    try {
      File tilesDir = new File(cmd.getOptionValue(CMD_OPTION_INPUT_TILES_DIR, MISSING));
      for (File fileEntry : Objects.requireNonNull(tilesDir.listFiles())) {
        if (fileEntry.getAbsolutePath().endsWith("jpg")) {
          final URL tileSource = new URL(fileEntry.getAbsolutePath());
          try (ImageInputStream iis = ImageIO.createImageInputStream(tileSource.openStream())) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
            reader.setInput(iis, true);
            ImageReadParam params = reader.getDefaultReadParam();
            tiles.add(new BufferedArtImage(reader.read(0, params)));
            reader.dispose();
          } catch (IOException e) {
            throw new IllegalArgumentException();
          }
        } else if (fileEntry.getAbsolutePath().endsWith("png")) {
          final URL tileSource = new URL(fileEntry.getAbsolutePath());
          try (ImageInputStream iis = ImageIO.createImageInputStream(tileSource.openStream())) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
            reader.setInput(iis, true);
            ImageReadParam params = reader.getDefaultReadParam();
            tiles.add(new BufferedArtImage(reader.read(0, params)));
            reader.dispose();
          } catch (IOException e) {
            throw new IllegalArgumentException();
          }
        }
      }
      if (tiles.size() < REQUIRED_IMAGE_COUNT) {
        throw new IllegalArgumentException();
      }
    } catch (MalformedURLException e) {
      System.err.println("Invalid or missing tiles path passed!");
      System.exit(1);
    } catch (IllegalArgumentException e) {
      System.err.println("Too few tile images provided or unsupported image format specified!");
      System.exit(1);
    }
    return tiles;
  }

  private static File fetchOutputDir(CommandLine cmd) {
    File outputDir = new File(cmd.getOptionValue(CMD_OPTION_OUTPUT_IMAGE, MISSING));
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
      tilesWidth = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_W, MISSING));

    } catch (NumberFormatException e) {
      System.err.println("Illegal number format for tiles width passed!");
      System.exit(1);
    }
    return tilesWidth;
  }

  private static int fetchTilesHeight(CommandLine cmd) {
    int tilesHeight = 0;
    try {
      tilesHeight = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_H, MISSING));
    } catch (NumberFormatException e) {
      System.err.println("Illegal number format for tiles height passed!");
      System.exit(1);
    }
    return tilesHeight;
  }

  private static void verifyDimensions(int tilesWidth, int tilesHeight) {
    try {
      if (tilesWidth != Math.round(input.getWidth() / REQUITED_SIZE_REDUCTION_RATIO) && tilesWidth != 0) {
        throw new NumberFormatException();
      }
      if (tilesHeight != Math.round(input.getHeight() / REQUITED_SIZE_REDUCTION_RATIO) && tilesHeight != 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      System.err.println("Invalid value for tiles width or/and height passed!");
      System.exit(1);
    }
  }

  private static void writeOutput(BufferedImage output, File outputDir) {
    try {
      ImageIO.write(output, "png", outputDir);
    } catch (IOException e) {
      System.err.println("Cannot write mosaique to file!");
      System.exit(1);
    }
  }
}
