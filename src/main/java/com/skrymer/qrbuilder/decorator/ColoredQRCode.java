package com.skrymer.qrbuilder.decorator;

import static com.skrymer.qrbuilder.util.ImageUtils.*;

import java.awt.*;
import java.awt.image.*;

/**
 * Decorator that colors a qrcode
 */
public class ColoredQRCode implements Decorator<BufferedImage> {
  private Color color;

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
    FilteredImageSource prod = new FilteredImageSource(qrcode.getSource(), new QRCodeRGBImageFilter());

    return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(prod));
  }

  private class QRCodeRGBImageFilter extends RGBImageFilter {
    public int filterRGB(int x, int y, int rgb) {
      if(rgb == Color.black.getRGB())
        return color.getRGB();

      return rgb;
    }
  }
}
