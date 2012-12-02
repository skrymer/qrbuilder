package com.skrymer.qrbuilder;

import static org.testng.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;

@Test
public class QRCodeBuilderTest {
  private static final String EXPTECTED_QRCODE_DATA = "some awesome data";
  private static final int EXPECTED_QRCODE_WIDTH 	= 250;
  private static final int EXPECTED_QRCODE_HEIGHT 	= 250;
  
  private ZXingQRCodeBuilder sut;
  
  @BeforeMethod
  public void setUp(){
    sut = new ZXingQRCodeBuilder();
  }
  
  @Test(timeOut=500)
  public void testBuildQRCode_noOverlay_sucess() throws Exception {
    BufferedImage qrcode = sut.newQRCode()
    						  .withSize(EXPECTED_QRCODE_WIDTH, EXPECTED_QRCODE_HEIGHT)
	                          .withData(EXPTECTED_QRCODE_DATA)
	                          .create();
    
    assertQRCode(qrcode);
  }

  @Test(timeOut=500)
  public void testBuildQRCode_withImageOverlay_sucess() throws Exception {
    BufferedImage qrcode = sut.newQRCode()
    						  .withSize(EXPECTED_QRCODE_WIDTH, EXPECTED_QRCODE_HEIGHT)
                              .withData(EXPTECTED_QRCODE_DATA)
                              .withImageOverlay(getOverlay())
                              .withOverlayRatio(0.25f)
                              .withOverlayTransparency(1.0f)
                              .create();
        
    assertQRCode(qrcode);
  }
	 
  @Test(expectedExceptions=InvalidSizeException.class)
  public void testBuildQRCode_widthIsZero_throwCouldNotCreateQRCodeException(){
    sut.newQRCode()
       .withSize(0, 1)
       .withData(EXPTECTED_QRCODE_DATA)
       .create();
  }
	
  @Test(expectedExceptions=InvalidSizeException.class)
  public void testBuildQRCode_heightIsZero_throwCouldNotCreateQRCodeException(){
    sut.newQRCode()
       .withSize(1, 0)
       .withData(EXPTECTED_QRCODE_DATA)
       .create();
  }
	
  @Test(expectedExceptions=UnreadableDataException.class) 
  public void testBuildQRCode_overlayToBig_throwUnreadableDataException() throws Exception {
    sut.newQRCode()
       .withSize(250, 250)
       .withData(EXPTECTED_QRCODE_DATA)
       .withImageOverlay(getOverlay())
       .withOverlayRatio(0.35f)
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
    Result result = new MultiFormatReader().decode(binaryBitmap);
    
    return result.getText();
  }
	
  private BufferedImage getOverlay() throws Exception {
    return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
  }
}
