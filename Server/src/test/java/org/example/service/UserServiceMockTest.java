package org.example.service;

import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.Pair;
import org.example.dto.RequestAction;
import org.example.dto.request.AuthRequest;
import org.example.exception.BusinessException;
import org.example.exception.RoleNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Budget;
import org.example.model.Role;
import org.example.model.User;
import org.example.security.JwtProvider;
import org.example.security.PasswordEncoder;
import org.example.util.SessionManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceMockTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserDAO userDAO;
    @Mock
    private RoleDAO roleDAO;
    @Mock
    private SessionManager sessionManager;

    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        Mockito.when(sessionManager.openSession()).thenReturn(session);
        Mockito.when(session.beginTransaction()).thenReturn(transaction);
    }

    @Test
    public void signIn_correctPassword() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_IN, "user", "123");
        User user = new User(1L, "user", "123", Set.of());
        String expectedAccessToken = "access-token";
        String expectedRefreshToken = "refresh-token";

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("123", user.getPassword())).thenReturn(true);

        Mockito.when(jwtProvider.generateAccessToken(1L)).thenReturn(expectedAccessToken);
        Mockito.when(jwtProvider.generateRefreshToken(1L)).thenReturn(expectedRefreshToken);

        var actualTokens = userService.signIn(authRequest);

        Assertions.assertEquals(new Pair<>(expectedAccessToken, expectedRefreshToken), actualTokens);

        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(passwordEncoder).matches("123", user.getPassword());
        Mockito.verify(jwtProvider).generateAccessToken(1L);
        Mockito.verify(jwtProvider).generateRefreshToken(1L);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(transaction).commit();
        Mockito.verify(session).close();
    }

    @Test
    public void signIn_wrongPassword_throwsException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_IN, "user", "123");
        User user = new User(1L, "user", "123", Set.of());

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("123", user.getPassword())).thenReturn(false);

        Assertions.assertThrows(BusinessException.class, () -> {
            userService.signIn(authRequest);
        });

        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(passwordEncoder).matches("123", user.getPassword());
        Mockito.verify(session).close();
    }

    @Test
    public void signIn_userNotFound_throwsException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_IN, "user", "123");

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.signIn(authRequest);
        });

        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(session).close();
    }

    @Test
    public void signIn_databaseError_throwsRuntimeException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_IN, "user", "123");

        Mockito.when(userDAO.findByUsername(session, "user"))
                .thenThrow(new RuntimeException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.signIn(authRequest);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(transaction).rollback();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(session).close();
    }

    @Test
    public void signUp() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_UP, "user", "123");
        Role role = new Role(1L, "USER");
        String expectedAccessToken = "access-token";
        String expectedRefreshToken = "refresh-token";

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.empty());
        Mockito.when(roleDAO.findById(session, 1L)).thenReturn(Optional.of(role));

        Mockito.doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return null;
        }).when(session).persist(Mockito.any(User.class));

        Mockito.when(jwtProvider.generateAccessToken(1L)).thenReturn(expectedAccessToken);
        Mockito.when(jwtProvider.generateRefreshToken(1L)).thenReturn(expectedRefreshToken);

        var actualTokens = userService.signUp(authRequest);

        Assertions.assertEquals(new Pair<>(expectedAccessToken, expectedRefreshToken), actualTokens);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(transaction).commit();
        Mockito.verify(session).close();

        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(passwordEncoder).encode("123");
        Mockito.verify(roleDAO).findById(session, 1L);
        Mockito.verify(jwtProvider).generateAccessToken(1L);
        Mockito.verify(jwtProvider).generateRefreshToken(1L);
        Mockito.verify(session).persist(Mockito.any(User.class));
        Mockito.verify(session).persist(Mockito.any(Budget.class));
    }

    @Test
    public void signUp_userAlreadyExists_throwsException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_UP, "user", "123");
        User user = new User(1L, "user", "123", Set.of());

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signUp(authRequest);
        });

        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    @Test
    public void signUp_roleNotFound_throwsException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_UP, "user", "123");

        Mockito.when(userDAO.findByUsername(session, "user")).thenReturn(Optional.empty());
        Mockito.when(roleDAO.findById(session, 1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RoleNotFoundException.class, () -> {
            userService.signUp(authRequest);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(userDAO).findByUsername(session, "user");
        Mockito.verify(roleDAO).findById(session, 1L);
    }

    @Test
    public void signUp_dataBaseError_throwsException() {
        AuthRequest authRequest = new AuthRequest(RequestAction.SIGN_UP, "user", "123");

        Mockito.when(userDAO.findByUsername(session, "user")).thenThrow(new RuntimeException("DataBase Error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.signUp(authRequest);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).rollback();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(userDAO).findByUsername(session, "user");
    }
}