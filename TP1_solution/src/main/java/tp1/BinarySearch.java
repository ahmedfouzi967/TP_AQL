package tp1;

/**
 * CORRECTION (Exo3Correction) :
 * Bug : while (low < high) manque le cas low == high.
 * Corrigé : while (low <= high)
 */
public class BinarySearch {
    public static int binarySearch(int[] array, int element) {
        if (array == null) {
            throw new NullPointerException("Array must not be null");
        }
        int low = 0;
        int high = array.length - 1;
        while (low <= high) { // CORRECTION : < remplacé par <=
            int mid = (low + high) / 2;
            if (array[mid] == element) {
                return mid;
            } else if (array[mid] <= element) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
