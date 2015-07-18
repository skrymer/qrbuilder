This is a simple qrcode builder that is build ontop of the awesome ZXing library for barcode generation: https://github.com/zxing/zxing

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
    public static final float TRANSPARENCY = 0.25f;
    public static final float OVERLAY_RATIO = 1f;
    public static final int WIDTH = 250;
    public static final int HEIGHT = 250;

    public static void main(String[] args) throws Exception {
        QRCBuilder<BufferedImage> qrCodeBuilder = new ZXingQRCodeBuilder();

        qrCodeBuilder.newQRCode()
        .withSize(WIDTH, HEIGHT)
            .and()
        .withData("The answer is 42")
            .and()
        .decorate(colorizeQRCode(Color.green.darker()))
            .and()
        .decorate(addImageOverlay(ImageIO.read(new File("src/test/resources/images/skull_bw.png")), TRANSPARENCY, OVERLAY_RATIO))
            .and()
        .doVerify(true)
        .toFile("./qrCode.png", "PNG");
    }
}

```
The following qrCode is then generated:

![alt text](https://raw.github.com/wiki/skrymer/qrbuilder/images/qrcode.png "QRCode")

##Decorators

The builder uses the notion of decorators to decorate the generated QRCode. 

Decorators currently avaliable:
* com.skrymer.qrbuilder.decorator.ImageOverlay - adds a image overlay
* com.skrymer.qrbuilder.decorator.ColoredQRCode - colors the qrcode

You can create you're own Decorators by implementing the com.skrymer.qrbuilder.decorator.QRCodeDecorator interface
