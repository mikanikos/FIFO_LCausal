import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class MessageData implements Delayed {

    private int sourceID;
    private int senderID;
    private int receiverID;
    private int messageID;
    private boolean isAck;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = System.currentTimeMillis() + time;
    }

    int getSourceID() { return sourceID; }
    int getSenderID() {
        return senderID;
    }
    int getReceiverID() { return receiverID; }
    int getMessageID() {
        return messageID;
    }
    boolean isAck() {
        return isAck;
    }

    MessageData(int sourceID, int senderID, int receiverID, int messageID, boolean isAck) {
        this.sourceID = sourceID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageID = messageID;
        this.isAck = isAck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageData that = (MessageData) o;
        return senderID == that.senderID &&
                messageID == that.messageID &&
                sourceID == that.sourceID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderID, messageID, sourceID);
    }

    @Override
    public String toString() {
        return sourceID  + " " + senderID + " " + receiverID + " "  + messageID + " " + isAck;
    }

    static MessageData parseMessage(String stringMessage) {
        String[] parsedMessage = stringMessage.split(" ");
        int sourceID = Integer.parseInt(parsedMessage[0]);
        int senderID = Integer.parseInt(parsedMessage[1]);
        int receiverID = Integer.parseInt(parsedMessage[2]);
        int seqID = Integer.parseInt(parsedMessage[3]);
        boolean isAck = Boolean.parseBoolean(parsedMessage[4]);

        return new MessageData(sourceID, senderID, receiverID, seqID, isAck);
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        long diff = time - System.currentTimeMillis();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed obj) {
        if (this.time < ((MessageData)obj).time) {
            return -1;
        }
        if (this.time > ((MessageData)obj).time) {
            return 1;
        }
        return 0;
    }
}
