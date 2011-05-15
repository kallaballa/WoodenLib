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

package org.wooden.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.NoSuchPaddingException;

import org.wooden.encryption.DesEncryption;

public class StringTool {
  public static final class XML {

    private static final int CDATA_BLOCK_THRESHOLD_LENGTH = 12;

    private static final char DEFAULT_QUOTE_CHAR = 34;

    private static final boolean contains(String text, char chars[]) {
      if (text == null || chars == null || chars.length == 0)
        return false;
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        for (char d : chars)
          if (d == c)
            return true;

      }

      return false;
    }

    public static final boolean isCompatibleWithCDATABlock(String text) {
      if (text == null)
        return false;
      return text.indexOf("]]>") == -1;
    }

    public static final boolean isWhiteSpace(String text) {
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (!Character.isWhitespace(c))
          return false;
      }

      return true;
    }

    private static final char lookAhead(int la, int offset, String data) {
      try {
        return data.charAt(offset + la);
      } catch (StringIndexOutOfBoundsException e) {
        return '\0';
      }
    }

    public static final boolean needsEncoding(String text) {
      return needsEncoding(text, false);
    }

    public static final boolean needsEncoding(String data, boolean checkForAttr) {
      if (data == null)
        return false;
      for (int i = 0; i < data.length(); i++) {
        char c = data.charAt(i);
        if (c == '&' || c == '<' || checkForAttr && (c == '"' || c == '\''))
          return true;
      }

      return false;
    }

    public static final String xmlDecodeTextToCDATA(String pcdata) {
      if (pcdata == null)
        return null;
      StringBuffer n = new StringBuffer(pcdata.length());
      for (int i = 0; i < pcdata.length(); i++) {
        char c = pcdata.charAt(i);
        if (c == '&') {
          char c1 = lookAhead(1, i, pcdata);
          char c2 = lookAhead(2, i, pcdata);
          char c3 = lookAhead(3, i, pcdata);
          char c4 = lookAhead(4, i, pcdata);
          char c5 = lookAhead(5, i, pcdata);
          if (c1 == 'a' && c2 == 'm' && c3 == 'p' && c4 == ';') {
            n.append("&");
            i += 4;
          } else if (c1 == 'l' && c2 == 't' && c3 == ';') {
            n.append("<");
            i += 3;
          } else if (c1 == 'g' && c2 == 't' && c3 == ';') {
            n.append(">");
            i += 3;
          } else if (c1 == 'q' && c2 == 'u' && c3 == 'o' && c4 == 't'
              && c5 == ';') {
            n.append("\"");
            i += 5;
          } else if (c1 == 'a' && c2 == 'p' && c3 == 'o' && c4 == 's'
              && c5 == ';') {
            n.append("'");
            i += 5;
          } else {
            n.append("&");
          }
        } else {
          n.append(c);
        }
      }

      return n.toString();
    }

    public static final String xmlEncodeText(String text) {
      if (text == null)
        return null;
      if (!needsEncoding(text))
        return text;
      if (text.length() > 12) {
        String cdata = xmlEncodeTextAsCDATABlock(text);
        if (cdata != null)
          return cdata;
      }
      return xmlEncodeTextAsPCDATA(text);
    }

    public static final String xmlEncodeTextAsCDATABlock(String text) {
      if (text == null)
        return null;
      if (isCompatibleWithCDATABlock(text))
        return (new StringBuilder("<![CDATA[")).append(text).append("]]>")
            .toString();
      else
        return null;
    }

    public static final String xmlEncodeTextAsPCDATA(String text) {
      if (text == null)
        return null;
      else
        return xmlEncodeTextAsPCDATA(text, false);
    }

    public static final String xmlEncodeTextAsPCDATA(String text,
        boolean forAttribute) {
      return xmlEncodeTextAsPCDATA(text, forAttribute, '"');
    }

    public static final String xmlEncodeTextAsPCDATA(String text,
        boolean forAttribute, char quoteChar) {
      if (text == null)
        return null;
      StringBuffer n = new StringBuffer(text.length() * 2);
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        switch (c) {
        case 38: // '&'
          n.append("&amp;");
          break;

        case 60: // '<'
          n.append("&lt;");
          break;

        case 62: // '>'
          n.append("&gt;");
          break;

        case 34: // '"'
          if (forAttribute)
            n.append("&quot;");
          else
            n.append(c);
          break;

        case 39: // '\''
          if (forAttribute)
            n.append("&apos;");
          else
            n.append(c);
          break;

        default:
          n.append(c);
          break;
        }
      }

      if (forAttribute) {
        n.append(quoteChar);
        n.insert(0, quoteChar);
      }
      return n.toString();
    }

    public static final String xmlEncodeTextForAttribute(String text,
        char quoteChar) {
      if (text == null)
        return null;
      else
        return xmlEncodeTextAsPCDATA(text, true, quoteChar);
    }

    public XML() {}
  }

  public static String DES(String s, String pass, boolean encrypt)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidAlgorithmParameterException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DesEncryption encrypter = new DesEncryption(pass);
    if (encrypt)
      encrypter.encrypt(new ByteArrayInputStream(s.getBytes()), out);
    else
      encrypter.decrypt(new ByteArrayInputStream(s.getBytes()), out);
    return new String(out.toByteArray());
  }

  public static byte[] digest(String key, String algorithm)
      throws NoSuchAlgorithmException, IOException {
    MessageDigest md = MessageDigest.getInstance(algorithm);
    ByteArrayInputStream in = new ByteArrayInputStream(key.getBytes());
    byte data[] = new byte[1024];
    int len;
    while ((len = in.read(data)) > 0)
      md.update(data, 0, len);
    in.close();
    return md.digest();
  }

  public static String eliminateBlank(String mayBeBlank) {
    return mayBeBlank == null || mayBeBlank.trim().length() != 0 ? mayBeBlank
        : null;
  }

  public static String eliminateNull(String mayBeNull) {
    return mayBeNull != null ? mayBeNull : "";
  }

  public static byte[] md5(String key) throws NoSuchAlgorithmException,
      IOException {
    return digest(key, "MD5");
  }

  public static String toHexString(byte b) {
    int value = (b & 0x7f) + (b >= 0 ? 0 : 128);
    String ret = value >= 16 ? "" : "0";
    ret = (new StringBuilder(String.valueOf(ret))).append(
        Integer.toHexString(value).toUpperCase()).toString();
    return ret;
  }

  public static String toHexString(byte arrBytes[])
      throws NoSuchAlgorithmException, IOException {
    StringBuffer digest = new StringBuffer();
    for (byte arrByte : arrBytes)
      digest.append(toHexString(arrByte));

    return digest.toString();
  }

  public static final String[] tokenize(String s, String delim) {
    Vector tmp = new Vector();
    for (StringTokenizer st = new StringTokenizer(s, delim); st.hasMoreTokens(); tmp
        .add(st.nextToken()))
      ;
    return (String[]) tmp.toArray(new String[0]);
  }

  public StringTool() {}
}
