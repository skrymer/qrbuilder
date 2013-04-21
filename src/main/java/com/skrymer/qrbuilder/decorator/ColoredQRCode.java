package com.skrymer.qrbuilder.decorator;

import static com.skrymer.qrbuilder.util.ImageUtils.*;

import java.awt.*;
import java.awt.image.*;

public class ColoredQRCode implements QRCodeDecorator {
  private Color color;

  /**
   * @param color
   */
  public ColoredQRCode(Color color) {
    this.color = color;
  }

  public BufferedImage decorate(BufferedImage qrcode) {
    FilteredImageSource prod = new FilteredImageSource(qrcode.getSource(), new QRCodeRGBImageFilter());//new QRCodeRGBImageFilter());

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
