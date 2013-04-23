package com.skrymer.qrbuilder.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

  public static BufferedImage imageToBufferedImage(Image image) {

    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bufferedImage.createGraphics();
    g2.drawImage(image, 0, 0, null);
    g2.dispose();

    return bufferedImage;

  }
}
