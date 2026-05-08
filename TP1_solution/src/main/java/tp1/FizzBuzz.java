package tp1;

/**
 * CORRECTION (Exo6Correction) :
 * Bug : n <= 1 rejette 1 qui est un entier positif valide.
 * Corrigé : n <= 0
 */
public class FizzBuzz {
    public static String fizzBuzz(int n) {
        if (n <= 0) { // CORRECTION : <= 1 remplacé par <= 0
            throw new IllegalArgumentException("n must be positive");
        }
        if (n % 15 == 0) {
            return "FizzBuzz";
        }
        if (n % 3 == 0) {
            return "Fizz";
        }
        if (n % 5 == 0) {
            return "Buzz";
        }
        return String.valueOf(n);
    }
}
