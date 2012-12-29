qrbuilder
=========

The builder is very simple to use as the following example will show.

Create a qrcode with dimensions 250*250, a image overlay and some data:

```java
BufferedImage qrcode = sut.newQRCode()
                          .withSize(250, 250)
                            .and()  
                          .withData("SOME DATA")
                            .and()
                          .withImageOverlay(ImageIO.read(new File("/path/to/overlay/overlay.png")))
                            .and()
                          .withOverlayRatio(0.25f)
                            .and()
                          .withOverlayTransparency(1.0f)
                          .create();
```
