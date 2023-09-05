package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> solicitarLoan (@RequestBody LoanApplicationDTO loanApplicationDTO,
                                                 Authentication authentication){
        Client client= clientRepository.findByEmail(authentication.getName());
        double getAmounts= loanApplicationDTO.getAmount();
        int getPayments= loanApplicationDTO.getPayments();
        long getLoanIds= loanApplicationDTO.getLoanId();
        String getToAccountNumber= loanApplicationDTO.getToAccountNumber();

        if(getAmounts<=0 || getPayments<=0){
            return new ResponseEntity<>("datos incorrectos", HttpStatus.BAD_REQUEST);
        }
        Optional<Loan> optionalLoan= loanRepository.findById(getLoanIds);
        if(optionalLoan.isEmpty()){
            return new ResponseEntity<>("el prestamo no existe", HttpStatus.BAD_REQUEST);
        }
        Loan loan= optionalLoan.get();
        Account numberAccount=accountRepository.findByNumber(getToAccountNumber);
        if(!numberAccount.getClient().equals(client)){
            return new ResponseEntity<>("la cuenta de destino no pertenece al clente", HttpStatus.BAD_REQUEST);
        }
        Loan getLoan= optionalLoan.get();
        double credit=  getAmounts+(0.20*getAmounts);
        String description= getLoan.getName() + " Loan approved";

        accountRepository.findByNumber(getToAccountNumber).setBalance(accountRepository.findByNumber(getToAccountNumber).getBalance()+credit);

        Transaction transactionCredit= new Transaction(TransactionType.CREDIT, credit, description, LocalDate.now());
        numberAccount.addTransaction(transactionCredit);
        transactionRepository.save(transactionCredit);
        accountRepository.save(accountRepository.findByNumber(getToAccountNumber));
        Loan newLoan= new Loan(getLoan.getName(),getAmounts, getLoan.getPayments() );
        loanRepository.save(loan);
        return new ResponseEntity<>("aceptado", HttpStatus.ACCEPTED);

    }
}
