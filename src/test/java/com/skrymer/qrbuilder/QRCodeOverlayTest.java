package com.skrymer.qrbuilder;

import com.skrymer.qrbuilder.exception.UnreadableDataException;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.skrymer.qrbuilder.TestHelpers.decode;
import static com.skrymer.qrbuilder.decorator.ImageOverlay.addImageOverlay;
import static org.testng.Assert.assertEquals;

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
            .withDecorator(addImageOverlay(getOverlay(), 1.0f, 0.25f))
    ).toImage();

    assertEquals("and time began with a bang", decode(qrcode));
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

  private BufferedImage getOverlay()  {
    try {
      return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
