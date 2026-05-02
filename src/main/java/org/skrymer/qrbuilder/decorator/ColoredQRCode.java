package org.skrymer.qrbuilder.decorator;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Decorator that colors a qrcode
 */
public class ColoredQRCode implements Decorator<BufferedImage> {
  private final Color color;

  /**
   * Colors the qrcode with the given color
   * @param color the color
   * @return this
   */
  public static Decorator<BufferedImage> colorizeQRCode(Color color){
    return new ColoredQRCode(color);
  }

  /**
   * @param color the color
   */
  private ColoredQRCode(Color color) {
    this.color = color;
  }

  /**
   * Colors the given qrcode
   * @param qrcode the qrcode to color
   * @return
   */
  public BufferedImage decorate(BufferedImage qrcode) {
    int width = qrcode.getWidth();
    int height = qrcode.getHeight();
    int blackRgb = Color.black.getRGB();
    int targetRgb = color.getRGB();

    var colored = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = qrcode.getRGB(x, y);
        colored.setRGB(x, y, rgb == blackRgb ? targetRgb : rgb);
      }
    }
    return colored;
  }
}
