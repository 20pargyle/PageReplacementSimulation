import java.util.LinkedList;

public class TaskLRU implements Runnable {
    private LinkedList<Integer> frameStack;
    private int[] sequence;
    private int maxMemoryFrames;
    private final int MAX_PAGE_REFERENCE;
    private int[] pageFaults;
    private int numPageFaults;

    public TaskLRU(int[] sequence, int maxMemoryFrames, int MAX_PAGE_REFERENCE, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.MAX_PAGE_REFERENCE = MAX_PAGE_REFERENCE;
        this.pageFaults = pageFaults;
        this.numPageFaults = 0;
        this.frameStack = new LinkedList<>();
    }

    @Override
    public void run() {
        for (int pageRef : sequence) {
            int pageIndex = frameStack.indexOf(pageRef);
            // if page is already in the list, we have just called for it, so let's add it to top of the list;
            if (pageIndex >= 0){
                frameStack.remove(pageIndex);
                frameStack.addFirst(pageRef);
            }
            // if it's not in the list, then a page fault occurs.
            else {
                numPageFaults++;
                // if stack is not full:
                if (frameStack.size() < maxMemoryFrames){
                    frameStack.addFirst(pageRef);
                }
                else {
                    frameStack.removeLast();
                    frameStack.addFirst(pageRef);
                }
            }
        }
        pageFaults[maxMemoryFrames] = numPageFaults;
    }
}