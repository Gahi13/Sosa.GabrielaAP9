package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return (args )-> {
			Client client= new Client("Melba", "Morel", "melba@mindhub.com");

			clientRepository.save(client);
			Client client1= new Client("Gabr", "sa", "hgmail");
			clientRepository.save(client1);

			LocalDate today =  LocalDate.now();
			Account newAccount1= new Account("VIN001", today, 5000.0);
			client.addAccount(newAccount1);
			accountRepository.save(newAccount1);
			Account newAccount2= new Account("VIN002", today.plusDays(1), 7500.0);
			client.addAccount(newAccount2);
			accountRepository.save(newAccount2);




		};

	}

}
