package com.skrymer.qrbuilder;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.skrymer.qrbuilder.TestHelpers.decode;
import static com.skrymer.qrbuilder.decorator.ColoredQRCode.colorizeQRCode;
import static com.skrymer.qrbuilder.decorator.ImageOverlay.addImageOverlay;
import static org.testng.Assert.assertEquals;

/**
 * Tests for class ZXingBuilder
 */
@Test
public class QRCodeTest {

  @Test(invocationCount=10)
  public void whenBuildingSimpleQrCode_thenEncodedDataIsAsExpected() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
               .and()
               .withData("the answer to everything is 42")
    ).toImage();

    assertEquals("the answer to everything is 42",  decode(qrcode));
  }

  @Test(invocationCount=10)
  public void whenBuildingSimpleQrCode_thenWidthIsAsExpected() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(200, 250)
            .and()
            .withData("To be or not to be that is...")
    ).toImage();

    assertEquals(200,  qrcode.getWidth());
  }

  @Test(invocationCount=10)
  public void whenBuildingSimpleQrCode_thenHeightIsAsExpected() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(300, 300)
            .and()
            .withData("Daffy duck is awesome")
    ).toImage();

    assertEquals(300,  qrcode.getHeight());
  }

  @Test(expectedExceptions=InvalidSizeException.class)
  public void whenWidthIsZero_thenThrowCouldNotCreateQRCodeException(){
    QRCode.ZXingBuilder.build(builder ->
        builder.withSize(0, 1)
    ).toImage();
  }
	
  @Test(expectedExceptions=InvalidSizeException.class)
  public void whenHeightIsZero_thenThrowCouldNotCreateQRCodeException(){
    QRCode.ZXingBuilder.build(builder ->
         builder.withSize(1, 0)
    ).toImage();
  }
}
