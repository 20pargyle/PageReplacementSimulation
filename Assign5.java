import java.util.Random;
import java.util.concurrent.*;
public class Assign5 {
    public static void main(String[] args) {
        int MAX_PAGE_REFERENCE = 250;
        int[][][] results = new int[3][1000][];
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            results[0][i] = new int[100]; // for FIFO
            results[1][i] = new int[100]; // for LRU
            results[2][i] = new int[100]; // for MRU
            for (int numFrames = 0; numFrames < 100; numFrames++) {
                int[] sequence = createSequence(MAX_PAGE_REFERENCE);
                TaskFIFO taskF = new TaskFIFO(sequence, numFrames+1, MAX_PAGE_REFERENCE, results[0][i]);
                threadPool.execute(taskF);
            }
        }
        System.out.printf("\nSimulation took %d ms\n", System.currentTimeMillis() - startTime);

        // min page fault detection

        reportAnomaly("FIFO", results[0]);
        // reportAnomaly("LRU", results[0]);
        // reportAnomaly("MRU", results[0]);
    }

    private static int[] createSequence(int MAX_PAGE_REFERENCE){
        int[] sequence = new int[1000];
        Random r = new Random();
        for (int i=0; i < sequence.length; i++) {
            sequence[i] = r.nextInt(MAX_PAGE_REFERENCE + 1);
        }
        return sequence;
    }

    private static void reportAnomaly(String algorithmName, int[][] results){
        int anomalyCount = 0;
        int maxDiff = 0;
        System.out.printf("Belady's Anomaly Report for %s\n\n", algorithmName);
        for (int[] list : results) {
            for (int i = 0; i < list.length-2; i++) {
                if (list[i+1] > list[i]){
                    int diff = list[i+1]-list[i];
                    System.out.printf("detected - Previous %d : Current %d (%d)\n", list[i], list[i+1], diff);
                    anomalyCount++;
                    if (diff > maxDiff){
                        maxDiff = diff;
                    }
                }
            }
        }
        System.out.printf("Anomaly detected %d times with a max difference of %d", anomalyCount, maxDiff);
    }

    public static void testFIFO(int MAX_PAGE_REFERENCE) {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3
    
        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskFIFO(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);
    
        // Replacement should be: 1, 2, -, 1, 
        // Page Faults should be 7
        (new TaskFIFO(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);
    
        // Replacement should be: 1, 2, 3, -, -, -, -, 1
        // Page Faults should be 4
        (new TaskFIFO(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }
    /*
    public static void testLRU(int MAX_PAGE_REFERENCE) {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3
    
        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskLRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);
    
        // Replacement should be: 1, 2, -, 1, 
        // Page Faults should be 7
        (new TaskLRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);
    
        // Replacement should be: 1, 2, 3, -, -, -, -, 1
        // Page Faults should be 4
        (new TaskLRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }
    public static void testMRU(int MAX_PAGE_REFERENCE) {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3
    
        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskMRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);
    
        // Replacement should be: 1, 2, -, 1, 
        // Page Faults should be 7
        (new TaskMRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);
    
        // Replacement should be: 1, 2, 3, -, -, -, -, 1
        // Page Faults should be 4
        (new TaskMRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }
    */
}