import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {

    static int NUMBERS_SIZE = 1000000;
    static long SEED = 2023;

    public static void main(String[] args) {

        if (!new File("results").exists()) { new File("results").mkdir(); }

        int[] numbersOfElements = { 100, 1000, 10000, 50000, 100000 };

        int runs = 5;

        CSVExporter bubbleCSV = new CSVExporter("results/BUBBLE.csv");
        bubbleCSV.addRow(new String[] { "Number of Elements", "Sorting Time", "Swaps", "Iterations" });

        CSVExporter shellCSV = new CSVExporter("results/SHELL.csv");
        shellCSV.addRow(new String[] { "Number of Elements", "Sorting Time", "Swaps", "Iterations" });

        CSVExporter quickCSV = new CSVExporter("results/QUICK.csv");
        quickCSV.addRow(new String[] { "Number of Elements", "Sorting Time", "Swaps", "Iterations" });

        for (int numberOfElements : numbersOfElements) {

            int[] arr = new int[numberOfElements];

            Random random = new Random(SEED);

            for (int i = 0; i < arr.length; i++) {
                arr[i] = random.nextInt(NUMBERS_SIZE);
            }

            Results[] bubbleResults = new Results[runs];
            Results[] shellResults = new Results[runs];
            Results[] quickResults = new Results[runs];

            for (int i = 0; i < runs; i++) {

                System.out.println();
                System.out.println("Run " + (i + 1) + " of " + runs + " with " + numberOfElements + " elements");

                int[] arrCopy = arr.clone();

                Bubble bubble = new Bubble(arrCopy);
                Results bubbleResult = bubble.sort();

                bubbleResults[i] = bubbleResult;

                System.out.println("Bubble Sort: " + bubbleResult.totalSortingTime() + "s");

                arrCopy = arr.clone();

                Shell shell = new Shell(arrCopy);
                Results shellResult = shell.sort();

                shellResults[i] = shellResult;

                System.out.println("Shell Sort: " + shellResult.totalSortingTime() + "s");

                arrCopy = arr.clone();

                Quick quick = new Quick(arrCopy);
                Results quickResult = quick.sort();

                quickResults[i] = quickResult;

                System.out.println("Quick Sort: " + quickResult.totalSortingTime() + "s");

            }

            // Bubble Sort

            double bubbleTotalSortingTime = 0;
            long bubbleTotalSwaps = 0;
            long bubbleTotalIterations = 0;

            for (Results bubbleResult : bubbleResults) {
                bubbleTotalSortingTime += bubbleResult.totalSortingTime();
                bubbleTotalSwaps += bubbleResult.swapTimes();
                bubbleTotalIterations += bubbleResult.iterationTimes();
            }

            bubbleTotalSortingTime /= runs; bubbleTotalSortingTime = (double) Math.round(bubbleTotalSortingTime * 1000d) / 1000d;
            bubbleTotalSwaps /= runs;
            bubbleTotalIterations /= runs;

            bubbleCSV.addRow(new String[] { String.valueOf(numberOfElements), String.valueOf(bubbleTotalSortingTime),
                    String.valueOf(bubbleTotalSwaps), String.valueOf(bubbleTotalIterations) });

            // Shell Sort

            double shellTotalSortingTime = 0;
            long shellTotalSwaps = 0;
            long shellTotalIterations = 0;

            for (Results shellResult : shellResults) {
                shellTotalSortingTime += shellResult.totalSortingTime();
                shellTotalSwaps += shellResult.swapTimes();
                shellTotalIterations += shellResult.iterationTimes();
            }

            shellTotalSortingTime /= runs; shellTotalSortingTime = (double) Math.round(shellTotalSortingTime * 1000d) / 1000d;
            shellTotalSwaps /= runs;
            shellTotalIterations /= runs;

            shellCSV.addRow(new String[] { String.valueOf(numberOfElements), String.valueOf(shellTotalSortingTime),
                    String.valueOf(shellTotalSwaps), String.valueOf(shellTotalIterations) });

            // Quick Sort

            double quickTotalSortingTime = 0;
            long quickTotalSwaps = 0;
            long quickTotalIterations = 0;

            for (Results quickResult : quickResults) {
                quickTotalSortingTime += quickResult.totalSortingTime();
                quickTotalSwaps += quickResult.swapTimes();
                quickTotalIterations += quickResult.iterationTimes();
            }

            quickTotalSortingTime /= runs; quickTotalSortingTime = (double) Math.round(quickTotalSortingTime * 1000d) / 1000d;
            quickTotalSwaps /= runs;
            quickTotalIterations /= runs;

            quickCSV.addRow(new String[] { String.valueOf(numberOfElements), String.valueOf(quickTotalSortingTime),
                    String.valueOf(quickTotalSwaps), String.valueOf(quickTotalIterations) });

        }

        bubbleCSV.close();
        shellCSV.close();
        quickCSV.close();

    }

}

class Bubble {

    private final int[] arr;

    private long swapTimes;
    private long iterationTimes;
    private double totalSortingTime;

    private boolean isSorted;

    public Bubble(int[] arr) {
        this.arr = arr;

        this.swapTimes = 0;
        this.iterationTimes = 0;
        this.totalSortingTime = 0;

        this.isSorted = false;
    }

