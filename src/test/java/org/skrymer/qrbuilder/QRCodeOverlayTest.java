package org.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.skrymer.qrbuilder.decorator.ImageOverlay;
import org.skrymer.qrbuilder.exception.UnreadableDataException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * QrCode overlay tests
 */
@Test
public class QRCodeOverlayTest {

  @Test(invocationCount=10)
  public void whenBuildingQrCodeWithOverlay_thenQRCodeDataShouldBeReadable() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
            .and()
            .withData("and time began with a bang")
            .and()
            .withDecorator(ImageOverlay.addImageOverlay(getOverlay(), 1.0f, 0.25f))
    ).toImage();

    Assert.assertEquals("and time began with a bang", TestHelpers.decode(qrcode));
  }

  @Test(expectedExceptions=UnreadableDataException.class)
  public void whenOverlayRatioIsToBig_thenThrowUnreadableDataException() throws Exception {
    QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
            .and()
            .withData("Some data")
            .and()
            .withDecorator(ImageOverlay.addImageOverlay(getOverlay(), 1.0f, 0.35f))
    ).toImage();
  }

  private BufferedImage getOverlay()  {
    try {
      return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
