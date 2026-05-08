package tp1;

/**
 * CORRECTION (Exo1Correction) :
 * Bug dans isPalindrome : i et j étaient incrémentés/décrémentés dans le mauvais sens.
 * Original : j++; i--;   →   Corrigé : i++; j--;
 */
public class Palindrome {
    public static boolean isPalindrome(String s) {
        if (s == null) {
            throw new NullPointerException("String must not be null");
        }
        s = s.toLowerCase().replaceAll("\\s+", "");
        int i = 0;
        int j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) {
                return false;
            }
            i++; // CORRECTION : était j++
            j--; // CORRECTION : était i--
        }
        return true;
    }
}
