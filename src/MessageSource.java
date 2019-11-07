import java.util.Objects;

// Structure to represent information that uniquely identify a message
public class MessageSource {

    // source of the message
    private int sourceID;
    // message id
    private int messageID;

    public int getSourceID() {
        return sourceID;
    }

    public int getMessageID() {
        return messageID;
    }

    public MessageSource(int sourceID, int messageID) {
        this.sourceID = sourceID;
        this.messageID = messageID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSource that = (MessageSource) o;
        return sourceID == that.sourceID &&
                messageID == that.messageID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceID, messageID);
    }
}