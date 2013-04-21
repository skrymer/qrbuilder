This is a simple qrcode builder that is build ontop of the awesome ZXing library for barcode generation: http://code.google.com/p/zxing/

qrbuilder
=========

The builder is very simple to use as the following example will show.

Create a qrcode with dimensions 250*250, a image overlay and some data:

```java
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
                                        .create();

    ImageIO.write(qrcode, "PNG", new File("/home/skrymer/Desktop/qrcode.png"));
  }
}
```
![alt text](https://raw.github.com/wiki/skrymer/qrbuilder/images/qrcode.png "Concept")

The builder uses decorators to decorate the generated QRCode. Currently there is only to decorators namly ImageOverlay to add a overlay and ColoredQRCode which colors the qrcode
