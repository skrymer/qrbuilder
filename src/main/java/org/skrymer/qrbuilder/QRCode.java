package org.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
import org.skrymer.qrbuilder.decorator.Decorator;
import org.skrymer.qrbuilder.exception.CouldNotCreateFileException;
import org.skrymer.qrbuilder.exception.CouldNotCreateQRCodeException;
import org.skrymer.qrbuilder.exception.InvalidSizeException;
import org.skrymer.qrbuilder.exception.UnreadableDataException;
import org.skrymer.qrbuilder.util.SyntacticSugar;

import javax.imageio.ImageIO;

import static org.skrymer.qrbuilder.util.SyntacticSugar.throwIf;
import static org.skrymer.qrbuilder.util.SyntacticSugar.throwIllegalArgumentExceptionIfEmpty;

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

  /**
   * Use builder
   * @param builder
   */
  private QRCode(Builder builder) {
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

      if (!Objects.equals(actualData.getText(), this.data)) {
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

  public static class Builder {
    private String data;
    private boolean verify;
    private int width, height;
    private Charset charSet;
    private List<Decorator<BufferedImage>> decorators;

    private Builder(){
      verify = true;
      charSet = Charset.defaultCharset();
      decorators = new ArrayList<>();
    }

    public static QRCode build(Consumer<Builder> block) {
      Builder builder = new Builder();
      block.accept(builder);
      return new QRCode(builder);
    }

    public Builder and() {
      return this;
    }

    public Builder verify(Boolean doVerify) {
      this.verify = doVerify;
      return this;
    }

    public Builder withData(String data) {
      this.data = data;
      return this;
    }

    public Builder withSize(Integer width, Integer height) {
      validateSize(width, height);
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder withDecorator(Decorator decorator) {
      decorators.add(decorator);
      return this;
    }

    public Builder withDecorators(List<Decorator<BufferedImage>> decorators){
      this.decorators.addAll(decorators);
      return this;
    }

    /**
     * Defaults to the jvm default char set
     * @param charSet
     * @return
     */
    public Builder withCharSet(Charset charSet){
      this.charSet = charSet;
      return this;
    }

    private void validateSize(Integer width, Integer height) {
      throwIf(() -> width <= 0, () -> new InvalidSizeException("Width should be larger than 0 is: " + width));
      throwIf(() -> height <= 0, () -> new InvalidSizeException("Height should be larger than 0 is: " + height));
    }
  }
}
