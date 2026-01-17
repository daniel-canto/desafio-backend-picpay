package com.picpaysimplificado.picpaysimplificado.domain.transaction;

import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name="transactions")
@Table(name="transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name="sender_id")
    // O @ManyToOne faz com que nesse caso, um usuário possua várias transacões,
    // enquanto uma transacao tem um usuario atrelado (dois no caso, pois um sera o sender e outro o receiver)
    private User sender;
    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;
    private LocalDateTime timestamp;

}
