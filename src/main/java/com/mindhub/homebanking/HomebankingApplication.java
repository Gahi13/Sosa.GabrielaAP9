package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository){
		return (args )-> {
			//nuevo cliente (Melba)
			Client client= new Client("Melba", "Morel", "melba@mindhub.com");
			clientRepository.save(client);
						//nuevas cuentas para cliente
			LocalDate today =  LocalDate.now();
			Account newAccount1= new Account("VIN001", today, 5000.0);
			client.addAccount(newAccount1);
			accountRepository.save(newAccount1);
			Account newAccount2= new Account("VIN002", today.plusDays(1), 7500.0);
			client.addAccount(newAccount2);
			accountRepository.save(newAccount2);

						//nuevas transacciones para las cuentas del cliente
			Transaction newTransaction1= new Transaction(TransactionType.DEBIT, -1235.2, "aca va la descripcion de la transaction 1", today);
			newAccount1.addTransaction(newTransaction1); 	//transaccion para la cuenta 1 de cliente
			transactionRepository.save(newTransaction1);
			Transaction newTransaction2= new Transaction(TransactionType.CREDIT, 123654.2, "otra desctripcion", today);
			newAccount2.addTransaction(newTransaction2); 	//transaccion para la cuenta 2 del cliente
			transactionRepository.save(newTransaction2);
			Transaction newTransaction3= new Transaction(TransactionType.CREDIT, 365494.2, "una descripcion", today);
			newAccount1.addTransaction(newTransaction3); 	//transaccion para la cuenta 2 del cliente
			transactionRepository.save(newTransaction3);



					//nuevo cliente 1 (inventado)
			Client client1= new Client("Gabriela", "sosa", "sosa@gmail");
			clientRepository.save(client1);
			Account account1= new Account("VIN003", today, 600.0);
			client1.addAccount(account1);
			accountRepository.save(account1);
			Transaction transaction1= new Transaction(TransactionType.CREDIT, 1254.2, "No tengo imaginacion", today);
			account1.addTransaction(transaction1);
			transactionRepository.save(transaction1);

			//loans
			Loan newLoan1 = new Loan("Hipotecario", 500000.0, List.of(12,24,36,48,60));
			loanRepository.save(newLoan1);
			Loan newLoan2= new Loan("Personal", 100000.0, List.of(6,12,24));
			loanRepository.save(newLoan2);
			Loan newLoan3= new Loan("Automotriz", 300000.0, List.of(6,12,24,36));
			loanRepository.save(newLoan3);



			//clientLoan Melba(cliente) y cliente1
			ClientLoan newClientLoanMelba1= new ClientLoan(400000.0, 60, client, newLoan1);
			clientLoanRepository.save(newClientLoanMelba1);
			ClientLoan newClientLoanMelba2= new ClientLoan(50000.0,12,client,newLoan3);
			clientLoanRepository.save(newClientLoanMelba2);

			ClientLoan newClientLoanCliente1= new ClientLoan(100000.0,24, client1, newLoan2);
			clientLoanRepository.save(newClientLoanCliente1);
			ClientLoan neWClientLoanClient2= new ClientLoan(200000.0, 36, client1, newLoan3);
			clientLoanRepository.save(neWClientLoanClient2);

		};

	}

}
