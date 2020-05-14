package org.iMage.mosaique;

import java.awt.image.BufferedImage;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;
import org.iMage.mosaique.base.IMosaiqueEasel;

/**
 * This class defines an {@link IMosaiqueEasel} which operates on {@link BufferedArtImage
 * BufferedArtImages}.
 *
 * @author Dominik Fuchss
 *
 */
public class MosaiqueEasel implements IMosaiqueEasel<BufferedArtImage> {

  /**
   * Number of tiles per dimension for the mosaique generation.
   */
  public static final int REDUCTION_RATIO = 20;

  /**
   * Creates a mosaique image for a given sample.
   * @param input sample image.
   * @param artist generator of independent mosaique tiles.
   * @return generated mosaique aggregation.
   */
  @Override
  public BufferedImage createMosaique(BufferedImage input,
      IMosaiqueArtist<BufferedArtImage> artist) {

    int width = input.getWidth();
    int height = input.getHeight();

    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    BufferedImage region;
    BufferedArtImage tile;

    for (int i = 1; i < width - width / REDUCTION_RATIO; i += width / REDUCTION_RATIO) {

      for (int j = 1; j < height - height / REDUCTION_RATIO; j += height / REDUCTION_RATIO) {
        region = input.getSubimage(i, j, width / REDUCTION_RATIO, height / REDUCTION_RATIO);
        tile = artist.getTileForRegion(new BufferedArtImage(region));
        writeTile(output, tile, i, j);
      }
    }
    return output;
  }

  /**
   * Writes a tile onto an image file to create a piece of the mosaic effect.
   * @param target image to write onto.
   * @param tile information source.
   * @param startWidth width coordinate of the allocated space.
   * @param startHeight height coordinate of the allocated space.
   */
  private void writeTile(BufferedImage target, BufferedArtImage tile, int startWidth, int startHeight) {
    for (int i = startWidth; i < startWidth + tile.getWidth(); i++) {
      for (int j = startHeight; j < startHeight + tile.getHeight(); j++) {
        target.setRGB(i, j, tile.getRGB(i - startWidth, j - startHeight));
      }
    }
  }
}
