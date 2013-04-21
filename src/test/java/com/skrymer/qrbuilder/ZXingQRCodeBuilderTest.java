package com.skrymer.qrbuilder;

import static org.testng.Assert.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.DecodeHintType;
import com.google.zxing.qrcode.QRCodeReader;
import com.skrymer.qrbuilder.decorator.ColoredQRCode;
import com.skrymer.qrbuilder.decorator.ImageOverlay;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;

@Test
public class ZXingQRCodeBuilderTest {
  private static final String EXPTECTED_QRCODE_DATA  = "some awesome data";
  private static final int    EXPECTED_QRCODE_WIDTH  = 250;
  private static final int    EXPECTED_QRCODE_HEIGHT = 250;
  
  private QRCBuilder sut;
  
  @BeforeMethod
  public void setUp(){
    sut = new ZXingQRCodeBuilder();
  }
  
  @Test(timeOut=500, invocationCount=10, successPercentage=95)
  public void testBuildQRCode_noOverlay_sucess() throws Exception {
    BufferedImage qrcode = sut.newQRCode()
                              .withSize(EXPECTED_QRCODE_WIDTH, EXPECTED_QRCODE_HEIGHT)
                                .and()
                              .withData(EXPTECTED_QRCODE_DATA)
                              .create();
    
    assertQRCode(qrcode);
  }

  @Test(timeOut=500)
  public void testBuildQRCode_withImageOverlay_sucess() throws Exception {
    BufferedImage qrcode = sut.newQRCode()
                              .withSize(EXPECTED_QRCODE_WIDTH, EXPECTED_QRCODE_HEIGHT)
                                .and()
                              .withData(EXPTECTED_QRCODE_DATA)
                                .and()
                              .decorate(new ImageOverlay(getOverlay(), 1.0f, 0.25f))
                              .create();

    assertQRCode(qrcode);
  }

  @Test(timeOut=1000)
  public void testBuildQRCode_createRedQRCode_sucess() throws Exception {
    BufferedImage qrcode = sut.newQRCode()
                              .withSize(EXPECTED_QRCODE_WIDTH, EXPECTED_QRCODE_HEIGHT)
                                .and()
                              .withData(EXPTECTED_QRCODE_DATA)
                                .and()
                              .decorate(new ColoredQRCode(Color.RED))
                               .and()
                              .verifyQRCode(false)
                              .create();

    assertQRCode(qrcode);
  }

  @Test(expectedExceptions=InvalidSizeException.class)
  public void testBuildQRCode_widthIsZero_throwCouldNotCreateQRCodeException(){
    sut.newQRCode()
       .withSize(0, 1)
         .and()
       .withData(EXPTECTED_QRCODE_DATA)
       .create();
  }
	
  @Test(expectedExceptions=InvalidSizeException.class)
  public void testBuildQRCode_heightIsZero_throwCouldNotCreateQRCodeException(){
    sut.newQRCode()
       .withSize(1, 0)
         .and()
       .withData(EXPTECTED_QRCODE_DATA)
       .create();
  }
	
  @Test(expectedExceptions=UnreadableDataException.class) 
  public void testBuildQRCode_overlayToBig_throwUnreadableDataException() throws Exception {
    sut.newQRCode()
       .withSize(250, 250)
         .and()
       .withData(EXPTECTED_QRCODE_DATA)
         .and()
       .decorate(new ImageOverlay(getOverlay(), 1.0f, 0.35f))
       .create();
  }
		
//----------------------
// Helper methods
//----------------------
	
  private void assertQRCode(BufferedImage qrcode) throws Exception {
    assertEquals(EXPECTED_QRCODE_WIDTH,  qrcode.getWidth());
    assertEquals(EXPECTED_QRCODE_HEIGHT, qrcode.getHeight());
    assertEquals(EXPTECTED_QRCODE_DATA,  decodeQRCode(qrcode));  
  }
	
  private String decodeQRCode(BufferedImage qrcode) throws Exception{
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrcode)));
    Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    decodeHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
    decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

    Result result = new QRCodeReader().decode(binaryBitmap, decodeHints);
    
    return result.getText();
  }
	
  private BufferedImage getOverlay() throws Exception {
    return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
  }
}
