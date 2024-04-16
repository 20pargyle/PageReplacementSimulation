import java.util.*;

public class TaskFIFO implements Runnable{
    private LinkedList<Integer> frameList = new LinkedList<>();
    private int[] sequence;
    private int maxMemoryFrames;
    private int MAX_PAGE_REFERENCE;
    private int[] pageFaults;
    int numPageFaults = 0;

    public TaskFIFO(int[] sequence, int maxMemoryFrames, int MAX_PAGE_REFERENCE, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.MAX_PAGE_REFERENCE = MAX_PAGE_REFERENCE;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        for (int i = 0; i < maxMemoryFrames; i++) {
            frameList.add(-1);
        }
        for (int pageRef : sequence) {
            // search frameList for desired page
            // if already there, do nothing;
            if (frameList.indexOf(pageRef) < 0){
                numPageFaults++;
                // search frameList for empty spots
                if (frameList.indexOf(-1) >= 0){
                    frameList.add(pageRef);
                    frameList.pop();
                }
                // if no empty spots, remove the oldest and add the newest
                else {
                    frameList.add(pageRef);
                    frameList.pop();
                }
            }
        }
        pageFaults[maxMemoryFrames-1] = numPageFaults;
    }
}