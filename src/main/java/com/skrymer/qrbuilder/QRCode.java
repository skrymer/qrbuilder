package com.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import com.skrymer.qrbuilder.exception.CouldNotCreateFileException;
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
  private Charset charSet;

  private QRCode(ZXingBuilder builder) {
    data = builder.data;
    verify = builder.verify;
    width = builder.width;
    height = builder.height;
    decorators = builder.decorators;
    charSet = builder.charSet;
  }

  public BufferedImage toImage() throws CouldNotCreateQRCodeException, UnreadableDataException {
    BufferedImage qrcode = decorate(encode());
    verifyQRCode(qrcode);

    return qrcode;
  }

  public File toFile(String fileName, String fileFormat) throws CouldNotCreateQRCodeException, UnreadableDataException {
    throwIllegalArgumentExceptionIfEmpty(fileName, "fileName");
    throwIllegalArgumentExceptionIfEmpty(fileFormat, "fileFormat");

    try {
      File imageFile = new File(fileName);
      ImageIO.write(toImage(), fileFormat, imageFile);
      return imageFile;
    } catch (IOException e) {
      throw new CouldNotCreateFileException("Could not create file", e);
    }
  }

//--------------------
// private methods
//--------------------

  private void verifyQRCode(BufferedImage qrCode) {
    if (!verify) {
      return;
    }

    try {
      Result actualData = decode(qrCode);

      if (actualData != null && !actualData.getText().equals(this.data)) {
        throw new UnreadableDataException("The data contained in the qrCode is not as expected: " + this.data + " actual: " + actualData);
      }
    } catch (Exception e) {
      throw new UnreadableDataException("Verifying qr code failed!", e);
    }
  }

  private Result decode(BufferedImage qrcode) throws FormatException, ChecksumException, NotFoundException {
    return new QRCodeReader().decode(new BinaryBitmap(
            new HybridBinarizer(
                new BufferedImageLuminanceSource(qrcode))
        ), getDecodeHints()
    );
  }

  private BufferedImage encode() {
    try {
      BitMatrix matrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, this.width, this.height, getEncodeHints());
      return MatrixToImageWriter.toBufferedImage(matrix);
    } catch (Exception e) {
      throw new CouldNotCreateQRCodeException("QRCode could not be generated", e);
    }
  }

  private Map<EncodeHintType, Object> getEncodeHints() {
    Map<EncodeHintType, Object> encodeHints = new HashMap<>();
    encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    encodeHints.put(EncodeHintType.CHARACTER_SET, this.charSet.name());
    return encodeHints;
  }

  private Map<DecodeHintType, Object> getDecodeHints() {
    Map<DecodeHintType, Object> decodeHints = new HashMap<>();
    decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
    decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    decodeHints.put(DecodeHintType.CHARACTER_SET, this.charSet.name());
    return decodeHints;
  }

  private BufferedImage decorate(BufferedImage qrCodeImage) {
    for(Decorator<BufferedImage> decorator : decorators){
      qrCodeImage = decorator.decorate(qrCodeImage);
    }
    return qrCodeImage;
  }

//-------------------
// Builder
//-------------------

  public static class ZXingBuilder {
    private String data;
    private boolean verify;
    private int width, height;
    private Charset charSet;
    private List<Decorator<BufferedImage>> decorators;

    private ZXingBuilder(){
      verify = true;
      charSet = Charset.defaultCharset();
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

    public ZXingBuilder verify(Boolean doVerify) {
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

    /**
     * Defaults to the jvm default char set
     * @param charSet
     * @return
     */
    public ZXingBuilder withCharSet(Charset charSet){
      this.charSet = charSet;
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
}
