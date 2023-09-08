package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getClientDTO();
    Client findById(long id);
    ClientDTO getClientDTO(Long id);
    void save(Client client);
    Client findByEmail(String email);
    ClientDTO getAll(String email);
}
