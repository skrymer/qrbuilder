package org.skrymer.qrbuilder;

import org.skrymer.qrbuilder.exception.InvalidSizeException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;

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

    Assert.assertEquals("the answer to everything is 42",  TestHelpers.decode(qrcode));
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

  @Test
  public void whenFileNameIsEmpty_thenExceptionNamesTheOffendingParameter() {
    QRCode qrCode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(100, 100)
            .and()
            .withData("the answer to everything is 42")
    );

    IllegalArgumentException thrown = Assert.expectThrows(IllegalArgumentException.class,
        () -> qrCode.toFile("", "png"));

    assertEquals(thrown.getMessage(), "Parameter fileName cannot be empty");
  }

  @Test
  public void whenFileFormatIsNull_thenExceptionNamesTheOffendingParameter() {
    QRCode qrCode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(100, 100)
            .and()
            .withData("the answer to everything is 42")
    );

    IllegalArgumentException thrown = Assert.expectThrows(IllegalArgumentException.class,
        () -> qrCode.toFile("qrcode.png", null));

    assertEquals(thrown.getMessage(), "Parameter fileFormat cannot be empty");
  }

  @Test(expectedExceptions=InvalidSizeException.class)
  public void whenSizeIsNeverGiven_thenThrowInvalidSizeException() {
    QRCode.ZXingBuilder.build(builder ->
        builder.withData("the answer to everything is 42")
    ).toImage();
  }

  @Test
  public void whenTheRequestedSizeIsTooSmallForThePayload_thenThrowInvalidSizeException() {
    QRCode qrCode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(20, 20)
            .and()
            .withData("a somewhat longer payload that needs more modules than 20px")
    );

    InvalidSizeException thrown =
        Assert.expectThrows(InvalidSizeException.class, qrCode::toImage);

    Assert.assertTrue(thrown.getMessage().contains("20x20")
            && thrown.getMessage().contains("53x53"),
        "Expected the message to name both sizes but it is: " + thrown.getMessage());
  }

  @Test
  public void whenTheRequestedSizeIsLargeEnough_thenTheImageIsExactlyThatSize() {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
            .and()
            .withData("the answer to everything is 42")
    ).toImage();

    assertEquals(qrcode.getWidth(), 250);
    assertEquals(qrcode.getHeight(), 250);
  }

  @Test
  public void whenDataIsNonAscii_thenItRoundTripsThroughTheDefaultCharSet() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
            .and()
            .withData("\u65e5\u672c\u8a9e \u2013 \u00e6\u00f8\u00e5")
    ).toImage();

    assertEquals(TestHelpers.decode(qrcode), "\u65e5\u672c\u8a9e \u2013 \u00e6\u00f8\u00e5");
  }
}
