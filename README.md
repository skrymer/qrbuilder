qrbuilder
=========

The builder is very simple to use as the following example will show.

Create a qrcode with dimensions 250*250, a image overlay and some data:

```java
BufferedImage qrcode = sut.newQRCode()
                          .withSize(250, 250)
                          .withData("SOME DATA")
                          .withImageOverlay(ImageIO.read(new File("/path/to/overlay/overlay.png")))
                          .withOverlayRatio(0.25f)
                          .withOverlayTransparency(1.0f)
                          .create();```
