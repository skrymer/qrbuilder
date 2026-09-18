package org.skrymer.qrbuilder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import org.skrymer.qrbuilder.decorator.ColoredQRCode;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by skrymer on 30/04/17.
 */
@Test
public class QRCodeColoringTest {

  @Test(invocationCount=10)
  public void whenColoringAQRCode_thenQRCodeShouldBeReadable() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(500, 500)
            .and()
            .withData("the ninjas are coming")
            .and()
            .withDecorator(ColoredQRCode.colorizeQRCode(Color.RED))
    ).toImage();

    Assert.assertEquals("the ninjas are coming", TestHelpers.decode(qrcode));
  }

  @Test(invocationCount=10)
  public void whenBuildingRedQRCode_thenQRCodeShouldBeRed() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(500, 500)
            .and()
            .withData("the ninjas are coming")
            .and()
            .withDecorator(ColoredQRCode.colorizeQRCode(Color.RED))
    ).toImage();
//    qrcode.getColorModel().getGreen()
    qrcode.getRGB(0,0);
    Assert.assertEquals("the ninjas are coming", TestHelpers.decode(qrcode));
  }

  @Test
  public void whenColorizingAQRCode_thenOnlyTheGivenColourAndWhiteRemain() throws Exception {
    BufferedImage qrcode = QRCode.ZXingBuilder.build(builder ->
        builder.withSize(500, 500)
            .and()
            .withData("the ninjas are coming")
            .and()
            .withDecorator(ColoredQRCode.colorizeQRCode(Color.RED))
    ).toImage();

    Assert.assertEquals(TestHelpers.distinctColours(qrcode),
        Set.of(Color.RED.getRGB(), Color.WHITE.getRGB()));
  }

  @Test
  public void whenWritingAColorizedQRCodeAsJpeg_thenTheFileIsNotEmpty() throws Exception {
    File target = Files.createTempDirectory("qrbuilder").resolve("qrcode.jpg").toFile();
    target.deleteOnExit();

    QRCode.ZXingBuilder.build(builder ->
        builder.withSize(500, 500)
            .and()
            .withData("the ninjas are coming")
            .and()
            .withDecorator(ColoredQRCode.colorizeQRCode(Color.RED))
    ).toFile(target.getAbsolutePath(), "jpg");

    Assert.assertTrue(target.length() > 0,
        "Expected a non-empty jpeg but the file is " + target.length() + " bytes");
  }
}
