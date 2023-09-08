package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {


    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/accounts")
    public List<AccountDTO>getAccounts(){
        return  accountService.getAccounts();
    }
    @RequestMapping("/clients/current/accounts")
    public List<AccountDTO> getAccount( Authentication authentication) {
        Client client= clientService.findByEmail(authentication.getName()) ;
        return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());

    }
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long id, Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        Account account= accountService.findByIdAndClient(id, client);

        if (account == null) {
            return new ResponseEntity<>("no autorizado", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(new AccountDTO(account), HttpStatus.ACCEPTED);
    }

    //creo la cuenta del cliente
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> newAccount (Authentication authentication){
        Client client= clientService.findByEmail(authentication.getName());
        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("No se pudo crerar la cuenta: Usted ya llegó al limite de numeros de cuentas registradas", HttpStatus.FORBIDDEN);
        }
        boolean uniqueNumber = false;
        while(!uniqueNumber){
            int numberRandom = getRandomNumber(0, 99999999);
            String numberAccount= "VIN-"+numberRandom;
            if(accountService.findByNumber(numberAccount)== null){
                uniqueNumber=true;
                Account newAccount = new Account(numberAccount, LocalDate.now(), 0.0);
                client.addAccount(newAccount);
                accountService.save(newAccount);
            }
        }
        return new ResponseEntity<>("cuenta creada", HttpStatus.CREATED);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }





}
