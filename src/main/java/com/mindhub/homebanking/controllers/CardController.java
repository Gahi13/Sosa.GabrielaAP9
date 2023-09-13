package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")

public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;
    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return cardService.getCards() ;
    }
    @GetMapping("/cards/{id}")
    public CardDTO getCardById(@PathVariable Long id){
        /*
        Optional<Card> cardOptional=cardRepository.findById(id);

        return new CardDTO(cardOptional.get());
    */
        return cardService.getCardDTO(id);
    }


    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> newCard(
            @RequestParam CardColor cardColor, @RequestParam CardType cardType, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());

        long cuentaDelMismoType= client.getCards().stream().filter(card -> card.getType()==cardType).count();
        long cardDelMismoTypeAndColor= client.getCards().stream().filter(card -> card.getType()==cardType && card.getColor()== cardColor).count();
        if(cardDelMismoTypeAndColor >=1 || cuentaDelMismoType >= 3){
            return new ResponseEntity<>("Ya existe este color de tarjeta", HttpStatus.FORBIDDEN);
        }


        //int createCvv =getRandomNumber(0,999);
        int createCvv = CardUtils.getRandomNumber(0,999);
        String createNumberCard= CardUtils.getRandomNumber(0,9999)+"-"+ CardUtils.getRandomNumber(0,9999)+"-"+ CardUtils.getRandomNumber(0,9999)+"-"+ CardUtils.getRandomNumber(0,9999);
        Card newCard= new Card(client.getFirstName() + client.getLastName(),cardType, cardColor, createNumberCard, createCvv, LocalDate.now().plusYears(5), LocalDate.now());
        client.addCard(newCard);
        cardService.save(newCard);
        return new ResponseEntity<>("tarjeta creada", HttpStatus.CREATED);
    }



    @RequestMapping("/clients/current/cards")
    public ResponseEntity<Object> getCard(@PathVariable Long id, Authentication authentication) {

        Client client= clientService.findByEmail(authentication.getName()) ;
        Card card= cardService.findById(id);
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
