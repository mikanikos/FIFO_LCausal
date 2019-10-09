import java.util.Objects;

public class MessageData {

    private int senderID;
    private int messageID;
    private boolean isAck;

    public int getSenderID() {
        return senderID;
    }

    public int getMessageID() {
        return messageID;
    }

    public boolean isAck() {
        return isAck;
    }

    public MessageData(int senderID, int messageID, boolean isAck) {
        this.senderID = senderID;
        this.messageID = messageID;
        this.isAck = isAck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageData that = (MessageData) o;
        return senderID == that.senderID &&
                messageID == that.messageID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderID, messageID);
    }

    @Override
    public String toString() {
        return senderID + " " + messageID + " " + isAck;
    }
}
