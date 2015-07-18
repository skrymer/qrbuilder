package com.skrymer.qrbuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
