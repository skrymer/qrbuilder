This is a simple qrcode builder that is build ontop of the awesome ZXing library for barcode generation: https://github.com/zxing/zxing

QRCodeBuilder
=========

The builder is very simple to use, as the following example will shows.

Create a QRCode with dimensions 250*250, a image overlay and some data:

```java
package org.skrymer.qrbuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.skrymer.qrbuilder.decorator.ImageOverlay.*;
import static com.skrymer.qrbuilder.decorator.ColoredQRCode.colorizeQRCode;

public class Main {
  static final float TRANSPARENCY = 0.25f;
  static final float OVERLAY_RATIO = 1f;
  static final int WIDTH = 250;
  static final int HEIGHT = 250;

  public static void main(String[] args) {
    QRCode.ZXingBuilder.build(builder ->
        builder.withSize(WIDTH, HEIGHT)
              .and()
            .withData("The answer is 42")
              .and()
            .withDecorator(colorizeQRCode(Color.green.darker()))
              .and()
            .withDecorator(addImageOverlay(readImage("src/test/resources/images/skull_bw.png"), TRANSPARENCY, OVERLAY_RATIO))
              .and()
            .doVerify(true)

    ).toFile("./qrCode.png", "PNG");
  }

  static BufferedImage readImage(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```
The following qrCode is then generated:

![alt text](https://raw.github.com/wiki/skrymer/qrbuilder/images/qrcode.png "QRCode")

## Decorators

The builder uses the decorators to decorate(obviously) the generated QRCode. 

Decorators currently available:
* ImageOverlay 
* ColoredQRCode 

You can create new Decorators by implementing the Decorator interface
