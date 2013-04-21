package com.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.skrymer.qrbuilder.decorator.QRCodeDecorator;
import com.skrymer.qrbuilder.exception.CouldNotCreateQRCodeException;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;

public class ZXingQRCodeBuilder implements QRCBuilder {

  private String data;
  private Boolean verify;
  private Integer width, height;
  private List<QRCodeDecorator> decorators;

  public ZXingQRCodeBuilder(){
    verify = true;
  }

  public QRCBuilder newQRCode(){
	  return this;
  }
  
  public QRCBuilder and(){
	  return this;
  }

  public QRCBuilder verifyQRCode(Boolean doVerify){
    this.verify = doVerify;

    return this;
  }

  public QRCBuilder withData(String data) {
    this.data = data;
    
    return this;
  }

  public QRCBuilder withSize(Integer width, Integer height) {
    validateSize(width, height);
    
    this.width = width;
    this.height = height;
    
    return this;
  }

  public QRCBuilder decorate(QRCodeDecorator decorator) {
    if(decorators == null){
       decorators = new LinkedList<QRCodeDecorator>();
    }

    decorators.add(decorator);

    return this;
  }

  public BufferedImage create() throws CouldNotCreateQRCodeException, UnreadableDataException {
    BufferedImage qrcode;
    
    try {          
      BitMatrix matrix = new QRCodeWriter().encode(data,BarcodeFormat.QR_CODE, this.width, this.height, getEncodeHints());

      qrcode = MatrixToImageWriter.toBufferedImage(matrix);
    }
    catch (Exception e) {
      throw new CouldNotCreateQRCodeException("QRCode could not be generated", e.getCause());
    }

    if(decorators != null){
      for(QRCodeDecorator decorator : decorators){
        qrcode = decorator.decorate(qrcode);
      }
    }

    if(verify){
      verifyQRCode(qrcode);
    }

    return qrcode;
  }


//--------------------
// private methods
//--------------------

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

      Result readData = new QRCodeReader().decode(binaryBitmap, getDecodeHints());
      
      if(! readData.getText().equals(this.data)){
        throw new UnreadableDataException("The data contained in the qrcode is not the same as the given value");
      }
    }
    catch(NotFoundException nfe){
      throw new UnreadableDataException("The data contained in the qrcode is not readable", nfe);
    }
    catch (ChecksumException ce) {
      throw new UnreadableDataException("The data contained in the qrcode is not readable", ce);
    }
    catch (FormatException fe) {
      throw new UnreadableDataException("The data contained in the qrcode is not readable", fe);
    }
  }

  private Map<EncodeHintType, Object> getEncodeHints() {
    Map<EncodeHintType,  Object> encodeHints = new HashMap<EncodeHintType,  Object>();
    encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    encodeHints.put(EncodeHintType.CHARACTER_SET, "utf-8");

    return encodeHints;
  }

  private Map<DecodeHintType, Object> getDecodeHints(){
    Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    decodeHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
    decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

    return decodeHints;
  }

}
