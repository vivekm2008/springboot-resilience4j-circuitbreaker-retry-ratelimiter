package com.javavm.os.init;

import com.javavm.os.entity.Order;
import com.javavm.os.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataLoader {

    @Autowired
    private OrderRepository orderRepository;

    @PostConstruct
    public void initOrdersTable() {
        // avoid duplicating on restart if you prefer: check count()
        if (orderRepository.count() == 0) {
            orderRepository.saveAll(Stream.of(
                    new Order("mobile", "electronics", "white", 20000),
                    new Order("T-Shirt", "clothes", "black", 999),
                    new Order("Jeans", "clothes", "blue", 1999),
                    new Order("Laptop", "electronics", "gray", 50000),
                    new Order("digital watch", "electronics", "black", 2500),
                    new Order("Fan", "electronics", "black", 50000)
            ).collect(Collectors.toList()));
        }
    }
}