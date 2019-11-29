package com.example.orderdemo.ordering.command;

import com.example.orderdemo.ordering.command.api.OrderPlacedEvt;
import com.example.orderdemo.ordering.command.api.PlaceOrderCmd;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderArrivedCmd;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderPreparedCmd;
import com.example.orderdemo.ordering.command.api.ShipmentForOrderArrivedEvt;
import com.example.orderdemo.ordering.command.api.ShipmentForOrderPreparedEvt;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashSet;
import java.util.Set;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
class Order {

    @AggregateIdentifier
    private String orderId;

    private Set<String> undeliveredShipments;

    @CommandHandler
    Order(PlaceOrderCmd cmd) {
        apply(new OrderPlacedEvt(cmd.getOrderId(), cmd.getGoods(), cmd.getDestination()));
    }

    @CommandHandler
    void handle(RegisterShipmentForOrderPreparedCmd cmd) {
        if (!undeliveredShipments.contains(cmd.getShipmentId())) {
            apply(new ShipmentForOrderPreparedEvt(orderId, cmd.getShipmentId()));
        }
    }

    @CommandHandler
    void handle(RegisterShipmentForOrderArrivedCmd cmd) {
        if (undeliveredShipments.contains(cmd.getShipmentId())) {
            apply(new ShipmentForOrderArrivedEvt(orderId, cmd.getShipmentId()));
        }
    }

    @EventSourcingHandler
    void on(OrderPlacedEvt evt) {
        orderId = evt.getOrderId();
        undeliveredShipments = new HashSet<>();
    }

    @EventSourcingHandler
    void on(ShipmentForOrderPreparedEvt evt) {
        undeliveredShipments.add(evt.getShipmentId());
    }

    @EventSourcingHandler
    void on(ShipmentForOrderArrivedEvt evt) {
        undeliveredShipments.remove(evt.getShipmentId());
    }
}
