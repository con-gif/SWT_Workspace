package org.iMage.mosaique.rectangle;

import java.awt.image.BufferedImage;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueShape;
import org.iMage.mosaique.base.ImageUtils;

/**
 * This class represents a rectangle as {@link IMosaiqueShape} based on an {@link BufferedArtImage}.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleShape implements IMosaiqueShape<BufferedArtImage> {

  private BufferedArtImage image;

  /**
   * Create a new {@link IMosaiqueShape}.
   *
   * @param image
   *          the image to use
   * @param w
   *          the width
   * @param h
   *          the height
   */
  public RectangleShape(BufferedArtImage image, int w, int h) {
    BufferedImage bufferedImage = image.toBufferedImage();
    ImageUtils.scaleWidth(bufferedImage, w);
    ImageUtils.scaleHeight(bufferedImage, h);
    this.image = new BufferedArtImage(bufferedImage);
  }

  /**
   * Computes the average color TYPE_INT_ARGB value over all pixels of the image.
   * @return TYPE_INT_ARGB average color.
   */
  @Override
  public int getAverageColor() {
    int sum = 0;
    for (int i = 0; i < image.getHeight(); i++) {
      for (int j = 0; j < image.getWidth(); j ++) {
        sum += image.getRGB(i, j);
      }
    }
    return sum / image.getHeight() * image.getWidth();
  }

  @Override
  public BufferedImage getThumbnail() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public void drawMe(BufferedArtImage targetRect) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int getHeight() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int getWidth() {
    throw new RuntimeException("not implemented");
  }
}