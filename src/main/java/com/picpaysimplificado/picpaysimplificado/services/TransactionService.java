package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository repository;
//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AuthorizationService authorizationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
            throw new Exception("Transacao não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        // Subtrai o amount do sender pelo valor da transacao
        sender.setBalance(sender.getBalance().subtract(transaction.value()));

        // Soma o amount do receiver pelo valor da transacao
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.repository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transacao realizada com sucesso!");
        this.notificationService.sendNotification(receiver, "Transacao recebida com sucesso!");

        return newTransaction;
    }

//    public boolean authorizeTransaction(User sender, BigDecimal value) {
//        try {
//            ResponseEntity<Map> response = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
//                return (boolean) data.get("authorization");
//            }
//        } catch (HttpClientErrorException.Forbidden e) {
//            // A API retornou 403, ou seja, não autorizado
//            return false;
//        } catch (Exception e) {
//            // Outros erros
//            return false;
//        }
//        return false;
//    }
}
