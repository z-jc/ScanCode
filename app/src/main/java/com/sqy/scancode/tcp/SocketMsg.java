package com.sqy.scancode.tcp;

public class SocketMsg {
    public String strData;
    public SocketMsg(String data){
        this.strData = data;
    }

    public String getStrData() {
        return strData;
    }
}