import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perfect_Receiver {

    private Map<Integer, List<Integer>> delivered;

    public Perfect_Receiver() {
        this.delivered = new HashMap<>();
    }


    // format of a message: "senderID seqID ackFlag"
    public String deliver(String packet) throws IOException {
        String[] parsedMessage = packet.split("_");
        List<Integer> idsList = delivered.get(parsedMessage[0])
        boolean alreadyReceived = false;
        if (idsList == null) {
            delivered.put(Integer.parseInt(parsedMessage[0]), new ArrayList<>());
        }
        if (!idsList.contains(Integer.parseInt(parsedMessage[1]))) {
            idsList.add(Integer.parseInt(parsedMessage[1]));
        }
        else {
            alreadyReceived = true;
        }

        return packet;
    }

}
