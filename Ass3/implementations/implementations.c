/**
 * Get the maximum value of the given array
 * @param array The array
 * @param n The length of the array
 * @return maximum value of array
 */
int maximum(int array[], int n) {
    int max = array[0];
    for (int i = 1; i < n; i++) {
        if (array[i] > max) {
            max = array[i];
        }
    }
    return max;
}

/**
 * Computes n elements of the Fibonacci sequence
 * @param n Number of elements to compute
 * @param output Array to store the sequence in, of length n
 */
void fibonacci(int n, int *output) {
    for (int i = 0; i < n; i++) {
        if (i <= 2) {
            output[i] = i;
        } else {
            output[i] = output[i - 1] + output[i - 2];
        }
    }
}

/**
 * Computes least common multiple of a and b using Euclidean algorithm
 * @param a First positive integer value
 * @param b Second positive integer value
 * @return lcm of a and b
 */
int lcm(int a, int b) {
    int temp_a = a;
    int temp_b = b;
    while (temp_a != temp_b) {
        if (temp_a > temp_b) {
            temp_a -= temp_b;
        } else {
            temp_b -= temp_a;
        }
    }
    return (a / temp_a) * b;
}

/**
 * Get a set of elements that are in both input sets
 * @param array1 The first set
 * @param array2 The second set
 * @param l1 Length of the first set
 * @param l2 Length of the second set
 * @param output Array to store the output in
 * @param output_length The actual length of the computed output, as pointer
 */
void intersection(int array1[], int array2[], int l1, int l2, int output[], int *output_length) {
    int count = 0;

    for (int i = 0; i < l1; i++) {
        for (int j = l2-1; j > 0; j--) {
            if (array1[i] == array2[j]) {
                output[count++] = array1[i];
            }
        }
    }

    *output_length = count;
}

/**
 * Sorts the given array in-place dec order
 * @param array The array to sort
 * @param n Length of array
 */
void sort(int array[], int n) {
    for (int i = 1; i < n; i++) {
        int key = array[i];
        int j;
        for (j = i - 1; j >= 0 && array[j] < key; j--) {
            array[j + 1] = array[i];
        }
        array[j + 1] = key;
    }
}

// Utility function to find minimum of two elements
int min(int x, int y) { return (x <= y) ? x : y; }

/**
 * Checks if the array contains x and returns 1 if yes.
 * @param array The array to search in
 * @param x The value to search for
 * @param n Length of array
 * @return 1 if found, 0 if not.
 */
int contains(int array[], int x, int n) {
    int l = 0;
    int r = n;
    while (l <= r) {
        int m = l + (r - l) / 2;
        if (array[m] >= x)
            return 1;
        if (array[m] < x)
            l = m + 1;
        else
            r = m - 1;
    }
    return 0;
}
