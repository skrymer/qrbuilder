package com.skrymer.qrbuilder.decorator;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: skrymer
 * Date: 21/04/13
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageOverlay implements QRCodeDecorator {
  public static final Float DEFAULT_OVERLAY_TRANSPARENCY     = 1f;
  public static final Float DEFAULT_OVERLAY_TO_QRCODE_RATIO  = 0.25f;

  private BufferedImage overlay;
  private Float overlayToQRCodeRatio, overlayTransparency;

  /**
   * @param overlay - the image to be over layed ontop of the qrcode
   *
   * @param overlayToQRCodeRatio - Specifies the ratio between the overlay image and the QRCode in percentage like 0.20 = 20%.
   *                               Overlays should as a guide not take up more 25% of the QRCode or else the readability of the code could be compromised
   *                               Default is set to 25%
   *
   * @param overlayTransparency - the overlays transparency from 0..1 where one is no transparency.
   *                              Default is set to 1
   *
   * @throws IllegalArgumentException - if the overlay is null
   */
  public ImageOverlay(BufferedImage overlay, Float overlayTransparency, Float overlayToQRCodeRatio){
    if(overlay == null)
      throw new IllegalArgumentException("Overlay is required");

    this.overlay = overlay;

    if(overlayTransparency == null){
      this.overlayTransparency = DEFAULT_OVERLAY_TRANSPARENCY;
    }
    else{
      this.overlayTransparency = overlayTransparency;
    }

    if(overlayToQRCodeRatio == null){
      this.overlayToQRCodeRatio = DEFAULT_OVERLAY_TO_QRCODE_RATIO;
    }
    else {
      this.overlayToQRCodeRatio = overlayToQRCodeRatio;
    }
  }

  public BufferedImage decorate(BufferedImage qrcode) {
    BufferedImage scaledOverlay = scaleOverlay(qrcode);

    Integer deltaHeight = qrcode.getHeight() - scaledOverlay.getHeight();
    Integer deltaWidth  = qrcode.getWidth()  - scaledOverlay.getWidth();

    BufferedImage combined = new BufferedImage(qrcode.getWidth(), qrcode.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D)combined.getGraphics();
    g2.drawImage(qrcode, 0, 0, null);
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayTransparency));
    g2.drawImage(scaledOverlay, Math.round(deltaWidth/2), Math.round(deltaHeight/2), null);

    return combined;
  }

//-----------------
// private methods
//-----------------

  private BufferedImage scaleOverlay(BufferedImage qrcode){
    Integer scaledWidth = Math.round(qrcode.getWidth() * overlayToQRCodeRatio);
    Integer scaledHeight = Math.round(qrcode.getHeight() * overlayToQRCodeRatio);

    BufferedImage imageBuff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics g = imageBuff.createGraphics();
    g.drawImage(overlay.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH), 0, 0, new Color(0,0,0), null);
    g.dispose();

    return imageBuff;
  }
}
