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
  private BufferedImage bufferedImage;

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
    this.bufferedImage = image.toBufferedImage();
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
      for (int j = 0; j < image.getWidth(); j++) {
        sum += image.getRGB(i, j);
      }
    }
    return sum / image.getHeight() * image.getWidth();
  }

  @Override
  public BufferedImage getThumbnail() {
    return (BufferedImage) this.bufferedImage.getScaledInstance(this.bufferedImage.getWidth() / 4,
            this.bufferedImage.getHeight() / 4, 4);
  }

  /**
   * Copies the contents of the current instance onto the method parameter.
   * @param targetRect BufferedArtImage to copy onto.
   */
  @Override
  public void drawMe(BufferedArtImage targetRect) {
    for (int i = 0; i < image.getHeight(); i++) {
      for (int j = 0; j < image.getWidth(); j++) {
        if (i < targetRect.getHeight() && j < targetRect.getWidth()) {
          targetRect.setRGB(i, j, image.getRGB(i, j));
        }
      }
    }
  }

  /**
   * Getter.
   * @return the height of the current instance in pixels.
   */
  @Override
  public int getHeight() {
    return this.bufferedImage.getHeight();
  }

  /**
   * Getter.
   * @return the width of the current instance in pixels.
   */
  @Override
  public int getWidth() {
    return this.bufferedImage.getWidth();
  }
}