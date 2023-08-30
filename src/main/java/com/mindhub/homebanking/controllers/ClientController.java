package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")


public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(Collectors.toList());
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClientById(@PathVariable Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        return new ClientDTO(clientOptional.get());
    }


        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired AccountRepository accountRepository;
        @RequestMapping(path = "/clients", method = RequestMethod.POST)
        public ResponseEntity<Object> register(
                @RequestParam String firstName, @RequestParam String lastName,
                @RequestParam String email, @RequestParam String password) {


            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }
            if (clientRepository.findByEmail(email) != null) {
                return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
            }
            Client client= clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));

            boolean uniqueNumber = false;
            while(!uniqueNumber){
                int numberRandom = getRandomNumber(0, 99999999);
                String numberAccount= "VIN-"+numberRandom;
                if(accountRepository.findByNumber(numberAccount)== null){
                    uniqueNumber=true;
                    Account newAccount = new Account(numberAccount, LocalDate.now(), 0.0);
                    client.addAccount(newAccount);
                    accountRepository.save(newAccount);
                }
            }
            return new ResponseEntity<>("cuenta creada", HttpStatus.CREATED);


        }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


        @RequestMapping("/clients/current")

        public ClientDTO getAll(Authentication authentication) {

            return new ClientDTO(clientRepository.findByEmail(authentication.getName())) ;
        }


}
