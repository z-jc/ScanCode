package com.sqy.scancode.util;

public class Bytes {

    /**
     * 读取温湿度指令
     */
    public static String getWSD() {
        byte[] mByte = {0x7E, 0x01, 0x00, 0x00};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * 读取电池电压
     */
    public static String getDCDY() {
        byte[] mByte = {0x7E, 0x02, 0x00, 0x00};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 锁头状态中断
     */
    public static String getSTZT(byte bytes) {
        byte[] mByte = {0x7E, 0x03, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 箱盖
     */
    public static String getXG(byte bytes) {
        byte[] mByte = {0x7E, 0x04, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 锁头控制
     */
    public static String getST(byte bytes) {
        byte[] mByte = {0x7E, (byte) 0x83, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 报警器控制
     */
    public static String getBJQ(byte bytes) {
        byte[] mByte = {0x7E, (byte) 0x85, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 自毁控制
     */
    public static String getZH(byte bytes) {
        byte[] mByte = {0x7E, (byte) 0x86, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

    /**
     * @Params: bytes
     * 明文加密
     */
    public static String getMWJM(byte bytes) {
        byte[] mByte = {0x7E, 0x41, 0x00, 0x01, bytes};
        return ByteUtil.getSum16(mByte, mByte.length);
    }

}