    public Results sort() {
        if (this.isSorted) { throw new RuntimeException("Array is already sorted!"); }

        long startTime = System.nanoTime();

        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            iterationTimes++;

            for (int j = 0; j < n - i - 1; j++) {
                iterationTimes++;

                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    swapTimes++;
                }
            }
        }

        long endTime = System.nanoTime();

        this.totalSortingTime = (double) Math.round(((endTime - startTime) / 1e9) * 1000d) / 1000d;

        this.isSorted = true;
        return new Results(this.totalSortingTime, this.swapTimes, this.iterationTimes);

    }

    public int[] getArr() {
        return arr;
    }

    public long getSwapTimes() {
        return swapTimes;
    }

    public long getIterationTimes() {
        return iterationTimes;
    }

    public double getTotalSortingTime() {
        return totalSortingTime;
    }

    public boolean isSorted() {
        return isSorted;
    }

}

class Shell {

    private final int[] arr;
    private final int[] sequence;

    private long swapTimes;
    private long iterationTimes;
    private double totalSortingTime;

    private boolean isSorted;

    public Shell(int[] arr) {
        this.arr = arr;
        this.sequence = calculateKnuthSequence(arr.length);

        this.swapTimes = 0;
        this.iterationTimes = 0;
        this.totalSortingTime = 0;

        this.isSorted = false;
    }

    public int[] calculateKnuthSequence(int length) {
        int x = 1;
        while (calculateKnuthElement(x) < length) { x++; }

        int n_inc = x - 2;
        int[] sequence = new int[n_inc];

        for (int i = 0; i < n_inc; i++) {
            sequence[i] = calculateKnuthElement(n_inc - i + 1);
        }

        return sequence;
    }

    public int calculateKnuthElement(int i) {
        return (i == 1) ? (1) : (3 * calculateKnuthElement(i - 1) + 1);
    }

    public Results sort() {
        if (this.isSorted) { throw new RuntimeException("Array is already sorted!"); }

        long startTime = System.nanoTime();

        int temp;

        for (int span : sequence) {
            iterationTimes++;

            for (int j = span; j < arr.length; j++) {
                iterationTimes++;

                int value = arr[j];

                for (temp = j - span; temp >= 0 && value < arr[temp]; temp -= span) {
                    iterationTimes++;
                    arr[temp + span] = arr[temp];
                    swapTimes++;
                }

                arr[temp + span] = value;
            }
        }

        long endTime = System.nanoTime();

        this.totalSortingTime = (double) Math.round(((endTime - startTime) / 1e9) * 1000d) / 1000d;

        this.isSorted = true;
        return new Results(this.totalSortingTime, this.swapTimes, this.iterationTimes);

    }

    public int[] getArr() {
        return arr;
    }

    public int[] getSequence() {
        return sequence;
    }

    public long getSwapTimes() {
        return swapTimes;
    }

    public long getIterationTimes() {
        return iterationTimes;
    }

    public double getTotalSortingTime() {
        return totalSortingTime;
    }

    public boolean isSorted() {
        return isSorted;
    }

}

class Quick {

    private final int[] arr;

    private long swapTimes;
    private long iterationTimes;
    private double totalSortingTime;

    private boolean isSorted;

    public Quick(int[] arr) {
        this.arr = arr;

        this.swapTimes = 0;
        this.iterationTimes = 0;
        this.totalSortingTime = 0;

        this.isSorted = false;
    }

    public Results sort() {
        if (this.isSorted) { throw new RuntimeException("Array is already sorted!"); }

        long startTime = System.nanoTime();

        quickSort(this.arr, 0, this.arr.length - 1);

        long endTime = System.nanoTime();

        this.totalSortingTime = (double) Math.round(((endTime - startTime) / 1e9) * 1000d) / 1000d;

        this.isSorted = true;
        return new Results(this.totalSortingTime, this.swapTimes, this.iterationTimes);
    }

    private void quickSort(int[] arr, int inferior, int superior) {
        if (inferior >= superior) { return; }

        int pivo = partition(arr, inferior, superior);
        quickSort(arr, inferior, pivo - 1);
        quickSort(arr, pivo + 1, superior);
    }

    private int partition(int[] arr, int inferior, int superior) {
        int pivo = arr[inferior];
        int i = inferior;

        for (int j = inferior + 1; j <= superior; j++) {
            iterationTimes++;

            if (arr[j] < pivo) {
                i++;

                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

                swapTimes++;
            }
        }

        int temp = arr[i];
        arr[i] = arr[inferior];
        arr[inferior] = temp;

        swapTimes++;

        return i;
    }

    public int[] getArr() {
        return arr;
    }

    public long getSwapTimes() {
        return swapTimes;
    }

    public long getIterationTimes() {
        return iterationTimes;
    }

    public double getTotalSortingTime() {
        return totalSortingTime;
    }

    public boolean isSorted() {
        return isSorted;
    }

}

record Results(double totalSortingTime, long swapTimes, long iterationTimes) {}

class CSVExporter {

    private FileWriter fileWriter;

    public CSVExporter(String filePath) {
        try {
            this.fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRow(String[] row) {
        try {

            for (int i = 0; i < row.length; i++) {
                fileWriter.append(row[i]);

                if (i != row.length - 1) {
                    fileWriter.append(",");
                }
            }

            fileWriter.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRows(String[][] rows) {
        for (String[] row : rows) {
            addRow(row);
        }
    }

    public void close() {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}