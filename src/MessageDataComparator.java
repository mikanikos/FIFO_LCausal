import java.util.Comparator;

public class MessageDataComparator implements Comparator<MessageData> {
    @Override
    public int compare(MessageData messageData, MessageData t1) {
//        for (int i=1; i<=Da_proc.getNumProcesses(); i++) {
//            if (messageData.getVectorClock().get(i).get() > t1.getVectorClock().get(i).get()) {
//                return 1;
//            }
//        }
//        return -1;

        return Integer.compare(messageData.getMessageID(), t1.getMessageID());
    }
}
