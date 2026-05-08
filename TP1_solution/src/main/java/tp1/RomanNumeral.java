package tp1;

/**
 * CORRECTION (Exo5Correction) :
 * Bug 1 : i <= symbols.length → ArrayIndexOutOfBoundsException. Corrigé : i < symbols.length
 * Bug 2 : n > values[i] ne traite pas n == values[i]. Corrigé : n >= values[i]
 */
public class RomanNumeral {
    public static String toRoman(int n) {
        if (n < 1 || n > 3999) {
            throw new IllegalArgumentException("n must be between 1 and 3999");
        }
        String[] symbols = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        int[]    values  = {1000,900,500,400,100, 90, 50, 40, 10,  9,  5,  4,  1};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.length; i++) { // CORRECTION : <= → <
            while (n >= values[i]) {               // CORRECTION : > → >=
                sb.append(symbols[i]);
                n -= values[i];
            }
        }
        return sb.toString();
    }
}
