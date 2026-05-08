package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;



public class AuthorizationService {
    @Autowired
    private RestTemplate restTemplate;

    public boolean authorizeTransaction(User sender, BigDecimal value) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return (boolean) data.get("authorization");
            }
        } catch (HttpClientErrorException.Forbidden e) {
            // A API retornou 403, ou seja, não autorizado
            return false;
        } catch (Exception e) {
            // Outros erros
            return false;
        }
        return false;
    }
}
