package com.orderservice.controller;

import com.orderservice.service.OrderService;
import common.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @Autowired
    OrderService orderService;

    @GetMapping("/order/{id}")
    Order getOrder(@PathVariable("id") Long id) {
        Order order = new Order();
        return order;
    }

    @PostMapping("/order/{id}")
    Order postOrder(@PathVariable("id") Long id, @RequestBody  Order order) {
        orderService.postOrder(id, order);
        return order;
    }

}
