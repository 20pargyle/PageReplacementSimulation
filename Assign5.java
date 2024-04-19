import java.util.Random;
import java.util.concurrent.*;
public class Assign5 {
    private static final int MAX_PAGE_REFERENCE = 250;
    public static void main(String[] args) {
        int[][][] results = new int[3][1000][];
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            results[0][i] = new int[101]; // for FIFO
            results[1][i] = new int[101]; // for LRU
            results[2][i] = new int[101]; // for MRU
            int[] sequence = createSequence(1000, MAX_PAGE_REFERENCE);
            for (int numFrames = 1; numFrames < 101; numFrames++) {
                TaskFIFO taskF = new TaskFIFO(sequence, numFrames, MAX_PAGE_REFERENCE, results[0][i]);
                TaskLRU taskL = new TaskLRU(sequence, numFrames, MAX_PAGE_REFERENCE, results[1][i]);
                TaskMRU taskM = new TaskMRU(sequence, numFrames, MAX_PAGE_REFERENCE, results[2][i]);
                threadPool.execute(taskF);
                threadPool.execute(taskL);
                threadPool.execute(taskM);
            }
        }

        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println("Oh no, we were interrupted!");
        }

        System.out.printf("\nSimulation took %d ms\n\n", System.currentTimeMillis() - startTime);

        // Report which algorithm had the lowest number of page faults for a given sequence and number of frames
        int minFIFO = 0;
        int minLRU = 0;
        int minMRU = 0;
        for (int i = 0; i < results[0].length; i++) {
            for (int j = 1; j < results[0][i].length-1; j++) {
                int minValue = Math.min(Math.min(results[0][i][j], results[1][i][j]), results[2][i][j]);
                if (minValue == results[0][i][j]){
                    minFIFO++;
                }
                if (minValue == results[1][i][j]){
                    minLRU++;
                }
                if (minValue == results[2][i][j]){
                    minMRU++;
                }
            }
        }
        System.out.printf("FIFO min PF : %d\n", minFIFO);
        System.out.printf("LRU min PF  : %d\n", minLRU);
        System.out.printf("MRU min PF  : %d\n\n", minMRU);

        reportAnomaly("FIFO", results[0]);
        reportAnomaly("LRU", results[1]);
        reportAnomaly("MRU", results[2]);
    }

    private static int[] createSequence(int sequenceLength, int limit){
        int[] sequence = new int[sequenceLength];
        Random r = new Random();
        for (int i=0; i < sequence.length-1; i++) {
            sequence[i] = r.nextInt(limit + 1);
        }
        return sequence;
    }

    private static int[] createSequence(int sequenceLength, int limit, int randomizerSeed){
        int[] sequence = new int[sequenceLength];
        Random r = new Random(randomizerSeed);
        for (int i=0; i < sequence.length-1; i++) {
            sequence[i] = r.nextInt(limit + 1);
        }
        return sequence;
    }

    private static void reportAnomaly(String algorithmName, int[][] results){
        int anomalyCount = 0;
        int maxDiff = 0;
        System.out.printf("Belady's Anomaly Report for %s\n\n", algorithmName);
        for (int[] list : results) {
            for (int i = 1; i < list.length-1; i++) {
                if (list[i] < list[i+1]){
                    int diff = list[i+1] - list[i];
                    System.out.printf("detected - Previous %d : Current %d (%d)\n", list[i], list[i+1], diff);
                    anomalyCount++;
                    if (diff > maxDiff){
                        maxDiff = diff;
                    }
                }
            }
        }
        System.out.printf("\tAnomaly detected %d times with a max difference of %d\n\n", anomalyCount, maxDiff);
    }

    private static void reportTestAnomaly(String algorithmName, int[] results){
        int anomalyCount = 0;
        int maxDiff = 0;
        System.out.printf("Belady's Anomaly Report for %s\n\n", algorithmName);
        for (int i = 1; i < results.length-1; i++) {
            if (results[i] < results[i+1]){
                int diff = results[i+1] - results[i];
                System.out.printf("detected - Previous %d : Current %d (%d)\n", results[i], results[i+1], diff);
                anomalyCount++;
                if (diff > maxDiff){
                    maxDiff = diff;
                }
            }
        }
        System.out.printf("\tAnomaly detected %d times with a max difference of %d\n\n", anomalyCount, maxDiff);
    }

    public static void testFIFO(int sequenceLength, int maxMemoryFrames, int randomizerSeed) {
        int[] sequence2 = createSequence(sequenceLength, MAX_PAGE_REFERENCE, randomizerSeed);
        int[] pageFaults = new int[maxMemoryFrames];

        for (int i = 1; i < pageFaults.length; i++) {
            (new TaskFIFO(sequence2, i, MAX_PAGE_REFERENCE, pageFaults)).run();
            // System.out.printf("Page Faults: %d\n", pageFaults[i]);  
        }
        reportTestAnomaly("FIFO", pageFaults);
    }
    
    public static void testLRU(int sequenceLength, int maxMemoryFrames, int randomizerSeed) {
        int[] sequence2 = createSequence(sequenceLength, MAX_PAGE_REFERENCE, randomizerSeed);
        int[] pageFaults = new int[maxMemoryFrames];

        for (int i = 1; i < pageFaults.length; i++) {
            (new TaskLRU(sequence2, i, MAX_PAGE_REFERENCE, pageFaults)).run();
            // System.out.printf("Page Faults: %d\n", pageFaults[i]);  
        }
        reportTestAnomaly("LRU", pageFaults);
    }

    public static void testMRU(int sequenceLength, int maxMemoryFrames, int randomizerSeed) {
        int[] sequence2 = createSequence(sequenceLength, MAX_PAGE_REFERENCE, randomizerSeed);
        int[] pageFaults = new int[maxMemoryFrames];

        for (int i = 1; i < pageFaults.length; i++) {
            (new TaskMRU(sequence2, i, MAX_PAGE_REFERENCE, pageFaults)).run();
            // System.out.printf("Page Faults: %d\n", pageFaults[i]);  
        }
        reportTestAnomaly("MRU", pageFaults);
    }
}