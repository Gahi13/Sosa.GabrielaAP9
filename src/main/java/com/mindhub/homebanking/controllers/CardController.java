package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
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

public class CardController {
    @Autowired
    private CardRepository cardRepository;
    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return  cardRepository.findAll().stream().map(card -> new CardDTO(card)).collect(Collectors.toList());
    }
    @GetMapping("/cards/{id}")
    public CardDTO getCardById(@PathVariable Long id){
        Optional<Card> cardOptional=cardRepository.findById(id);
        return new CardDTO(cardOptional.get());
    }
    @Autowired ClientRepository clientRepository;
    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> newCard(
            @RequestParam CardColor cardColor, @RequestParam CardType cardType, Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        long cuentaDelMismoType= client.getCards().stream().filter(card -> card.getType()==cardType).count();
        if ( cuentaDelMismoType >= 3) {
            return new ResponseEntity<>("No se pudo crear una nueva tarjeta: Usted ya llegó al limite de numeros de tarjetas registradas", HttpStatus.FORBIDDEN);
        }

        int createCvv =getRandomNumber(0,999);
        String createNumberCard= getRandomNumber(0,9999)+"-"+getRandomNumber(0,9999)+"-"+getRandomNumber(0,9999)+"-"+getRandomNumber(0,9999);
        Card newCard= new Card(client.getFirstName() + client.getLastName(),cardType, cardColor, createNumberCard, createCvv, LocalDate.now().plusYears(5), LocalDate.now());
        client.addCard(newCard);
        cardRepository.save(newCard);
        return new ResponseEntity<>("tarjeta creada", HttpStatus.CREATED);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    @RequestMapping("/clients/current/cards")
    public ResponseEntity<Object> getCard(@PathVariable Long id, Authentication authentication) {

        Client client= clientRepository.findByEmail(authentication.getName()) ;
        Card card= cardRepository.findById(id).orElse(null);
        if (card == null){
            return new ResponseEntity<>("Cuenta no valida", HttpStatus.BAD_GATEWAY);
        }

        if (card.getClient().equals(client)) {
            CardDTO cardDTO = new CardDTO(card);
            return new ResponseEntity(cardDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Esta cuenta no pertenece al cliente", HttpStatus.I_AM_A_TEAPOT);
        }

    }
    }
