package org.skrymer.qrbuilder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

  @Test
  public void whenWritingAnOverlaidQRCodeAsJpeg_thenTheFileIsNotEmpty() throws Exception {
    File target = Files.createTempDirectory("qrbuilder").resolve("qrcode.jpg").toFile();
    target.deleteOnExit();

    QRCode.ZXingBuilder.build(builder ->
        builder.withSize(250, 250)
            .and()
            .withData("and time began with a bang")
            .and()
            .withDecorator(ImageOverlay.addImageOverlay(getOverlay(), 1.0f, 0.25f))
    ).toFile(target.getAbsolutePath(), "jpg");

    Assert.assertTrue(target.length() > 0,
        "Expected a non-empty jpeg but the file is " + target.length() + " bytes");
  }

  @Test
  public void whenOverlayingAnImage_thenItIsCentredAndCoversTheGivenRatio() throws Exception {
    BufferedImage overlay = TestHelpers.solidImage(80, 80, Color.BLUE);

    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(400, 400)
            .and()
            .withData("and time began with a bang")
            .and()
            .verify(false)
            .withDecorator(ImageOverlay.addImageOverlay(overlay, 1.0f, 0.25f))
    ).toImage();

    // 25% of 400 is a 100x100 overlay, centred leaves a 150px margin on each side.
    Assert.assertEquals(TestHelpers.boundingBoxOf(qrcode, Color.BLUE.getRGB()),
        new int[] {150, 150, 249, 249});
  }

  @Test
  public void whenOverlayIsTransparent_thenItBlendsWithTheQRCodeBeneath() throws Exception {
    BufferedImage overlay = TestHelpers.solidImage(80, 80, Color.BLUE);

    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(400, 400)
            .and()
            .withData("and time began with a bang")
            .and()
            .verify(false)
            .withDecorator(ImageOverlay.addImageOverlay(overlay, 0.5f, 0.25f))
    ).toImage();

    Assert.assertNull(TestHelpers.boundingBoxOf(qrcode, Color.BLUE.getRGB()),
        "A half transparent overlay should leave no fully opaque blue pixel");

    Color centre = new Color(qrcode.getRGB(200, 200));
    Assert.assertTrue(centre.getBlue() > centre.getRed() && centre.getBlue() > centre.getGreen(),
        "Expected the centre to be tinted towards the overlay colour but it is " + centre);
  }

  private BufferedImage getOverlay()  {
    try {
      return ImageIO.read(new File("src/test/resources/images/skull_bw.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
