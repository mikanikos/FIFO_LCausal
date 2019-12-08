import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Store all message data that are important for the protocols used
public class MessageData implements Serializable {

	private static final long serialVersionUID = 1L;
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
    // vector clock
    private ConcurrentMap<Integer, AtomicInteger> vectorClock;

    public ConcurrentMap<Integer, AtomicInteger> getVectorClock() {
        return vectorClock;
    }

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

    public MessageData(int sourceID, int senderID, int receiverID, int messageID, boolean isAck, ConcurrentMap<Integer, AtomicInteger> vectorClock) {
        this.sourceID = sourceID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageID = messageID;
        this.isAck = isAck;
        this.vectorClock = vectorClock;
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
        String mapAsString = vectorClock.keySet().stream()
                .map(key -> key + "=" + vectorClock.get(key))
                .collect(Collectors.joining(","));

        return sourceID  + " " + senderID + " " + receiverID + " "  + messageID + " " + isAck + " " + mapAsString;
    }

    // Parse message: convert from string to MessageData
    public static MessageData parseMessage(String stringMessage) {
        String[] parsedMessage = stringMessage.split(" ");
        int sourceID = Integer.parseInt(parsedMessage[0]);
        int senderID = Integer.parseInt(parsedMessage[1]);
        int receiverID = Integer.parseInt(parsedMessage[2]);
        int seqID = Integer.parseInt(parsedMessage[3]);
        boolean isAck = Boolean.parseBoolean(parsedMessage[4]);

        Map<String, String> map = Arrays.stream(parsedMessage[5].split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));

        ConcurrentMap<Integer, AtomicInteger> vector = new ConcurrentHashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            vector.put(Integer.parseInt(entry.getKey()), new AtomicInteger(Integer.parseInt(entry.getValue())));
        }

        return new MessageData(sourceID, senderID, receiverID, seqID, isAck, vector);
    }
}