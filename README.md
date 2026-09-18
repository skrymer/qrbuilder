This is a simple qrcode builder that is build ontop of the awesome ZXing library for barcode generation: https://github.com/zxing/zxing

QRCodeBuilder
=========

## Requirements

* JDK 25
* Maven 3.9+

## Build

```
mvn package
```

This also produces a runnable demo:

```
java -jar target/qrbuilder-0.1.jar
```

## Usage

The builder is very simple to use, as the following example will shows.

Create a QRCode with dimensions 250*250, a image overlay and some data:

```java
package org.skrymer.qrbuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.skrymer.qrbuilder.decorator.ImageOverlay.*;

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
            .withColor(Color.green.darker())
              .and()
            .withDecorator(addImageOverlay(readImage("/images/skull_bw.png"), TRANSPARENCY, OVERLAY_RATIO))
              .and()
            .verify(true)

    ).toFile("./qrCode.png", "PNG");
  }

  static BufferedImage readImage(String resource) {
    try (InputStream in = Main.class.getResourceAsStream(resource)) {
      return ImageIO.read(in);
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

The qrcode colour is not a decorator - set it on the builder with `withColor(Color)`,
which colours the code as it is rendered rather than repainting it afterwards.

## Sizing

`withSize(width, height)` is required, and the size you ask for is the size you get.
A payload that needs more modules than the requested size can hold throws
`InvalidSizeException` naming the minimum, rather than quietly returning a larger
image than you asked for.

You can create new Decorators by implementing the Decorator interface
