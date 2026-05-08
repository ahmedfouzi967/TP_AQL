package ConditionCoverageTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tp1.*;

/**
 * CONDITION COVERAGE TESTS
 * Objectif : chaque sous-condition booléenne doit être évaluée à true ET à false
 * au moins une fois.
 */

// ─────────────────────────────────────────────
// Exercice 1 – Palindrome
// Conditions :
//   C1: s == null               → true / false
//   C2: i < j                   → true / false
//   C3: charAt(i) != charAt(j)  → true / false
// ─────────────────────────────────────────────
class Exo1Test {

    @Test
    void testC1_True() {
        assertThrows(NullPointerException.class, () -> Palindrome.isPalindrome(null));
    }

    @Test
    void testC1_False_C2_False() {
        assertTrue(Palindrome.isPalindrome(""));
    }

    @Test
    void testC2_True_C3_False() {
        assertTrue(Palindrome.isPalindrome("kayak"));
    }

    @Test
    void testC2_True_C3_True() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }
}

// ─────────────────────────────────────────────
// Exercice 2 – Anagram
// Conditions :
//   C1: s1 == null              → true / false
//   C2: s2 == null              → true / false
//   C3: length != length        → true / false
//   C4: c != 0                  → true / false
// ─────────────────────────────────────────────
class Exo2Test {

    @Test
    void testC1_True() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram(null, "b"));
    }

    @Test
    void testC1_False_C2_True() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram("a", null));
    }

    @Test
    void testC3_True_DifferentLengths() {
        assertFalse(Anagram.isAnagram("ab", "abc"));
    }

    @Test
    void testC4_True_NotAnagram() {
        assertFalse(Anagram.isAnagram("hello", "world"));
    }

    @Test
    void testC3_False_C4_False_ValidAnagram() {
        assertTrue(Anagram.isAnagram("chien", "niche"));
    }

    @Test
    void testEmptyStrings() {
        assertTrue(Anagram.isAnagram("", ""));
    }
}

// ─────────────────────────────────────────────
// Exercice 3 – BinarySearch
// Conditions :
//   C1: array == null           → true / false
//   C2: low <= high             → true / false
//   C3: array[mid] == element   → true / false
//   C4: array[mid] <= element   → true / false
// ─────────────────────────────────────────────
class Exo3Test {

    @Test
    void testC1_True() {
        assertThrows(NullPointerException.class, () -> BinarySearch.binarySearch(null, 5));
    }

    @Test
    void testC2_False_EmptyArray() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{}, 1));
    }

    @Test
    void testC3_True_ElementFound() {
        assertEquals(2, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 5));
    }

    @Test
    void testC3_False_C4_True_GoRight() {
        assertEquals(4, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 9));
    }

    @Test
    void testC3_False_C4_False_GoLeft() {
        assertEquals(0, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 1));
    }

    @Test
    void testC2_EventuallyFalse_NotFound() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 4));
    }
}

// ─────────────────────────────────────────────
// Exercice 4 – QuadraticEquation
// Conditions :
//   C1: a == 0    → true / false
//   C2: delta < 0 → true / false
//   C3: delta == 0→ true / false
// ─────────────────────────────────────────────
class Exo4Test {

    @Test
    void testC1_True() {
        assertThrows(IllegalArgumentException.class, () -> QuadraticEquation.solve(0, 2, 1));
    }

    @Test
    void testC2_True_NegativeDelta() {
        assertNull(QuadraticEquation.solve(1, 0, 5));
    }

    @Test
    void testC2_False_C3_True_ZeroDelta() {
        double[] r = QuadraticEquation.solve(1, -2, 1);
        assertNotNull(r);
        assertEquals(1, r.length);
        assertEquals(1.0, r[0], 1e-9);
    }

    @Test
    void testC2_False_C3_False_PositiveDelta() {
        double[] r = QuadraticEquation.solve(1, -5, 6);
        assertNotNull(r);
        assertEquals(2, r.length);
        assertEquals(3.0, r[0], 1e-9);
        assertEquals(2.0, r[1], 1e-9);
    }
}

// ─────────────────────────────────────────────
// Exercice 5 – RomanNumeral
// Conditions :
//   C1: n < 1       → true / false
//   C2: n > 3999    → true / false
//   C3: n >= val[i] → true / false
// ─────────────────────────────────────────────
class Exo5Test {

    @Test
    void testC1_True() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(0));
    }

    @Test
    void testC2_True() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(4000));
    }

    @Test
    void testC1_False_C2_False_C3_True() {
        assertEquals("I", RomanNumeral.toRoman(1));
    }

    @Test
    void testC3_MultipleSymbols() {
        assertEquals("III", RomanNumeral.toRoman(3));
    }

    @Test
    void testAllSymbolsUsed() {
        assertEquals("MCMXCIX", RomanNumeral.toRoman(1999));
    }

    @Test
    void testMaxValue() {
        assertEquals("MMMCMXCIX", RomanNumeral.toRoman(3999));
    }
}

// ─────────────────────────────────────────────
// Exercice 6 – FizzBuzz
// Conditions :
//   C1: n <= 0        → true / false
//   C2: n % 15 == 0   → true / false
//   C3: n % 3 == 0    → true / false
//   C4: n % 5 == 0    → true / false
// ─────────────────────────────────────────────
class Exo6Test {

    @Test
    void testC1_True_Zero() {
        assertThrows(IllegalArgumentException.class, () -> FizzBuzz.fizzBuzz(0));
    }

    @Test
    void testC1_True_Negative() {
        assertThrows(IllegalArgumentException.class, () -> FizzBuzz.fizzBuzz(-3));
    }

    @Test
    void testC2_True_FizzBuzz() {
        assertEquals("FizzBuzz", FizzBuzz.fizzBuzz(15));
    }

    @Test
    void testC2_False_C3_True_Fizz() {
        assertEquals("Fizz", FizzBuzz.fizzBuzz(3));
    }

    @Test
    void testC2_False_C3_False_C4_True_Buzz() {
        assertEquals("Buzz", FizzBuzz.fizzBuzz(5));
    }

    @Test
    void testC2_False_C3_False_C4_False_Number() {
        assertEquals("7", FizzBuzz.fizzBuzz(7));
    }

    @Test
    void testC1_False_ValueOne() {
        assertEquals("1", FizzBuzz.fizzBuzz(1));
    }
}
