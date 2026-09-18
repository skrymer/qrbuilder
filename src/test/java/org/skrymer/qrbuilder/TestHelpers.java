package org.skrymer.qrbuilder;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by skrymer on 30/04/17.
 */
public class TestHelpers {
  public static String decode(BufferedImage qrcode) throws Exception{
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrcode)));
    Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

    Result result = new QRCodeReader().decode(binaryBitmap, decodeHints);

    return result.getText();
  }

  /**
   * Every distinct ARGB value present in the image.
   */
  public static Set<Integer> distinctColours(BufferedImage image) {
    Set<Integer> colours = new HashSet<>();
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        colours.add(image.getRGB(x, y));
      }
    }
    return colours;
  }

  /**
   * Bounding box of every pixel matching the given ARGB value, as
   * {minX, minY, maxX, maxY}, or null when the colour is absent.
   */
  public static int[] boundingBoxOf(BufferedImage image, int rgb) {
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -1, maxY = -1;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) == rgb) {
          minX = Math.min(minX, x);
          minY = Math.min(minY, y);
          maxX = Math.max(maxX, x);
          maxY = Math.max(maxY, y);
        }
      }
    }
    return maxX < 0 ? null : new int[] {minX, minY, maxX, maxY};
  }

  /**
   * A solid block of colour, usable as a locatable overlay.
   */
  public static BufferedImage solidImage(int width, int height, Color colour) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(colour);
    g.fillRect(0, 0, width, height);
    g.dispose();
    return image;
  }
}
