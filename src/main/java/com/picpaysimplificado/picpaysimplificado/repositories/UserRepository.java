package com.picpaysimplificado.picpaysimplificado.repositories;

import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Para o repository, passamos como parâmetro genérico a classe da nossa entidade e o tipo
// de dado das suas chaves primárias.
public interface UserRepository extends JpaRepository<User, Long> {
    // O Optional serve para indicar que nem sempre um usuário pode ser retornado. Além disso, o JPA
    // já lida com essa query sozinho, por conter o find, nome da tabela e campo.
    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(Long id);
}
