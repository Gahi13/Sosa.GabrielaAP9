package com.mindhub.homebanking;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class RepositoriesTest {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Test
    public void existLoans(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans,is(not(empty())));
    }
    @Test
    public void existPersonalLoan(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
    }
    @Test
    public void existClient(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients,is(not(empty())));
    }
    @Test
    public void firstNameNotNull() {
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, allOf(hasItem(hasProperty("firstName", notNullValue()))));
    }
    @Test
    public void existAccount(){
        List<Account>accounts= accountRepository.findAll();
        assertThat(accounts, is(not(empty())));
    }
    @Test
    public void numberAccountNotNull(){
        List<Account> accounts= accountRepository.findAll();
        assertThat(accounts, allOf(hasItem(hasProperty("number", is(notNullValue())))));
    }

    @Test
    public void existCard(){
        List<Card> cards= cardRepository.findAll();
        assertThat(cards, is(not(empty())));
    }
    @Test
    public void cardNumberNotNull(){
        List<Card> cards= cardRepository.findAll();
        assertThat(cards, is(not(nullValue())));
    }
    @Test
    public void existTransaction(){
        List<Transaction> transactions= transactionRepository.findAll();
        assertThat(transactions, is(not(empty())));
    }
    @Test
    public void existTransactionAccount(){
        List<Transaction> transactions= transactionRepository.findAll();
        assertThat(transactions, allOf(hasItem(hasProperty("account", is(notNullValue())))));
    }

}
