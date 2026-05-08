package BranchCoverageTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tp1.*;

/**
 * BRANCH COVERAGE TESTS
 * Objectif : exécuter chaque branche (true/false de chaque if, while, for) au moins une fois.
 */

// ─────────────────────────────────────────────
// Exercice 1 – Palindrome
// Branches :
//   B1: if (s == null)          → true / false
//   B2: while (i < j)           → exécuté / non exécuté
//   B3: if (charAt(i)!=charAt(j))→ true / false
// ─────────────────────────────────────────────
class Exo1Test {

    @Test
    void testB1_NullString() {
        assertThrows(NullPointerException.class, () -> Palindrome.isPalindrome(null));
    }

    @Test
    void testB2_WhileNotEntered_EmptyString() {
        assertTrue(Palindrome.isPalindrome(""));
    }

    @Test
    void testB2_WhileEntered_B3_CharsMatch() {
        assertTrue(Palindrome.isPalindrome("kayak"));
    }

    @Test
    void testB3_CharsMismatch() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }

    @Test
    void testPalindromeWithSpaces() {
        assertTrue(Palindrome.isPalindrome("Esope reste ici et se repose"));
    }
}

// ─────────────────────────────────────────────
// Exercice 2 – Anagram
// Branches :
//   B1: s1 == null              → true / false
//   B2: s2 == null              → true / false
//   B3: length != length        → true / false
//   B4: for loop                → exécuté / non exécuté
//   B5: if (c != 0)             → true / false
// ─────────────────────────────────────────────
class Exo2Test {

    @Test
    void testB1_NullS1() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram(null, "abc"));
    }

    @Test
    void testB2_NullS2() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram("abc", null));
    }

    @Test
    void testB3_DifferentLengths() {
        assertFalse(Anagram.isAnagram("ab", "abc"));
    }

    @Test
    void testB4_ForLoopNotEntered_EmptyStrings() {
        assertTrue(Anagram.isAnagram("", ""));
    }

    @Test
    void testB5_CountNonZero_NotAnagram() {
        assertFalse(Anagram.isAnagram("hello", "world"));
    }

    @Test
    void testB5_CountAllZero_ValidAnagram() {
        assertTrue(Anagram.isAnagram("chien", "niche"));
    }
}

// ─────────────────────────────────────────────
// Exercice 3 – BinarySearch
// Branches :
//   B1: array == null           → true / false
//   B2: while (low <= high)     → exécuté / non exécuté
//   B3: array[mid] == element   → true / false
//   B4: array[mid] <= element   → true / false
// ─────────────────────────────────────────────
class Exo3Test {

    @Test
    void testB1_NullArray() {
        assertThrows(NullPointerException.class, () -> BinarySearch.binarySearch(null, 5));
    }

    @Test
    void testB2_WhileNotEntered_EmptyArray() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{}, 5));
    }

    @Test
    void testB3_ElementFoundInMiddle() {
        assertEquals(2, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 5));
    }

    @Test
    void testB4_ElementInUpperHalf() {
        assertEquals(4, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 9));
    }

    @Test
    void testB4_ElementInLowerHalf() {
        assertEquals(0, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 1));
    }

    @Test
    void testElementNotFound() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 4));
    }
}

// ─────────────────────────────────────────────
// Exercice 4 – QuadraticEquation
// Branches :
//   B1: a == 0    → true / false
//   B2: delta < 0 → true / false
//   B3: delta == 0→ true / false
// ─────────────────────────────────────────────
class Exo4Test {

    @Test
    void testB1_AEqualsZero() {
        assertThrows(IllegalArgumentException.class, () -> QuadraticEquation.solve(0, 1, 1));
    }

    @Test
    void testB2_NegativeDelta() {
        assertNull(QuadraticEquation.solve(1, 0, 1));
    }

    @Test
    void testB3_ZeroDelta() {
        double[] r = QuadraticEquation.solve(1, -2, 1);
        assertNotNull(r);
        assertEquals(1, r.length);
        assertEquals(1.0, r[0], 1e-9);
    }

    @Test
    void testB4_PositiveDelta() {
        double[] r = QuadraticEquation.solve(1, -5, 6);
        assertNotNull(r);
        assertEquals(2, r.length);
        assertEquals(3.0, r[0], 1e-9);
        assertEquals(2.0, r[1], 1e-9);
    }
}

// ─────────────────────────────────────────────
// Exercice 5 – RomanNumeral
// Branches :
//   B1: n < 1 || n > 3999       → true / false
//   B2: for i (0..12)           → exécuté
//   B3: while (n >= values[i])  → exécuté / non exécuté
// ─────────────────────────────────────────────
class Exo5Test {

    @Test
    void testB1_BelowRange() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(0));
    }

    @Test
    void testB1_AboveRange() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(4000));
    }

    @Test
    void testB3_WhileEnteredOnce() {
        assertEquals("I", RomanNumeral.toRoman(1));
    }

    @Test
    void testB3_WhileEnteredMultipleTimes() {
        assertEquals("III", RomanNumeral.toRoman(3));
    }

    @Test
    void testComplexValue() {
        assertEquals("MCMXCIX", RomanNumeral.toRoman(1999));
    }

    @Test
    void testMaxValue() {
        assertEquals("MMMCMXCIX", RomanNumeral.toRoman(3999));
    }
}

// ─────────────────────────────────────────────
// Exercice 6 – FizzBuzz
// Branches :
//   B1: n <= 0        → true / false
//   B2: n % 15 == 0   → true / false
//   B3: n % 3 == 0    → true / false
//   B4: n % 5 == 0    → true / false
// ─────────────────────────────────────────────
class Exo6Test {

    @Test
    void testB1_NonPositive() {
        assertThrows(IllegalArgumentException.class, () -> FizzBuzz.fizzBuzz(0));
        assertThrows(IllegalArgumentException.class, () -> FizzBuzz.fizzBuzz(-1));
    }

    @Test
    void testB2_FizzBuzz() {
        assertEquals("FizzBuzz", FizzBuzz.fizzBuzz(15));
    }

    @Test
    void testB3_Fizz() {
        assertEquals("Fizz", FizzBuzz.fizzBuzz(3));
    }

    @Test
    void testB4_Buzz() {
        assertEquals("Buzz", FizzBuzz.fizzBuzz(5));
    }

    @Test
    void testB5_Number() {
        assertEquals("7", FizzBuzz.fizzBuzz(7));
    }
}
