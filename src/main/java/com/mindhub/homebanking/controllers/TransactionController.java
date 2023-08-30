package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired TransactionRepository transactionRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired ClientRepository clientRepository;
    @PostMapping("/transactions")
    @Transactional
    //api/transactions?fromAccountNumber=${this.accountFromNumber}&toAccountNumber=${this.accountToNumber}&amount=${this.amount}&description=${this.description}
    public ResponseEntity<Object> creationTransaction(
            @RequestParam String fromAccountNumber,
            @RequestParam String toAccountNumber,
            @RequestParam double amount,
            @RequestParam String description,
            Authentication authentication
                                               ){
        Client client= clientRepository.findByEmail(authentication.getName()) ;
        if (description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.equals(toAccountNumber)){
            return  new ResponseEntity<>("los numeros de cuenta no deben ser las mismos", HttpStatus.FORBIDDEN );
        }
        Account fromNumber= accountRepository.findByNumber(fromAccountNumber);
        Account toNumber= accountRepository.findByNumber(toAccountNumber);
        if (!fromNumber.getClient().equals(client)){
            return new ResponseEntity<>("La cuenta no pertenece al cliente", HttpStatus.FORBIDDEN);
        }
        if(fromNumber.getBalance() < amount){
            return new ResponseEntity<>("no tiene suficientes fondos para realizar la transaccion", HttpStatus.FORBIDDEN);
        }
        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, -amount, description, LocalDate.now());
        Transaction transactionCredit=new Transaction(TransactionType.CREDIT, amount, description, LocalDate.now());

        Account debitAccount= accountRepository.findByNumber(fromAccountNumber);
        debitAccount.addTransaction(transactionDebit);
        transactionRepository.save(transactionDebit);

        Account creditAccount= accountRepository.findByNumber(toAccountNumber);
        creditAccount.addTransaction(transactionCredit);
        transactionRepository.save(transactionCredit);

        fromNumber.setBalance(fromNumber.getBalance()-amount);
        toNumber.setBalance(toNumber.getBalance()+amount);

        accountRepository.save(fromNumber);
        accountRepository.save(toNumber);
        return new ResponseEntity<>("transferencia realizada con exito", HttpStatus.CREATED);

    }
}
