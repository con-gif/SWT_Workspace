package org.iMage.mosaique.rectangle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
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
      this.tiles.add(new BufferedArtImage((BufferedImage) bufferedImage
              .getScaledInstance(tileWidth, tileHeight, 4)));
    }
  }

  @Override
  public List<BufferedImage> getThumbnails() {
    throw new RuntimeException("not implemented");
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

  @Override
  public int getTileWidth() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int getTileHeight() {
    throw new RuntimeException("not implemented");
  }
}
