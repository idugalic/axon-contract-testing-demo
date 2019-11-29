package com.example.orderdemo.shipping.command;

import com.example.orderdemo.shipping.command.api.PrepareShipmentCmd;
import com.example.orderdemo.shipping.command.api.RegisterShipmentArrivalCmd;
import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
class Shipment {

    @AggregateIdentifier
    private String shipmentId;

    @CommandHandler
    Shipment(PrepareShipmentCmd cmd) {
        apply(new ShipmentPreparedEvt(cmd.getShipmentId(), cmd.getDestination()));
    }

    @CommandHandler
    void handle(RegisterShipmentArrivalCmd cmd) {
        apply(new ShipmentArrivedEvt(shipmentId));
    }

    @EventSourcingHandler
    void on(ShipmentPreparedEvt evt) {
        shipmentId = evt.getShipmentId();
    }

    @EventSourcingHandler
    void on(ShipmentArrivedEvt evt) {
        markDeleted();
    }
}
