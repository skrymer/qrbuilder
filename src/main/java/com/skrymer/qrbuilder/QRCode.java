package com.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.skrymer.qrbuilder.decorator.Decorator;
import com.skrymer.qrbuilder.exception.CouldNotCreateQRCodeException;
import com.skrymer.qrbuilder.exception.InvalidSizeException;
import com.skrymer.qrbuilder.exception.UnreadableDataException;

import javax.imageio.ImageIO;

import static com.skrymer.qrbuilder.util.SyntacticSugar.throwIllegalArgumentExceptionIfEmpty;

/**
 * QRCode using the ZXing library to generate images and files
 * <p>
 * see https://github.com/zxing/zxing
 */
public class QRCode {
  private String data;
  private boolean verify;
  private int width, height;
  private List<Decorator<BufferedImage>> decorators;

  private QRCode(ZXingBuilder builder) {
    data = builder.data;
    verify = builder.verify;
    width = builder.width;
    height = builder.height;
    decorators = builder.decorators;
  }

  public BufferedImage toImage() throws CouldNotCreateQRCodeException, UnreadableDataException {
    BufferedImage qrcode = decorate(encode());
    verifyQRCode(qrcode);

    return qrcode;
  }

  public File toFile(String fileName, String fileFormat) throws CouldNotCreateQRCodeException, UnreadableDataException, IOException {
    throwIllegalArgumentExceptionIfEmpty(fileName, "fileName");
    throwIllegalArgumentExceptionIfEmpty(fileFormat, "fileFormat");

    File imageFile = new File(fileName);
    ImageIO.write(toImage(), fileFormat, imageFile);
    return imageFile;
  }

//-------------------
// Builder
//-------------------

  public static class ZXingBuilder {
    private String data;
    private boolean verify;
    private int width, height;
    private List<Decorator<BufferedImage>> decorators;

    private ZXingBuilder(){
      verify = true;
      decorators = new ArrayList<>();
    }

    public static QRCode build(Consumer<ZXingBuilder> block) {
      ZXingBuilder builder = new ZXingBuilder();
      block.accept(builder);
      return new QRCode(builder);
    }

    public ZXingBuilder and() {
      return this;
    }

    public ZXingBuilder doVerify(Boolean doVerify) {
      this.verify = doVerify;

      return this;
    }

    public ZXingBuilder withData(String data) {
      this.data = data;

      return this;
    }

    public ZXingBuilder withSize(Integer width, Integer height) {
      validateSize(width, height);
      this.width = width;
      this.height = height;
      return this;
    }

    public ZXingBuilder withDecorator(Decorator decorator) {
      decorators.add(decorator);
      return this;
    }

    private void validateSize(Integer width, Integer height) {
      if (width <= 0) {
        throw new InvalidSizeException("Width is to small should be > 0 is " + width);
      }

      if (height <= 0) {
        throw new InvalidSizeException("Height is to small should be > 0 is " + height);
      }
    }
  }

//--------------------
// private methods
//--------------------

  private void verifyQRCode(BufferedImage qrcode) {
    if (!verify) {
      return;
    }

    try {
      Result readData = readData(qrcode);

      if (readData != null && !readData.getText().equals(this.data)) {
        throw new UnreadableDataException("The data contained in the qrCode is not as expected: " + this.data + " actual: " + readData);
      }
    } catch (NotFoundException nfe) {
      throw new UnreadableDataException("The data contained in the qrCode is not readable", nfe);
    } catch (ChecksumException ce) {
      throw new UnreadableDataException("The data contained in the qrCode is not readable", ce);
    } catch (FormatException fe) {
      throw new UnreadableDataException("The data contained in the qrCode is not readable", fe);
    }
  }

  private Result readData(BufferedImage qrcode) throws FormatException, ChecksumException, NotFoundException {
    BinaryBitmap binaryBitmap = new BinaryBitmap(
        new HybridBinarizer(
            new BufferedImageLuminanceSource(qrcode))
    );

    return new QRCodeReader().decode(binaryBitmap, getDecodeHints());
  }

  private Map<EncodeHintType, Object> getEncodeHints() {
    Map<EncodeHintType, Object> encodeHints = new HashMap<EncodeHintType, Object>();
    encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    return encodeHints;
  }

  private Map<DecodeHintType, Object> getDecodeHints() {
    Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
    return decodeHints;
  }

  private BufferedImage encode() {
    BufferedImage qrcode;

    try {
      BitMatrix matrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, this.width, this.height, getEncodeHints());
      qrcode = MatrixToImageWriter.toBufferedImage(matrix);
    } catch (Exception e) {
      throw new CouldNotCreateQRCodeException("QRCode could not be generated", e.getCause());
    }

    return qrcode;
  }

  private BufferedImage decorate(BufferedImage qrcode) {
    for(Decorator<BufferedImage> decorator : decorators){
      qrcode = decorator.decorate(qrcode);
    }

    return qrcode;
  }
}
