package org.skrymer.qrbuilder;

import org.skrymer.qrbuilder.decorator.ImageOverlay;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
  public static final float TRANSPARENCY = 0.20f;
  public static final float OVERLAY_RATIO = 1f;
  public static final int WIDTH = 250;
  public static final int HEIGHT = 250;

  public static void main(String[] args) throws Exception {
    QRCode.ZXingBuilder.build(builder ->
        builder
            .withSize(WIDTH, HEIGHT)
            .and()
            .withData("One day, lad, all this will be yours. What, the curtains?")
            .and()
            .withColor(Color.green.darker())
            .and()
            .withDecorator(ImageOverlay.addImageOverlay(readImage("/images/skull_bw.png"), TRANSPARENCY, OVERLAY_RATIO))
            .and()
            .withCharSet(StandardCharsets.UTF_8)
            .verify(true)

    ).toFile("./qrCode.png", "PNG");
  }

  /**
   * Loads from the classpath rather than the working directory, so this runs from
   * anywhere and keeps working once packaged into the jar.
   */
  public static BufferedImage readImage(String resource) {
    try (InputStream in = Main.class.getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalArgumentException("No such resource on the classpath: " + resource);
      }
      return ImageIO.read(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
