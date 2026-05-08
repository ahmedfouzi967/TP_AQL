package LineCoverageTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tp1.*;

/**
 * LINE COVERAGE TESTS
 * Objectif : exécuter chaque ligne de code source au moins une fois.
 */

// ─────────────────────────────────────────────
// Exercice 1 – Palindrome
// ─────────────────────────────────────────────
class Exo1Test {

    @Test
    void testNullThrowsException() {
        assertThrows(NullPointerException.class, () -> Palindrome.isPalindrome(null));
    }

    @Test
    void testPalindromeWord() {
        assertTrue(Palindrome.isPalindrome("kayak"));
    }

    @Test
    void testNotPalindrome() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }

    @Test
    void testPalindromeWithSpacesAndCase() {
        assertTrue(Palindrome.isPalindrome("Esope reste ici et se repose"));
    }

    @Test
    void testEmptyString() {
        assertTrue(Palindrome.isPalindrome(""));
    }
}

// ─────────────────────────────────────────────
// Exercice 2 – Anagram
// ─────────────────────────────────────────────
class Exo2Test {

    @Test
    void testNullFirstArgThrowsException() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram(null, "abc"));
    }

    @Test
    void testNullSecondArgThrowsException() {
        assertThrows(NullPointerException.class, () -> Anagram.isAnagram("abc", null));
    }

    @Test
    void testDifferentLengthsReturnFalse() {
        assertFalse(Anagram.isAnagram("ab", "abc"));
    }

    @Test
    void testValidAnagram() {
        assertTrue(Anagram.isAnagram("chien", "niche"));
    }

    @Test
    void testNotAnagram() {
        assertFalse(Anagram.isAnagram("hello", "world"));
    }
}

// ─────────────────────────────────────────────
// Exercice 3 – BinarySearch
// ─────────────────────────────────────────────
class Exo3Test {

    @Test
    void testNullArrayThrowsException() {
        assertThrows(NullPointerException.class, () -> BinarySearch.binarySearch(null, 5));
    }

    @Test
    void testElementFound() {
        assertEquals(2, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 5));
    }

    @Test
    void testElementInUpperHalf() {
        assertEquals(3, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 7));
    }

    @Test
    void testElementInLowerHalf() {
        assertEquals(1, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 3));
    }

    @Test
    void testElementNotFound() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{1, 3, 5, 7, 9}, 4));
    }
}

// ─────────────────────────────────────────────
// Exercice 4 – QuadraticEquation
// ─────────────────────────────────────────────
class Exo4Test {

    @Test
    void testAEqualsZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> QuadraticEquation.solve(0, 1, 1));
    }

    @Test
    void testNegativeDeltaReturnsNull() {
        assertNull(QuadraticEquation.solve(1, 0, 1));
    }

    @Test
    void testZeroDeltaReturnsOneRoot() {
        double[] result = QuadraticEquation.solve(1, -2, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(1.0, result[0], 1e-9);
    }

    @Test
    void testPositiveDeltaReturnsTwoRoots() {
        double[] result = QuadraticEquation.solve(1, -3, 2);
        assertNotNull(result);
        assertEquals(2, result.length);
    }
}

// ─────────────────────────────────────────────
// Exercice 5 – RomanNumeral
// ─────────────────────────────────────────────
class Exo5Test {

    @Test
    void testBelowRangeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(0));
    }

    @Test
    void testAboveRangeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(4000));
    }

    @Test
    void testSimpleValue() {
        assertEquals("I", RomanNumeral.toRoman(1));
    }

    @Test
    void testMixedSymbols() {
        assertEquals("MCMXCIX", RomanNumeral.toRoman(1999));
    }

    @Test
    void testMaxValue() {
        assertEquals("MMMCMXCIX", RomanNumeral.toRoman(3999));
    }
}

// ─────────────────────────────────────────────
// Exercice 6 – FizzBuzz
// ─────────────────────────────────────────────
class Exo6Test {

    @Test
    void testZeroOrNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FizzBuzz.fizzBuzz(0));
    }

    @Test
    void testFizzBuzz() {
        assertEquals("FizzBuzz", FizzBuzz.fizzBuzz(15));
    }

    @Test
    void testFizz() {
        assertEquals("Fizz", FizzBuzz.fizzBuzz(9));
    }

    @Test
    void testBuzz() {
        assertEquals("Buzz", FizzBuzz.fizzBuzz(10));
    }

    @Test
    void testNumber() {
        assertEquals("7", FizzBuzz.fizzBuzz(7));
    }
}
