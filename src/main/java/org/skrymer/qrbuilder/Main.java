package org.skrymer.qrbuilder;

import org.skrymer.qrbuilder.decorator.ColoredQRCode;
import org.skrymer.qrbuilder.decorator.ImageOverlay;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Main {
  private static final float TRANSPARENCY = 0.20f;
  private static final float OVERLAY_RATIO = 1f;
  private static final int WIDTH = 250;
  private static final int HEIGHT = 250;

  public static void main(String[] args) {
    QRCode.Builder.build(builder ->
        builder
            .withSize(WIDTH, HEIGHT)
            .and()
            .withData(loremIpsum)
            .and()
            .withDecorator(ColoredQRCode.colorizeQRCode(Color.green.darker()))
            .and()
            .withDecorator(ImageOverlay.addImageOverlay(readImage("src/test/resources/images/skull_bw.png"), TRANSPARENCY, OVERLAY_RATIO))
            .and()
            .withCharSet(Charset.forName("UTF-8"))
            .verify(true)

    ).toFile("./qrCode.png", "PNG");
  }

  private static BufferedImage readImage(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam scelerisque dictum ipsum, mollis faucibus neque. Vestibulum suscipit eu urna eget lobortis.";
}
