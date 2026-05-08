package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    // Mockar servicos relacionados, ele torna a implementacao dos métodos vazio.
    @Mock
    private UserService userService;
    @Mock
    private TransactionRepository repository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AuthorizationService authorizationService;

    @Autowired
    // Instruir o mockito a usar as classes e métodos mockados ao invés do real.
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create transaction successfully when everything is ok")
    void createTransactionCase1() throws Exception{
        User sender = new User(1L, "Daniel", "Canto", "11122233344",
                "daniel@teste.com", "senha123", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Mariana", "Canto", "11122233345",
                "mariana@teste.com", "senha123", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);

        transactionService.createTransaction(request);

        verify(repository, times(1)).save(any());

          Assertions.assertEquals(new BigDecimal(0), sender.getBalance());
        Assertions.assertEquals(new BigDecimal(20), receiver.getBalance());
        verify(userService, times(1)).saveUser(sender);
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transacao realizada com sucesso!");
        verify(notificationService, times(1)).sendNotification(receiver, "Transacao recebida com sucesso!");

    }

    @Test
    @DisplayName("Should throw exception when transaction is not allowed")
    void createTransactionCase2() throws Exception {
        User sender = new User(1L, "Daniel", "Canto", "11122233344",
                "daniel@teste.com", "senha123", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Mariana", "Canto", "11122233345",
                "mariana@teste.com", "senha123", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = assertThrows(Exception.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transacao não autorizada", thrown.getMessage());
    }
}