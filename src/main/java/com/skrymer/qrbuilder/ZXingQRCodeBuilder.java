package com.skrymer.qrbuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

import javax.imageio.ImageIO;

import static com.skrymer.qrbuilder.util.SyntacticSugar.throwIllegalArgumentExceptionIfEmpty;

/**
 * QRCBuilder implementation using the ZXing library to generate a qrCode as a  BufferedImage
 *
 * see https://github.com/zxing/zxing
 */
public class ZXingQRCodeBuilder implements QRCBuilder<BufferedImage> {
    private String data;
    private Boolean verify;
    private Integer width, height;
    private List<QRCodeDecorator<BufferedImage>> decorators;

    public ZXingQRCodeBuilder() {
        verify = true;
    }

    public QRCBuilder newQRCode() {
        return this;
    }

    public QRCBuilder and() {
        return this;
    }

    public QRCBuilder doVerify(Boolean doVerify) {
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
        if (decorators == null) {
            decorators = new LinkedList<QRCodeDecorator<BufferedImage>>();
        }

        decorators.add(decorator);

        return this;
    }

    public BufferedImage toImage() throws CouldNotCreateQRCodeException, UnreadableDataException {
        BufferedImage qrcode = encode();
        qrcode = decorate(qrcode);
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

//--------------------
// private methods
//--------------------

    private void validateSize(Integer width, Integer height) {
        if (width <= 0) {
            throw new InvalidSizeException("Width is to small should be > 0 is " + width);
        }

        if (height <= 0) {
            throw new InvalidSizeException("Height is to small should be > 0 is " + height);
        }
    }

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
        if (decorators != null) {
            for (QRCodeDecorator<BufferedImage> decorator : decorators) {
                qrcode = decorator.decorate(qrcode);
            }
        }

        return qrcode;
    }
}
