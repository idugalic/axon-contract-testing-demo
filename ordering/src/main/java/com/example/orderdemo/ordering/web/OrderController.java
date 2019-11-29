package com.example.orderdemo.ordering.web;

import com.example.orderdemo.ordering.command.api.PlaceOrderCmd;
import com.example.orderdemo.ordering.query.api.FindOrderStatusQry;
import com.example.orderdemo.ordering.query.api.OrderStatusModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public String placeOrder(String goods, String destination) {
        return commandGateway.sendAndWait(new PlaceOrderCmd(UUID.randomUUID().toString(), goods, destination));
    }

    @GetMapping("/{orderId}")
    public OrderStatusModel getOrderStatus(@PathVariable String orderId) {
        return queryGateway.query(new FindOrderStatusQry(orderId), ResponseTypes.instanceOf(
                OrderStatusModel.class))
                           .join();
    }
}
