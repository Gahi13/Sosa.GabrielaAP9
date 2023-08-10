package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args )-> {
			//nuevo cliente (Melba)
			Client client= new Client("Melba", "Morel", "melba@mindhub.com");
			clientRepository.save(client);
						//nuevas cuentas para cliente 1
			LocalDate today =  LocalDate.now();
			Account newAccount1= new Account("VIN001", today, 5000.0);
			client.addAccount(newAccount1);
			accountRepository.save(newAccount1);
			Account newAccount2= new Account("VIN002", today.plusDays(1), 7500.0);
			client.addAccount(newAccount2);
			accountRepository.save(newAccount2);
						//nuevas transacciones para las cuentas del cliente 1
			Transaction newTransaction1= new Transaction(TransactionType.DEBIT, 1235.2, "aca va la descripcion de la transaction 1", today);
			newAccount1.addTransaction(newTransaction1); 	//transaccion para la cuenta 1 de cliente 1
			transactionRepository.save(newTransaction1);
			Transaction newTransaction2= new Transaction(TransactionType.CREDIT, 123654.2, "otra desctripcion", today);
			newAccount2.addTransaction(newTransaction2); 	//transaccion para la cuenta 2 del cliente 1
			transactionRepository.save(newTransaction2);
			Transaction newTransaction3= new Transaction(TransactionType.CREDIT, 365494.2, "una descripcion", today);
			newAccount1.addTransaction(newTransaction3); 	//transaccion para la cuenta 2 del cliente 1
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


		};

	}

}
