package com.javavm.os.service;

import com.javavm.os.entity.Order;
import com.javavm.os.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCategory(String category) {
        return orderRepository.findByCategory(category);
    }

    public void saveAll(List<Order> orders) {
        orderRepository.saveAll(orders);
    }
}
