package com.skrymer.qrbuilder.decorator;

import java.awt.image.BufferedImage;

/**
 * Implement this interface to create custom decorators
 */
public interface QRCodeDecorator {

  public BufferedImage decorate(BufferedImage qrcode);
}
