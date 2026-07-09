package com.revature.domain;

//@ExtendWith(MockitoExtension.class) enables Mockito in JUnit6
//@Mock creates a mock object
//@InjectMocks creates the SUT and injects mocks into it
//Three ways to create mocks: @Mock, Mockito.mock(), MockitoAnnotations.openMocks()

//Required Dependencies
//mockito-core
//mockito-junit-jupiter

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito Setup Demo")
public class Demo_Mockito_Setup {

    //Creating Mocks

    @Mock
    private UserRepository repository; // This is a mock!

    @Mock
    private EmailClient emailClient; //This is also a mock!

    //@InjectMocks creates the real object and injects mocks
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Basic mock creating with @Mock annotation")
    void demonstrateMockCreation(){
        // The repository is a mock - not a real implementation
        assertNotNull(repository, "Mock should be created");

        //By Default, mocks return null/false/0/empty
        assertNull(repository.findById(1L).orElse(null),
                "Unstubbed mock returns empty optional");

        assertEquals(0, repository.count(),
                "Unstubbed mock return 0 for primitives");

        assertFalse(repository.existsByEmail("test@test.com"),
                "Unstubbed mock returns false for boolean");
    }

    //Basic stubbing - making mocks return values
    @Test
    @DisplayName("Stubbing mock to return specific value")
    void demonstrateBasicStubbing() {
        //ARRANGE : Define what the mock should return

        User mockUser = new User(1L, "John Doe", "john@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(mockUser));

        //ACT: Call the service (which uses the mock)
        User result = userService.getUser(1L);

        //ASSERT: Verify we got the stubbed value
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Mock returns stubbed value only for matching arguments")
    void demonstrateArgumentMatching(){
        // Stub for ID 1
        when(repository.findById(1L))
                .thenReturn(Optional.of(new User(1L,"John","john@test.com")));

        // Stub for ID 2
        when(repository.findById(2L))
                .thenReturn(Optional.of(new User(2L,"Jane","jane@test.com")));

        // ID 1 returns John
        assertEquals("John",userService.getUser(1L).getName());

        // ID 2 returns Jane
        assertEquals("Jane",userService.getUser(2L).getName());

        // ID 3 is not stubbed - throws exception (from UserService)
        assertThrows(UserService.UserNotFoundException.class,()-> userService.getUser(3L));

    }

    //Creating Mocks Programmatically
    @Test
    @DisplayName("Alternative: Create mock with Mockito.mock()")
    void demonstrateProgrammaticMockCreation(){
        //Create mock programatically
        UserRepository programaticMock = mock(UserRepository.class);

        //stub it
        when(programaticMock.count()).thenReturn(42L);

        //Create service with programmatic mock
        UserService service = new UserService(programaticMock);

        //Verify
        assertEquals(42,service.getUserCount());
    }

    //The @InjectMocks Magic
    @Test
    @DisplayName("@InjectMocks automatically injects @Mock fields")
    void demonstrateInjectMocks(){
        //userService was create by @InjectMocks (above)
        //It automatically received the @Mock repository and emailClient

        //Stub repositoru
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.save(any(User.class)))
                .thenAnswer(inv->{
                    User u = inv.getArgument(0);
                    u.setId(100L);
                    return u;
                });

        //stub email client
        when(emailClient.send(anyString(),anyString(), anyString()))
                .thenReturn(true);

        //Act
        User created = userService.createUser("Test User", "test@example.com");

        //Assert
        assertNotNull(created);
        assertEquals(100L,created.getId());
        assertEquals("Test User",created.getName());


    }

    @Test
    @DisplayName("Dependency Injection enable testing")
    void demonstrateDependencyInjection(){
        //We can create services with any combination of real/mock dependences
        UserRepository mockRepo = mock(UserRepository.class); //or could be real
        EmailClient mockEmail = mock(EmailClient.class); //or could be real

        UserService testService = new UserService(mockRepo,mockEmail);

        //Now we control exactly what the service uses
        when(mockRepo.count()).thenReturn(999L);
        assertEquals(999L,testService.getUserCount());
    }







}
