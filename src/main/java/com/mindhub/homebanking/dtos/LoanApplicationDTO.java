package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LoanApplicationDTO {

   // loanTypeId, amount: this.amount, payments: this.payments, toAccountNumber: this.accountToNumber
    private Long loanId;
    private Double amount;
    private Integer payments ;
    private String toAccountNumber;

        public LoanApplicationDTO(long loanId, double amount, int payments, String toAccountNumber) {
            this.loanId = loanId;
            this.amount = amount;
            this.payments = payments;
            this.toAccountNumber = toAccountNumber;
        }

    public Long getLoanId() {
        return loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
