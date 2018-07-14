package com.sqy.scancode.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2018/6/15.
 */

public class ByteUtil {

    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 向串口发送数据转为字节数组
     */
    public static byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];
        byte temp;
        for (int p = 0; p < bytes.length; p++) {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
            temp += digital.indexOf(hex2char[2 * p + 1]);
            bytes[p] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    /**
     * 接收到的字节数组转换16进制字符串
     */
    public static String bytes2HexString(byte[] b,int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 接收到的字节数组转换16进制字符串
     */
    public static String byteToStr(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 将图片转换为base64加密数据
     */
    public static String getBase64(String imgFile) {
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 计算CRC16校验码
     * 逐个求和
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC_16(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        if (Integer.toHexString(CRC).toUpperCase().length() == 2) {
            return byteToStr(bytes, bytes.length) + "00" + Integer.toHexString(CRC).toUpperCase();
        } else if (Integer.toHexString(CRC).toUpperCase().length() == 3) {
            return byteToStr(bytes, bytes.length) + "0" + Integer.toHexString(CRC).toUpperCase();
        }
        return byteToStr(bytes, bytes.length) + Integer.toHexString(CRC).toUpperCase();
    }

    public static String getSum16(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */
        return byteToStr(msg, length) + byteToStr(mByte, mByte.length).substring(byteToStr(mByte, mByte.length).length() - 4, byteToStr(mByte, mByte.length).length());
    }

}