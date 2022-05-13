package at.tugraz.ist.qs2021.simple;

/**
 * Some Simple Functions in Java.
 * Just for demonstration, please test the scala version.
 */
public class SimpleJavaFunctions {
    public static int[] insertionSort(int[] array) {
        int[] sorted = array.clone();
        for (int i = 0; i < sorted.length; i++) {
            int temp = sorted[i];
            int j = i - 1;

            while (j >= 0 && sorted[j] > temp) {
                sorted[j + 1] = sorted[j];
                j = j - 1;
            }

            sorted[j + 1] = temp;
        }

        return sorted;
    }

    public static int max(int[] array) throws ArrayIndexOutOfBoundsException {
        if (array.length == 0) throw new ArrayIndexOutOfBoundsException("Empty array given");
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static int minIndex(int[] array) throws ArrayIndexOutOfBoundsException {
        if (array.length == 0) throw new ArrayIndexOutOfBoundsException("Empty array given");
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
}
