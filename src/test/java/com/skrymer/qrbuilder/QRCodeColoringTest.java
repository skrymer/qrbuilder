package com.skrymer.qrbuilder;

import org.testng.annotations.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.skrymer.qrbuilder.TestHelpers.decode;
import static com.skrymer.qrbuilder.decorator.ColoredQRCode.colorizeQRCode;
import static org.testng.Assert.assertEquals;

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
            .withDecorator(colorizeQRCode(Color.RED))
    ).toImage();

    assertEquals("the ninjas are coming", decode(qrcode));
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
//    qrcode.getColorModel().getGreen()

    qrcode.getRGB(0,0);
    assertEquals("the ninjas are coming", decode(qrcode));
  }
}
