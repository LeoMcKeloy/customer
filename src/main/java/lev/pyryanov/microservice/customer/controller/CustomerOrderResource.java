package lev.pyryanov.microservice.customer.controller;

import lev.pyryanov.microservice.customer.domain.Customer;
import lev.pyryanov.microservice.customer.domain.Order;
import lev.pyryanov.microservice.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping ("/api/v1")
@Slf4j
public class CustomerOrderResource {

    private static final String ENTITY_NAME = "order";

    @Value ("${spring.application.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;

    public CustomerOrderResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping ("/customerOrders/{customerId}")
    public ResponseEntity<Order> createOrder(@PathVariable String customerId, @Valid @RequestBody Order order) {
        log.debug("REST request to save Order : {} for Customer ID: {}", order, customerId);
        if (customerId.isBlank()) {
            throw new ResponseStatusException (
                    HttpStatus.NOT_FOUND, "No Customer: " + ENTITY_NAME);
        }
        final Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            final var customer = customerOptional.get();
            customer.addOrder(order);
            customerRepository.save(customer);
            return ResponseEntity.ok()
                    .body(order);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer: " + ENTITY_NAME);
        }
    }
}
