package org.skrymer.qrbuilder.decorator;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Adds a image overlay to a qrcode
 */
public class ImageOverlay implements Decorator<BufferedImage> {
  public static final Float DEFAULT_OVERLAY_TRANSPARENCY     = 1f;
  public static final Float DEFAULT_OVERLAY_TO_QRCODE_RATIO  = 0.25f;

  private final BufferedImage overlay;
  private final Float overlayToQRCodeRatio;
  private final Float overlayTransparency;

  /** The scaled overlay is a pure function of the target size, so it is worth keeping. */
  private record ScaledOverlay(int width, int height, BufferedImage image) {}

  /**
   * Volatile rather than synchronized: a decorator can be reused across qrcodes of
   * different sizes, and the worst a race can do is scale the same overlay twice.
   */
  private volatile ScaledOverlay scaledOverlay;

  /**
   * @param overlay - the image to be over rendered on top of the qrcode
   *
   *
   * @param overlayTransparency - the overlays transparency from 0..1 where one is no transparency.
   *                              Default is set to 1
   *
   *
   * @param overlayToQRCodeRatio - Specifies the ratio between the overlay image and the QRCode in percentage like 0.20 = 20%.
   *                               Overlays should as a guide not take up more 25% of the QRCode or else the readability of the code could be compromised
   *                               Default is set to 25%
   *
   * It's safe to add a overlay that takes up more than 25%, if the transparency is less than .20
   *
   * @throws IllegalArgumentException - if the overlay is null
   */
  public static Decorator<BufferedImage> addImageOverlay(BufferedImage overlay, Float overlayTransparency, Float overlayToQRCodeRatio){
    return new ImageOverlay(overlay, overlayTransparency, overlayToQRCodeRatio);
  }

  private ImageOverlay(BufferedImage overlay, Float overlayTransparency, Float overlayToQRCodeRatio){
    if(overlay == null) {
      throw new IllegalArgumentException("Overlay is required");
    }

    this.overlay = overlay;
    this.overlayTransparency = overlayTransparency == null ? DEFAULT_OVERLAY_TRANSPARENCY : overlayTransparency;
    this.overlayToQRCodeRatio = overlayToQRCodeRatio == null ? DEFAULT_OVERLAY_TO_QRCODE_RATIO : overlayToQRCodeRatio;
  }

  public BufferedImage decorate(BufferedImage qrcode) {
    BufferedImage scaledOverlay = scaleOverlay(qrcode);

    int deltaHeight = qrcode.getHeight() - scaledOverlay.getHeight();
    int deltaWidth  = qrcode.getWidth()  - scaledOverlay.getWidth();

    // TYPE_INT_RGB, not ARGB: the composite over an opaque qrcode is itself opaque,
    // and an alpha channel makes ImageIO.write silently fail for formats such as JPEG.
    // The scaled overlay below keeps its alpha so transparent pixels still blend.
    var combined = new BufferedImage(qrcode.getWidth(), qrcode.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = combined.createGraphics();
    g2.drawImage(qrcode, 0, 0, null);
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayTransparency));
    g2.drawImage(scaledOverlay, deltaWidth / 2, deltaHeight / 2, null);
    g2.dispose();

    return combined;
  }

//-----------------
// private methods
//-----------------

  private BufferedImage scaleOverlay(BufferedImage qrcode){
    int scaledWidth = Math.round(qrcode.getWidth() * overlayToQRCodeRatio);
    int scaledHeight = Math.round(qrcode.getHeight() * overlayToQRCodeRatio);

    // Keyed on the target size, not just cached: the same decorator may be handed
    // qrcodes of different sizes, and each needs an overlay scaled to match.
    ScaledOverlay cached = scaledOverlay;
    if (cached != null && cached.width() == scaledWidth && cached.height() == scaledHeight) {
      return cached.image();
    }

    var scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = scaled.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.drawImage(overlay, 0, 0, scaledWidth, scaledHeight, null);
    g.dispose();

    // Only ever drawn from, never drawn into, so sharing the instance is safe.
    scaledOverlay = new ScaledOverlay(scaledWidth, scaledHeight, scaled);

    return scaled;
  }
}
