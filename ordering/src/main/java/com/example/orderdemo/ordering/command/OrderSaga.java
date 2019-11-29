package com.example.orderdemo.ordering.command;

import com.example.orderdemo.ordering.command.api.OrderPlacedEvt;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderArrivedCmd;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderPreparedCmd;
import com.example.orderdemo.shipping.command.api.PrepareShipmentCmd;
import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    public final static String SHIPMENT_PREFIX = "shipment_";

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvt evt) {
        orderId = evt.getOrderId();
        String shipmentId = SHIPMENT_PREFIX + orderId;
        associateWith("shipmentId", shipmentId.toString());
        commandGateway.send(new PrepareShipmentCmd(shipmentId, evt.getDestination()));
    }

    @SagaEventHandler(associationProperty = "shipmentId")
    public void on(ShipmentPreparedEvt evt) {
        commandGateway.send(new RegisterShipmentForOrderPreparedCmd(orderId, evt.getShipmentId()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "shipmentId")
    public void on(ShipmentArrivedEvt evt) {
        commandGateway.send(new RegisterShipmentForOrderArrivedCmd(orderId, evt.getShipmentId()));
    }
}
