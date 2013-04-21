package com.skrymer.qrbuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.skrymer.qrbuilder.decorator.ImageOverlay.*;
import static com.skrymer.qrbuilder.decorator.ColoredQRCode.colorizeQRCode;

public class Main {
  public static void main(String[] args) throws Exception {
    QRCBuilder qrcodeBuilder = new ZXingQRCodeBuilder();

    BufferedImage qrcode = qrcodeBuilder.newQRCode()
                                        .withSize(250, 250)
                                          .and()
                                        .withData("The answer to the universe and everything: 42")
                                          .and()
                                        .decorate(colorizeQRCode(Color.green.darker()))
                                          .and()
                                        .decorate(addImageOverlay(ImageIO.read(new File("src/test/resources/images/skull_bw.png")), 0.25f, 1f))
                                        .create();

    ImageIO.write(qrcode, "PNG", new File("/home/skrymer/Desktop/qrcode.png"));
  }
}
