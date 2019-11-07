import java.util.*;

public class FIFOBroadcast {

    private static List<MessageSource> messages = new ArrayList<>();

    public static List<MessageSource> getMessages() {
        return messages;
    }

    static void deliver(MessageSource ms) {
        messages.add(ms);
    }
}
