/*******************************************************************************
 * Copyright (C) 2009-2011 Amir Hassan <amir@viel-zu.org>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

package org.wooden.image;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageToolkit {

  private static Container observer = new Container();

  public static int[] average(int pixel1[], int pixel2[], int preAlloc[]) {
    if (preAlloc == null)
      preAlloc = new int[3];
    preAlloc[0] = (pixel1[0] + pixel2[0]) / 2;
    preAlloc[1] = (pixel1[1] + pixel2[1]) / 2;
    preAlloc[2] = (pixel1[2] + pixel2[2]) / 2;
    return preAlloc;
  }

  public static int calculateContrast(int r1, int g1, int b1, int r2, int g2,
      int b2) {
    return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
  }

  public static int[][] colorVariantsToBase(int alpha, int red, int green,
      int blue, int base, int preAlloc[][]) {
    int channels[] = { red, green, blue };
    for (int i = 0; i < channels.length; i++) {
      int modulo = channels[i] % base;
      if (blue - modulo >= 0)
        channels[i] -= modulo;
      else
        channels[i] += base - modulo;
    }

    for (int i = 0; i < channels.length; i++) {
      int encColor = encodeColorInfo(alpha, channels[0], channels[1],
          channels[2]);
      preAlloc[i] = decodeColorInfo(encColor, preAlloc[i]);
    }

    return preAlloc;
  }

  public static BufferedImage createBufferedImagePixels(int w, int h,
      int pixels[]) {
    System.out.println("createBufferedImage");
    BufferedImage image = new BufferedImage(w, h, 1);
    image.getRaster().setPixels(0, 0, w, h, pixels);
    return image;
  }

  public static BufferedImage createBufferedImageRGB(int w, int h, int rgb[],
      int rgbType) {
    System.out.println("createBufferedImage");
    BufferedImage image = new BufferedImage(w, h, rgbType);
    image.setRGB(0, 0, w, h, rgb, 0, w);
    return image;
  }

  public static byte[] createJpegData(BufferedImage img) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
    param.setQuality(1.0F, false);
    encoder.setJPEGEncodeParam(param);
    encoder.encode(img);
    return out.toByteArray();
  }

  public static BufferedImage createJpegImage(byte data[]) throws IOException {
    return readJpegImage(new ByteArrayInputStream(data));
  }

  public static int[] decodeColorInfo(int rgb) {
    return decodeColorInfo(rgb, new int[3]);
  }

  public static int[] decodeColorInfo(int rgb, int preAlloc[]) {
    preAlloc[0] = rgb >>> 24 & 0xff;
    preAlloc[1] = rgb >>> 16 & 0xff;
    preAlloc[2] = rgb >>> 8 & 0xff;
    preAlloc[3] = rgb >>> 0 & 0xff;
    return preAlloc;
  }

  public static int[] decodeDataPixels(InputStream inSrc) throws IOException {
    DataInputStream in;
    if (inSrc instanceof DataInputStream)
      in = (DataInputStream) inSrc;
    else
      in = new DataInputStream(inSrc);
    int r = 0;
    int g = 0;
    int b = 0;
    int pixels[] = new int[in.readInt()];
    int onepercent = pixels.length / 100;
    short pixelCount = 0;
    for (int i = 0; i + 1 < pixels.length;) {
      pixelCount = 1;
      int curr = in.read();
      if (curr == 0) {
        pixelCount = (short) (in.readShort() + 1);
        curr = in.read();
      }
      r = curr;
      g = in.read();
      b = in.read();
      for (int j = 0; j < pixelCount; j++) {
        pixels[i] = r;
        pixels[i + 1] = g;
        pixels[i + 2] = b;
        i += 3;
      }

    }

    in.close();
    return pixels;
  }

  public static int[] decodeDataRGB(InputStream inSrc) throws IOException {
    DataInputStream in;
    if (inSrc instanceof DataInputStream)
      in = (DataInputStream) inSrc;
    else
      in = new DataInputStream(inSrc);
    int r = 0;
    int g = 0;
    int b = 0;
    int arrRGB[] = new int[in.readInt()];
    int onepercent = arrRGB.length / 100;
    int rgb = 0;
    short pixelCount = 0;
    for (int i = 0; i + 1 < arrRGB.length;) {
      rgb = 0;
      pixelCount = 1;
      int curr = in.read();
      if (curr == 0) {
        pixelCount = (short) (in.readShort() + 1);
        curr = in.read();
      }
      r = curr;
      g = in.read();
      b = in.read();
      rgb = encodeColorInfo(255, r, g, b);
      for (int j = 0; j < pixelCount; j++) {
        arrRGB[i] = rgb;
        i++;
      }

    }

    in.close();
    return arrRGB;
  }

  public static int encodeColorInfo(int rgbColor[]) {
    return encodeColorInfo(rgbColor[0], rgbColor[1], rgbColor[2], rgbColor[3]);
  }

  public static int encodeColorInfo(int alpha, int red, int green, int blue) {
    return (alpha << 24) + (red << 16) + (green << 8) + (blue << 0);
  }

  public static void encodeDataPixels(int pixels[], int level,
      OutputStream outDest) throws IOException {
    DataOutputStream out;
    if (outDest instanceof DataOutputStream)
      out = (DataOutputStream) outDest;
    else
      out = new DataOutputStream(outDest);
    short pixelCount = 0;
    int r = 0;
    int g = 0;
    int b = 0;
    int avgR = 0;
    int avgG = 0;
    int avgB = 0;
    int dec[] = null;
    int onepercent = pixels.length / 100;
    out.writeInt(pixels.length);
    for (int i = 0; i < pixels.length; i += 3) {
      int currR = pixels[i];
      int currG = pixels[i + 1];
      int currB = pixels[i + 2];
      if (i == 0) {
        avgR = r = currR;
        avgG = g = currG;
        avgB = b = currB;
      }
      int diff = calculateContrast(r, g, b, currR, currG, currB);
      if (diff > level || pixelCount > 32765 || i == pixels.length - 1) {
        if (pixelCount > 0) {
          out.write(0);
          out.writeShort(pixelCount);
        }
        out.write(avgR);
        out.write(avgG);
        out.write(avgB);
        avgR = r = currR;
        avgG = g = currG;
        avgB = b = currB;
        pixelCount = 0;
      } else {
        avgR = (avgR + currR) / 2;
        avgG = (avgG + currG) / 2;
        avgB = (avgB + currB) / 2;
        pixelCount++;
      }
    }

    out.close();
  }

  public static void encodeDataRGB(int rgb[], int level, OutputStream outDest)
      throws IOException {
    DataOutputStream out;
    if (outDest instanceof DataOutputStream)
      out = (DataOutputStream) outDest;
    else
      out = new DataOutputStream(outDest);
    short pixelCount = 0;
    int r = 0;
    int g = 0;
    int b = 0;
    int avgR = 0;
    int avgG = 0;
    int avgB = 0;
    int dec[] = null;
    int onepercent = rgb.length / 100;
    out.writeInt(rgb.length);
    for (int i = 0; i < rgb.length; i++) {
      dec = decodeColorInfo(rgb[i]);
      for (int j = 1; j < dec.length; j++)
        if (dec[j] == 0)
          dec[j]++;

      int currR = dec[1];
      int currG = dec[2];
      int currB = dec[3];
      if (i == 0) {
        avgR = r = currR;
        avgG = g = currG;
        avgB = b = currB;
      }
      int diff = calculateContrast(r, g, b, currR, currG, currB);
      if (diff > level || pixelCount > 32765 || i == rgb.length - 1) {
        if (pixelCount > 0) {
          out.write(0);
          out.writeShort(pixelCount);
        }
        out.write(avgR);
        out.write(avgG);
        out.write(avgB);
        avgR = r = currR;
        avgG = g = currG;
        avgB = b = currB;
        pixelCount = 0;
      } else {
        avgR = (avgR + currR) / 2;
        avgG = (avgG + currG) / 2;
        avgB = (avgB + currB) / 2;
        pixelCount++;
      }
    }

    out.close();
  }

  public static int[] getDataRGB(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    return image.getRGB(0, 0, w, h, new int[w * h], 0, w);
  }

  public static void loadImageChecked(Image img) {
    loadImageChecked(img, ((observer)));
  }

  public static void loadImageChecked(Image img, Component observer) {
    int status;
    for (int failure = 0x80 | 0x40; ((status = observer.checkImage(img,
        observer)) & failure) == 0;) {
      System.out.println((new StringBuilder(String.valueOf(img
          .getWidth(observer)))).append(" ").append(status & 1).append("/")
          .append(img.getHeight(observer)).append(" ").append(status & 2)
          .toString());
      if ((status & 1) > 0 && (status & 2) > 0) {
        observer.prepareImage(img, observer);
        return;
      }
      try {
        Thread.sleep(100L);
      } catch (InterruptedException interruptedexception) {}
    }

    String strStatus = "ABORT";
    if ((status & 0x40) != 0)
      strStatus = "ERROR";
    throw new RasterFormatException((new StringBuilder(
        "Illegal image Format (checkImage: ")).append(strStatus).append(")")
        .toString());
  }

  public static int[] readDataRGB(File source) throws IOException {
    long size = source.length();
    if (size % 4L > 0L)
      System.err.println("Invalid file size");
    int rgb[] = new int[(int) (size / 4L)];
    DataInputStream in = new DataInputStream(new FileInputStream(source));
    for (int i = 0; i < rgb.length; i++)
      rgb[i] = in.readInt();

    in.close();
    return rgb;
  }

  public static BufferedImage readJpegImage(InputStream in) throws IOException {
    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
    return decoder.decodeAsBufferedImage();
  }

  public static BufferedImage readPngImage(File f) throws IOException {
    Image img = observer.getToolkit().getImage(f.getCanonicalPath());
    MediaTracker mt = new MediaTracker(observer);
    mt.addImage(img, 0);
    try {
      mt.waitForAll();
    } catch (InterruptedException interruptedexception) {}
    BufferedImage image = new BufferedImage(img.getWidth(observer),
        img.getHeight(observer), 1);
    image.getGraphics().drawImage(img, 0, 0, observer);
    return image;
  }

  public static void writeDataRGB(int rgb[], File dest) throws IOException {
    DataOutputStream out = new DataOutputStream(new FileOutputStream(dest));
    for (int element : rgb)
      out.writeInt(element);

    out.close();
  }

  public static void writeJpepImage(BufferedImage img, OutputStream out)
      throws IOException {
    out.write(createJpegData(img));
    out.close();
  }

  private ImageToolkit() {}

}
