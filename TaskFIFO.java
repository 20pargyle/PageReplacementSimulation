import java.util.*;
public class TaskFIFO implements Runnable {
    private LinkedList<Integer> frameList;
    private int[] sequence;
    private int maxMemoryFrames;
    private final int MAX_PAGE_REFERENCE;
    private int[] pageFaults;
    private int numPageFaults;

    public TaskFIFO(int[] sequence, int maxMemoryFrames, int MAX_PAGE_REFERENCE, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.MAX_PAGE_REFERENCE = MAX_PAGE_REFERENCE;
        this.pageFaults = pageFaults;
        this.numPageFaults = 0;
        this.frameList = new LinkedList<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < maxMemoryFrames; i++) {
            frameList.add(-1);
        }
        for (int pageRef : sequence) {
            // if the page is already loaded, do nothing;
            // if not, take out the oldest and add the newest 
            if (frameList.indexOf(pageRef) < 0){
                numPageFaults++;
                frameList.pop();
                frameList.add(pageRef);
            }
        }
        pageFaults[maxMemoryFrames] = numPageFaults;
    }
}