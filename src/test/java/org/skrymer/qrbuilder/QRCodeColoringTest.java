package org.skrymer.qrbuilder;

import org.skrymer.qrbuilder.decorator.ColoredQRCode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.testng.Assert.assertEquals;

/**
 * Created by skrymer on 30/04/17.
 */
@Test
public class QRCodeColoringTest {

  @Test(invocationCount=10)
  public void whenColoringAQRCode_thenQRCodeShouldBeReadable() throws Exception {
    BufferedImage qrcode = QRCode.Builder.build(builder ->
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
    BufferedImage qrcode = QRCode.Builder.build(builder ->
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
}
