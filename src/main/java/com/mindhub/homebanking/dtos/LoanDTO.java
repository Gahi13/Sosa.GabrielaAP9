package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Loan;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LoanDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String name;
    private Double maxAmount;
    @ElementCollection
    @Column(name="payments")
    private List<Integer> payments = new ArrayList<>();
    private Set<ClientLoanDTO>clientLoans;

    public LoanDTO(Loan loan){
        this.id = loan.getId();
        this.name= loan.getName();
        this.maxAmount= loan.getMaxAmount();
        this.payments= loan.getPayments();

        this.clientLoans=loan.getClientLoans()
                .stream()
                .map(loanClient -> new ClientLoanDTO(loanClient))
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public Set<ClientLoanDTO> getClientLoans() {
        return clientLoans;
    }

    public void setClientLoans(Set<ClientLoanDTO> clientLoans) {
        this.clientLoans = clientLoans;
    }
}
