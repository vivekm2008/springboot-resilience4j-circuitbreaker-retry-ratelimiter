package com.javavm.us.service;

import com.javavm.us.dto.OrderDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {

    private static final String USER_SERVICE = "userService";
    private static final String BASEURL = "http://localhost:9191/orders";

    @Autowired
    private RestTemplate restTemplate;

    private int attemptCircuitBreaker = 1;
    private int attemptRetry = 1;

    private int attemptRateLimiter = 1;

    /**
     * Method used by controller when no category param is provided.
     * This returns local static list (no resilience applied).
     */
    public List<OrderDTO> getAllAvailableProducts() {
        return getFallbackOrders();
    }

    /**
     * Method annotated with CircuitBreaker. Fallback method must be in same class
     * and signature = (original params..., Throwable).
     */
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getAllAvailableProductsFallback")
    public List<OrderDTO> displayOrdersUsingCircuitBreaker(String category) {
        String url = (category == null || category.isBlank()) ? BASEURL : BASEURL + "/" + category;
        System.out.println("circuitBreaker method called " + attemptCircuitBreaker++ + " times at " + new Date());

        OrderDTO[] orders = restTemplate.getForObject(url, OrderDTO[].class);
        return orders == null ? Collections.emptyList() : Arrays.asList(orders);
    }

    /**
     * Method annotated with Retry. Fallback signature similar to above.
     */
    @Retry(name = USER_SERVICE, fallbackMethod = "getAllAvailableProductsFallback")
    public List<OrderDTO> displayOrdersUsingRetry(String category) {
        String url = (category == null || category.isBlank()) ? BASEURL : BASEURL + "/" + category;
        System.out.println("retry method called " + attemptRetry++ + " times at " + new Date());

        OrderDTO[] orders = restTemplate.getForObject(url, OrderDTO[].class);
        return orders == null ? Collections.emptyList() : Arrays.asList(orders);
    }

    /**
     * Fallback method for both CircuitBreaker and Retry.
     * Signature: (original parameters..., Throwable)
     */
    public List<OrderDTO> getAllAvailableProductsFallback(String category, Throwable t) {
        System.err.println("Fallback called for category=" + category + ", cause=" + t);
        return getFallbackOrders();
    }

    /**
     * Local static list used by fallback and the simple GET endpoint.
     */
    private List<OrderDTO> getFallbackOrders() {
        return Stream.of(
                new OrderDTO(119, "LED TV", "electronics", "white", 45000.0),
                new OrderDTO(345, "Headset", "electronics", "black", 7000.0),
                new OrderDTO(475, "Sound bar", "electronics", "black", 13000.0),
                new OrderDTO(574, "Puma Shoes", "foot wear", "black & white", 4600.0),
                new OrderDTO(678, "Vegetable chopper", "kitchen", "blue", 999.0),
                new OrderDTO(532, "Oven Gloves", "kitchen", "gray", 745.0)
        ).collect(Collectors.toList());
    }
}