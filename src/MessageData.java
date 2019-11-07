import java.util.Objects;

// Store all message data that are important for the protocols used
public class MessageData {

    // id of the source of the message (creator)
    private int sourceID;
    // id of the sender of the message
    private int senderID;
    // id of the supposed receiver of the message
    private int receiverID;
    // message id
    private int messageID;
    // identify if the message is an ack
    private boolean isAck;


    public int getSourceID() { return sourceID; }

    public int getSenderID() {
        return senderID;
    }

    public int getReceiverID() { return receiverID; }

    public int getMessageID() {
        return messageID;
    }

    public boolean isAck() {
        return isAck;
    }

    public MessageData(int sourceID, int senderID, int receiverID, int messageID, boolean isAck) {
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

    // Parse message: convert from string to MessageData
    public static MessageData parseMessage(String stringMessage) {
        String[] parsedMessage = stringMessage.split(" ");
        int sourceID = Integer.parseInt(parsedMessage[0]);
        int senderID = Integer.parseInt(parsedMessage[1]);
        int receiverID = Integer.parseInt(parsedMessage[2]);
        int seqID = Integer.parseInt(parsedMessage[3]);
        boolean isAck = Boolean.parseBoolean(parsedMessage[4]);

        return new MessageData(sourceID, senderID, receiverID, seqID, isAck);
    }
}
