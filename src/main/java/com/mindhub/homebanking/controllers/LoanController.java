package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
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
    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> solicitarLoan (@RequestBody LoanApplicationDTO loanApplicationDTO,
                                                 Authentication authentication){
        Client client= clientRepository.findByEmail(authentication.getName());
        double getAmounts= loanApplicationDTO.getAmount();
        int getPayments= loanApplicationDTO.getPayments();
        long getLoanIds= loanApplicationDTO.getLoanId();
        String getToAccountNumber= loanApplicationDTO.getToAccountNumber();
        Loan loan= loanRepository.findLoanById(loanApplicationDTO.getLoanId());
        List<Integer> payments= loan.getPayments();
        if(getAmounts<=0 || getPayments<=0){
            return new ResponseEntity<>("datos incorrectos", HttpStatus.BAD_REQUEST);
        }

        if(loan == null){
            return new ResponseEntity<>("el prestamo no existe", HttpStatus.BAD_REQUEST);
        }
        if(loan.getMaxAmount()<getAmounts){
            return new ResponseEntity<>("Usted se excedio del monto permitido", HttpStatus.FORBIDDEN);
        }
        if(!payments.contains(getPayments)){
            return new ResponseEntity<>("No existe esa cantidad de cuotas para ese prestamo", HttpStatus.FORBIDDEN);
        }
        //Loan loan= optionalLoan.get();
        Account numberAccount=accountRepository.findByNumber(getToAccountNumber);
        if(!numberAccount.getClient().equals(client)){
            return new ResponseEntity<>("la cuenta de destino no pertenece al clente", HttpStatus.BAD_REQUEST);
        }


        double credit=  getAmounts+(0.20*getAmounts);
        String description= loan.getName() + " Loan approved";
        ClientLoan newLoan= new ClientLoan(credit, getPayments, client, loan );
        loan.addLoans(newLoan);
        client.addLoan(newLoan);
        clientRepository.save(client);

        accountRepository.findByNumber(getToAccountNumber).setBalance(accountRepository.findByNumber(getToAccountNumber).getBalance()+credit);

        Transaction transactionCredit= new Transaction(TransactionType.CREDIT, credit, description, LocalDate.now());
        numberAccount.addTransaction(transactionCredit);
        transactionRepository.save(transactionCredit);
        accountRepository.save(numberAccount);
        clientLoanRepository.save(newLoan);
        return new ResponseEntity<>("aceptado", HttpStatus.ACCEPTED);

    }
}
