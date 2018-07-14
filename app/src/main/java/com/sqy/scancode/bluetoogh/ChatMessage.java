package com.sqy.scancode.bluetoogh;

/*
 *
 * @author   liaohongfei
 *
 * @Date  2015 2015-1-27  上午9:50:42
 *
 */
public class ChatMessage {

    private String message;
    private boolean isSiri;

    public ChatMessage(String message, boolean siri) {
        this.message = message;
        this.isSiri = siri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSiri() {
        return isSiri;
    }

    public void setSiri(boolean isSiri) {
        this.isSiri = isSiri;
    }
}