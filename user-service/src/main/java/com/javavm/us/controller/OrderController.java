package com.javavm.us.controller;
import com.javavm.us.dto.OrderDTO;
import com.javavm.us.service.OrderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
@RestController
@RequestMapping("/user-service")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Simple endpoint that returns the static list
    @GetMapping
    public List<OrderDTO> getOrders() {
        return orderService.getAllAvailableProducts();
    }

    // CircuitBreaker-protected endpoint (service handles fallback)
    @GetMapping("circuitBreaker/displayOrders")
    public List<OrderDTO> displayOrdersusingCircuitBreaker(@RequestParam(value = "category", required = false) String category) {
        return orderService.displayOrdersUsingCircuitBreaker(category);
    }

    // Retry-protected endpoint
    @GetMapping("retry/displayOrders")
    public List<OrderDTO> displayOrdersUsingRetry(@RequestParam(value = "category", required = false) String category) {
        return orderService.displayOrdersUsingRetry(category);
    }

    private int attemptRateLimiter = 1;
    private int attemptRateLimiter1 = 1;

    @GetMapping("/rate")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<String> getRateLimiter() {
        System.out.println("RateLimiter method called " + attemptRateLimiter++ + " times at " + new Date());
        return ResponseEntity.ok("getRateLimiter");
    }

    @GetMapping("/rate1")
    @RateLimiter(name = "userRateLimiter1", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<String> getRateLimiter1() {
        System.out.println("RateLimiter1 method called " + attemptRateLimiter1++ + " times at " + new Date());
        return ResponseEntity.ok("getRateLimiter1 ");
    }

    public ResponseEntity<String> ratingHotelFallback(Exception ex) {
        System.err.println("RateLimiter Fallback called for cause=" + ex);
        ex.printStackTrace();
        return new ResponseEntity<>("failed", HttpStatus.BAD_REQUEST);
    }

}