package g20.homework8;

import java.util.Calendar;

public class MessageObject {

    private Calendar timeStamp;
    private String text;
    private String senderUid;
    private String receiverUar;

    public MessageObject() {
    }

    public MessageObject(Calendar timeStamp, String text, String senderUid, String receiverUar) {
        this.timeStamp = timeStamp;
        this.text = text;
        this.senderUid = senderUid;
        this.receiverUar = receiverUar;
    }

    public String getReceiverUar() {
        return receiverUar;
    }

    public void setReceiverUar(String receiverUar) {
        this.receiverUar = receiverUar;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "timeStamp=" + timeStamp +
                ", text='" + text + '\'' +
                ", senderUid='" + senderUid + '\'' +
                ", receiverUar='" + receiverUar + '\'' +
                '}';
    }
}
