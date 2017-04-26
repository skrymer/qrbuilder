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

    assertEquals("the answer to everything is 42",  decodeQRCode(qrcode));
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

  @Test(invocationCount=10)
  public void whenBuildingQrCodeWithOverlay_thenQRCodeShouldContainOverlay() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
                .and()
                .withData("and time began with a bang")
                .and()
                .withDecorator(addImageOverlay(getOverlay(), 1.0f, 0.25f))
    ).toImage();
    //Should assert overlay
    assertEquals("and time began with a bang", decodeQRCode(qrcode));
  }

  @Test(invocationCount=10)
  public void whenBuildingRedQRCode_thenQRCodeShouldBeRed() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(500, 500)
                .and()
                .withData("the ninjas are coming")
                .and()
                .withDecorator(colorizeQRCode(Color.RED))
    ).toImage();
//   Should assert color
//    assertEquals("Daffy duck is awesome", qrcode.);
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
	
  @Test(expectedExceptions=UnreadableDataException.class) 
  public void whenOverlayRatioIsToBig_thenThrowUnreadableDataException() throws Exception {
    QRCode.ZXingBuilder.build(builder ->
         builder.withSize(250, 250)
                .and()
                .withData("Some data")
                .and()
                .withDecorator(addImageOverlay(getOverlay(), 1.0f, 0.35f))
    ).toImage();
  }
		
//----------------------
// Helper methods
//----------------------
	
  private String decodeQRCode(BufferedImage qrcode) throws Exception{
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrcode)));
    Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

    Result result = new QRCodeReader().decode(binaryBitmap, decodeHints);
    
    return result.getText();
  }
	
  private BufferedImage getOverlay()  {
    try {
      return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
