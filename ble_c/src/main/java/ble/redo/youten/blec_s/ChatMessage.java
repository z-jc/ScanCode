package ble.redo.youten.blec_s;

/*
 *  这里定义一个类，用来保存我们发送的数据信息...
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