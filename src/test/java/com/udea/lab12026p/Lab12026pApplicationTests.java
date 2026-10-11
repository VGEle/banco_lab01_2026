package com.udea.lab12026p;

import com.udea.lab12026p.dto.CustomerDTO;
import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.repository.CustomerRepository;
import com.udea.lab12026p.repository.TransactionRepository;
import com.udea.lab12026p.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class Lab12026pApplicationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TransactionService transactionService;

	@Test
	void contextLoads() {
	}

	@Test
	void transferUpdatesBalancesAndCreatesHistory() {
		customerRepository.deleteAll();
		transactionRepository.deleteAll();

		var sender = customerRepository.save(newCustomer("1001", 500.0));
		var receiver = customerRepository.save(newCustomer("1002", 100.0));

		var transfer = new TransactionDTO();
		transfer.setSenderAccountNumber(sender.getAccountNumber());
		transfer.setReceiverAccountNumber(receiver.getAccountNumber());
		transfer.setAmount(150.0);

		TransactionDTO result = transactionService.transferMoney(transfer);

		assertEquals(150.0, result.getAmount());
		assertEquals(1, transactionRepository.count());
		assertEquals(350.0, customerRepository.findByAccountNumber("1001").orElseThrow().getBalance());
		assertEquals(250.0, customerRepository.findByAccountNumber("1002").orElseThrow().getBalance());
	}

	@Test
	void transferRejectsInsufficientBalanceWithoutChangingAccounts() {
		customerRepository.deleteAll();
		transactionRepository.deleteAll();

		customerRepository.save(newCustomer("2001", 50.0));
		customerRepository.save(newCustomer("2002", 100.0));

		var transfer = new TransactionDTO();
		transfer.setSenderAccountNumber("2001");
		transfer.setReceiverAccountNumber("2002");
		transfer.setAmount(150.0);

		assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(transfer));
		assertEquals(50.0, customerRepository.findByAccountNumber("2001").orElseThrow().getBalance());
		assertEquals(100.0, customerRepository.findByAccountNumber("2002").orElseThrow().getBalance());
		assertEquals(0, transactionRepository.count());
	}

	private com.udea.lab12026p.entity.Customer newCustomer(String accountNumber, double balance) {
		var customer = new com.udea.lab12026p.entity.Customer();
		customer.setAccountNumber(accountNumber);
		customer.setFirstName("Test");
		customer.setLastName("User");
		customer.setBalance(balance);
		return customer;
	}

}
