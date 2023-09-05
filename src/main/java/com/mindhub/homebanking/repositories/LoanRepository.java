package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

   Loan findLoanById(Long id);
}
