package com.skrymer.qrbuilder.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: skrymer
 * Date: 21/04/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageUtils {

  public static BufferedImage imageToBufferedImage(Image image) {

    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bufferedImage.createGraphics();
    g2.drawImage(image, 0, 0, null);
    g2.dispose();

    return bufferedImage;

  }
}
