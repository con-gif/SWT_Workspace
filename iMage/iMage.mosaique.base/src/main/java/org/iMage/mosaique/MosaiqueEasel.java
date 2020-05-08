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
   * Creates a mosaique image for a given sample.
   * @param input sample image.
   * @param artist generator of independent mosaique tiles.
   * @return generated mosaique aggregation.
   */
  @Override
  public BufferedImage createMosaique(BufferedImage input,
      IMosaiqueArtist<BufferedArtImage> artist) {

    BufferedImage output;
    output = artist.getTileForRegion(new BufferedArtImage(input)).toBufferedImage();

    return output;
  }

}
