package com.udea.lab12026p.service;

import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.entity.Customer;
import com.udea.lab12026p.entity.Transaction;
import com.udea.lab12026p.repository.CustomerRepository;
import com.udea.lab12026p.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository; // Para validar cuentas

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        validateTransfer(transactionDTO);

        // Buscar los clientes por número de cuenta
        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del remitente no existe."));
        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del receptor no existe."));
//    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        // Validar que el remitente tenga saldo suficiente
        if (sender.getBalance() < transactionDTO.getAmount()) {
            throw new IllegalArgumentException("Saldo insuficiente en la cuenta del remitente.");
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        // Realizar la transferencia
        sender.setBalance(sender.getBalance() - transactionDTO.getAmount());
        receiver.setBalance(receiver.getBalance() + transactionDTO.getAmount());

        // Guardar los cambios en las cuentas
        customerRepository.save(sender);
        customerRepository.save(receiver);


        // Crear y guardar la transacción
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        // Devolver la transacción creada como DTO
        TransactionDTO savedTransaction = new TransactionDTO();
        savedTransaction.setId(transaction.getId());
        savedTransaction.setSenderAccountNumber(transaction.getSenderAccountNumber());
        savedTransaction.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setTimestamp(transaction.getTimestamp());

        return savedTransaction;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        return transactionRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada."));
    }

    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO request) {
        validateTransfer(request);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada."));

        Customer oldSender = getCustomer(transaction.getSenderAccountNumber());
        Customer oldReceiver = getCustomer(transaction.getReceiverAccountNumber());
        if (oldReceiver.getBalance() < transaction.getAmount()) {
            throw new IllegalArgumentException("No se puede revertir la transacción porque el receptor no tiene saldo suficiente.");
        }
        oldSender.setBalance(oldSender.getBalance() + transaction.getAmount());
        oldReceiver.setBalance(oldReceiver.getBalance() - transaction.getAmount());

        Customer newSender = getCustomer(request.getSenderAccountNumber());
        Customer newReceiver = getCustomer(request.getReceiverAccountNumber());
        if (newSender.getBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Saldo insuficiente en la cuenta del remitente.");
        }
        newSender.setBalance(newSender.getBalance() - request.getAmount());
        newReceiver.setBalance(newReceiver.getBalance() + request.getAmount());

        customerRepository.save(oldSender);
        customerRepository.save(oldReceiver);
        customerRepository.save(newSender);
        customerRepository.save(newReceiver);

        transaction.setSenderAccountNumber(request.getSenderAccountNumber());
        transaction.setReceiverAccountNumber(request.getReceiverAccountNumber());
        transaction.setAmount(request.getAmount());
        return toDTO(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada."));
        Customer sender = getCustomer(transaction.getSenderAccountNumber());
        Customer receiver = getCustomer(transaction.getReceiverAccountNumber());
        if (receiver.getBalance() < transaction.getAmount()) {
            throw new IllegalArgumentException("No se puede eliminar la transacción porque el receptor no tiene saldo suficiente.");
        }
        sender.setBalance(sender.getBalance() + transaction.getAmount());
        receiver.setBalance(receiver.getBalance() - transaction.getAmount());
        customerRepository.save(sender);
        customerRepository.save(receiver);
        transactionRepository.delete(transaction);
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        List<Transaction> transactions = transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);
        return transactions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void validateTransfer(TransactionDTO transactionDTO) {
        if (transactionDTO == null || transactionDTO.getSenderAccountNumber() == null
                || transactionDTO.getReceiverAccountNumber() == null
                || transactionDTO.getSenderAccountNumber().isBlank()
                || transactionDTO.getReceiverAccountNumber().isBlank()) {
            throw new IllegalArgumentException("Debes indicar las dos cuentas.");
        }
        if (transactionDTO.getSenderAccountNumber().equals(transactionDTO.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("La cuenta de origen y la de destino deben ser diferentes.");
        }
        if (transactionDTO.getAmount() == null || !Double.isFinite(transactionDTO.getAmount())
                || transactionDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser un número positivo.");
        }
    }

    private Customer getCustomer(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta no existe: " + accountNumber));
    }

    private TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setSenderAccountNumber(transaction.getSenderAccountNumber());
        dto.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }
}
