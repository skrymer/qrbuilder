package com.skrymer.qrbuilder;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.skrymer.qrbuilder.exception.CouldNotCreateQRCodeException;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;

public class ZXingQRCodeBuilder {
  private static final Float DEFAULT_OVERLAY_TRANSPARENCY     = 1f;
  private static final Float DEFAULT_OVERLAY_TO_QRCODE_RATIO  = 0.25f;
  
  private String data;
  private Integer width, height;
  private BufferedImage overlay;
  private Float overlayToQRCodeRatioPercentage, overlayTransparencyValue;
  
  /**
   * Syntactic sugar - qrbuilder.newQRCode().with....
   * @return
   */
  public ZXingQRCodeBuilder newQRCode(){
	  return this;
  }
  
  /**
   * Syntactic sugar - qrbuilder.newQRCode().with().and().with()
   * @return
   */
  public ZXingQRCodeBuilder and(){
	  return this;
  }
  
  /**
   * @param overlay - Specifies a image overlay
   * @return
   *    this
   */
  public ZXingQRCodeBuilder withImageOverlay(BufferedImage overlay) {
    this.overlay = overlay;
    
    return this;
  }

  /**
   * 
   * @param percentage - Specifies the ratio between the overlay image and the QRCode in percentage like 0.20 = 20%. 
   *                     Overlays should as a guide not take up more 25% of the QRCode or else the readability of the code could be compromised
   *                     Default is set to 25%
   * @return
   */
  public ZXingQRCodeBuilder withOverlayRatio(Float percentage){
    this.overlayToQRCodeRatioPercentage = percentage;

    return this;
  }
  
  /**
   * Sets the overlays transparency. 
   * 
   * @param value
   *    the overlays transparency from 0..1 where one is no transparency. 
   *    Default is set to 1
   * @return
   */
  public ZXingQRCodeBuilder withOverlayTransparencyOf(Float value){
    this.overlayTransparencyValue = value;
    
    return this;
  }
  
  /**
   * Adds the specified string to the QRCode
   * 
   * @param data
   * @return
   */
  public ZXingQRCodeBuilder withData(String data) {
    this.data = data;
    
    return this;
  }
  
  /**
   * Sets the size for the generated QRCode
   * @param expectedImageWidht
   * @param expectedImageHeight
   * @throws InvalidSizeException
   *    If width or height <= 0 a InvalidSizeException is thrown
   * @return
   *    this
   */
  public ZXingQRCodeBuilder withSize(Integer width, Integer height) {
    validateSize(width, height);
    
    this.width = width;
    this.height = height;
    
    return this;
  }

  /**
   * Creates the new QRCode image with the specified attributes
   * 
   * @throws CouldNotCreateQRCodeException
   *    If the QRCode could not be generated a CouldNotCreateQRCodeException is thrown
   * @throws UnreadableDataException
   *    If the data contained in the QRCode is not the same as the data specified or the data is unreadable 
   * @return
   *    The new QRCode image
   */    
  public BufferedImage create() throws CouldNotCreateQRCodeException, UnreadableDataException {
    BufferedImage qrcode = null;
    
    try {          
      BitMatrix matrix = new QRCodeWriter().encode(data,BarcodeFormat.QR_CODE, this.width, this.height, createHints());
      
      if(overlay != null){
        qrcode = createQRCodeWithOverlay(MatrixToImageWriter.toBufferedImage(matrix));
      }
      else{
        qrcode = MatrixToImageWriter.toBufferedImage(matrix);
      }
    } 
    catch (Exception e) {
      throw new CouldNotCreateQRCodeException("QRCode could not be generated", e.getCause());
    }
    
    verifyQRCode(qrcode);
    
    return qrcode;
  }

//--------------------
// private methods
//--------------------
  
  private BufferedImage createQRCodeWithOverlay(BufferedImage qrcode) {
    BufferedImage scaledOverlay = scaleOverlay(overlay);
    
    Integer deltaHeight = qrcode.getHeight() - scaledOverlay.getHeight();
    Integer deltaWidth  = qrcode.getWidth()  - scaledOverlay.getWidth();
     
    BufferedImage combined = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D)combined.getGraphics();
    g2.drawImage(qrcode, 0, 0, null);
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOverlayTransparency()));
    g2.drawImage(scaledOverlay, (int)Math.round(deltaWidth/2), (int)Math.round(deltaHeight/2), null);
    
    return combined;
  }
  
  private Float getOverlayTransparency() {
    if(this.overlayTransparencyValue == null)
      return DEFAULT_OVERLAY_TRANSPARENCY;
    
    return this.overlayTransparencyValue;
  }

  private BufferedImage scaleOverlay(BufferedImage overlay){
    Float scaleRatio = getScaleRatio(); 
    
    Integer scaledWidth = (int) Math.round(this.width * scaleRatio);
    Integer scaledHeight = (int) Math.round(this.height * scaleRatio);
    
    BufferedImage imageBuff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics g = imageBuff.createGraphics();
    g.drawImage(overlay.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH), 0, 0, new Color(0,0,0), null);
    g.dispose();
    
    return imageBuff;
  }

  private Float getScaleRatio() {
    if(overlayToQRCodeRatioPercentage == null)
      return DEFAULT_OVERLAY_TO_QRCODE_RATIO;
    
    return overlayToQRCodeRatioPercentage;
  }

  private Map<EncodeHintType, ErrorCorrectionLevel> createHints() {
    Map<EncodeHintType,  ErrorCorrectionLevel> hints = new HashMap<EncodeHintType,  ErrorCorrectionLevel>();
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

    return hints;
  }

  private void validateSize(Integer width, Integer height) {
    if(width <= 0) 
    	throw new InvalidSizeException("Width is to small should be > 0 is " + width);
    
    if(height <= 0) 
    	throw new InvalidSizeException("Height is to small should be > 0 is " + height);
  }
  
  private void verifyQRCode(BufferedImage qrcode){
    try{
      BinaryBitmap binaryBitmap = new BinaryBitmap(
                                    new HybridBinarizer(
                                      new BufferedImageLuminanceSource(qrcode)));
      
      Result readData = new MultiFormatReader().decode(binaryBitmap);
      
      if(! readData.getText().equals(this.data))
        throw new UnreadableDataException("The data contained in the qrcode is not the same as the given value");
    }
    catch(NotFoundException nfe){
      throw new UnreadableDataException("The data contained in the qrcode is not readable", nfe);
    }
  }
}
