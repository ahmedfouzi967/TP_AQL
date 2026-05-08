package exercice1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void testGetUserById_returnsCorrectUser() {
        // ARRANGE
        User mockUser = new User(1L, "Alice");
        when(userRepository.findUserById(1L)).thenReturn(mockUser);

        // ACT
        User result = userService.getUserById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());

        // Vérifie que findUserById a bien été appelé avec l'argument 1L
        verify(userRepository, times(1)).findUserById(1L);
    }


    @Test
    void testGetUserById_userNotFound_returnsNull() {
        when(userRepository.findUserById(99L)).thenReturn(null);

        User result = userService.getUserById(99L);

        assertNull(result);
        verify(userRepository, times(1)).findUserById(99L);
    }


    @Test
    void testGetUserById_wrongId_doesNotReturnUser() {
        User mockUser = new User(1L, "Alice");
        when(userRepository.findUserById(1L)).thenReturn(mockUser);

        User result = userService.getUserById(1L);

        assertNotEquals(2L, result.getId());
        verify(userRepository).findUserById(1L);
    }


    @Test
    void testUser_getters() {
        User user = new User(5L, "Bob");

        assertEquals(5L, user.getId());
        assertEquals("Bob", user.getName());
    }


    @Test
    void testUser_toString() {
        User user = new User(3L, "Charlie");
        String str = user.toString();

        assertNotNull(str);
        assertTrue(str.contains("Charlie"));
        assertTrue(str.contains("3"));
    }


    @Test
    void testGetUserById_multipleUsers() {
        User alice = new User(1L, "Alice");
        User bob   = new User(2L, "Bob");
        when(userRepository.findUserById(1L)).thenReturn(alice);
        when(userRepository.findUserById(2L)).thenReturn(bob);

        assertEquals("Alice", userService.getUserById(1L).getName());
        assertEquals("Bob",   userService.getUserById(2L).getName());

        verify(userRepository).findUserById(1L);
        verify(userRepository).findUserById(2L);
    }
}
