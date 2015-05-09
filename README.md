This is a simple qrcode builder that is build ontop of the awesome ZXing library for barcode generation: http://code.google.com/p/zxing/

QRCBuilder
=========

The builder is very simple to use, as the following example will demonstrate.

Create a qrcode with dimensions 250*250, a image overlay and some data:

```java
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

```
The following qrCode is then generated:

![alt text](https://raw.github.com/wiki/skrymer/qrbuilder/images/qrcode.png "QRCode")

##Decorators

The builder uses the notion of decorators to decorate the generated QRCode. 

Decorators currently avaliable:
* ImageOverlay to add a image overlay 
* ColoredQRCode which colors the qrcode

You can create you're own Decorators by implementing the QRCodeDecorator interface
