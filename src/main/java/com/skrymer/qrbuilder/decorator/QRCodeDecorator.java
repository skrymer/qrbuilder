package com.skrymer.qrbuilder.decorator;

import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: skrymer
 * Date: 21/04/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface QRCodeDecorator {

  public BufferedImage decorate(BufferedImage qrcode);
}
