package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientLoanDTO {
    private Long id;
    private Long idPrestamo;
    private String name;
    private Double amount;

    private Integer payments ;
    private Set<ClientDTO> clientLoans = new HashSet<>();

    public ClientLoanDTO(ClientLoan clientLoan){
        id=clientLoan.getId();
        idPrestamo= clientLoan.getLoan().getId();
        name= clientLoan.getLoan().getName();
        amount= clientLoan.getAmount();
        payments=clientLoan.getPayments();
    }

    public Long getId() {
        return id;
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public Set<ClientDTO> getClientLoans() {
        return clientLoans;
    }
}












