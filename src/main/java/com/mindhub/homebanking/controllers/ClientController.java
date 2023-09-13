package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.ClientUtils;
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
    private ClientService clientService;
    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.getClientDTO();
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClientById(@PathVariable Long id) {
        return clientService.getClientDTO(id);
    }
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private AccountService accountService;
        @PostMapping("/clients")
        public ResponseEntity<Object> register(
                @RequestParam String firstName, @RequestParam String lastName,
                @RequestParam String email, @RequestParam String password) {


            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }
            if (clientService.findByEmail(email) != null) {
                return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
            }
            Client newClient= new Client(firstName, lastName, email, passwordEncoder.encode(password));
            clientService.save(newClient);
            boolean uniqueNumber = false;
            while(!uniqueNumber){
                int numberRandom = ClientUtils.getRandomNumber(0, 99999999);
                String numberAccount= "VIN-"+numberRandom;
                if(accountService.findByNumber(numberAccount)== null){
                    uniqueNumber=true;
                    Account newAccount = new Account(numberAccount, LocalDate.now(), 0.0);
                    newClient.addAccount(newAccount);
                    accountService.save(newAccount);
                }
            }
            return new ResponseEntity<>("cuenta creada", HttpStatus.CREATED);


        }
        @GetMapping("/clients/current")
        public ClientDTO getAll(Authentication authentication) {

            return clientService.getAll(authentication.getName());
        }

}
