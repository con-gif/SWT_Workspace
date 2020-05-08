package org.iMage.mosaique.rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;

/**
 * This class represents an {@link IMosaiqueArtist} who uses rectangles as tiles.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleArtist implements IMosaiqueArtist<BufferedArtImage> {

  private Collection<BufferedArtImage> tiles;
  private int tileWidth;
  private int tileHeight;

  /**
   * Create an artist who works with {@link RectangleShape RectangleShapes}
   *
   * @param images
   *          the images for the tiles
   * @param tileWidth
   *          the desired width of the tiles
   * @param tileHeight
   *          the desired height of the tiles
   * @throws IllegalArgumentException
   *           iff tileWidth or tileHeight &lt;= 0, or images is empty.
   */
  public RectangleArtist(Collection<BufferedArtImage> images, int tileWidth, int tileHeight) {
    this.tiles = new ArrayList<>();
    BufferedImage bufferedImage;
    for (BufferedArtImage image : images) {
      bufferedImage = image.toBufferedImage();
      this.tiles.add(new BufferedArtImage(imageToBufferedImage(bufferedImage.getScaledInstance(tileWidth, tileHeight, 4))));
    }
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  /**
   * Artificial cast from class Image to BufferedImage
   * @param image file to convert.
   * @return converted image.
   */
  private static BufferedImage imageToBufferedImage(Image image) {
    BufferedImage bufferedImage = new BufferedImage
            (image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
    Graphics bg = bufferedImage.getGraphics();
    bg.drawImage(image, 0, 0, null);
    bg.dispose();
    return bufferedImage;
  }

  /**
   * Getter.
   * @return a List of all tiles cast to BufferedImage.
   */
  @Override
  public List<BufferedImage> getThumbnails() {
    List<BufferedImage> thumbnails = new ArrayList<>();
    for (BufferedArtImage tile : tiles) {
      thumbnails.add(tile.toBufferedImage());
    }
    return thumbnails;
  }

  /**
   * Finds and returns the tile with the closest average color to a given image.
   * @param region image to compare colors against.
   * @return image with the closest average color.
   */
  @Override
  public BufferedArtImage getTileForRegion(BufferedArtImage region) {
    RectangleShape regionShape = new RectangleShape(region, region.getWidth(), region.getHeight());
    int regionAverageColor = regionShape.getAverageColor();

    RectangleShape currentTileRect;
    BufferedArtImage targetTile = region;
    RectangleShape targetShape = regionShape;
    for (BufferedArtImage tile : tiles) {
      currentTileRect = new RectangleShape(tile, tile.getWidth(), tile.getHeight());
      if (regionAverageColor - currentTileRect.getAverageColor() < regionAverageColor - targetShape.getAverageColor()
              || regionAverageColor - targetShape.getAverageColor() == 0) {
        targetTile = tile;
        targetShape = currentTileRect;
      }
    }
    return targetTile;
  }

  /**
   * Getter.
   * @return tile width preference of the current instance.
   */
  @Override
  public int getTileWidth() {
    return this.tileWidth;
  }

  /**
   * Getter.
   * @return tile height preference of the current instance.
   */
  @Override
  public int getTileHeight() {
    return this.tileHeight;
  }
}
