package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public List<AccountDTO>getAccounts(){
        return  accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }
    @GetMapping("/accounts/{id}")
    public AccountDTO getAccountById(@PathVariable Long id){
        Optional<Account> accountOptional=accountRepository.findById(id);
        return new AccountDTO(accountOptional.get());
    }

    @Autowired
    private ClientRepository clientRepository;

    //creo la cuenta del cliente
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> newAccount (Authentication authentication){
        Client client= clientRepository.findByEmail(authentication.getName());
        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("No se pudo crerar la cuenta: Usted ya llegó al limite de numeros de cuentas registradas", HttpStatus.FORBIDDEN);
        }
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


    //veo que la cuenta sea del cliente
    @RequestMapping("/clients/current/accounts")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication) {

       Client client= clientRepository.findByEmail(authentication.getName()) ;
       Account account= accountRepository.findById(id).orElse(null);
       if (account == null){
           return new ResponseEntity<>("Cuenta no valida", HttpStatus.BAD_GATEWAY);
       }

           if (account.getClient().equals(client)) {
               AccountDTO accountDTO = new AccountDTO(account);
               return new ResponseEntity(accountDTO, HttpStatus.ACCEPTED);
           } else {
               return new ResponseEntity<>("Esta cuenta no pertenece al cliente", HttpStatus.I_AM_A_TEAPOT);
           }

    }

}
