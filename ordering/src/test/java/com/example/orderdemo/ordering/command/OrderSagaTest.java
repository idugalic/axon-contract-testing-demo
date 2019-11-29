package com.example.orderdemo.ordering.command;

import com.example.orderdemo.ordering.command.api.OrderPlacedEvt;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderArrivedCmd;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderPreparedCmd;
import com.example.orderdemo.shipping.command.api.PrepareShipmentCmd;
import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.*;

import java.util.UUID;

class OrderSagaTest {

    private FixtureConfiguration fixture;
    private String orderId;
    private String shipmentId;

    @BeforeEach
    void setUp() {
        fixture = new SagaTestFixture<>(OrderSaga.class);
        orderId = UUID.randomUUID().toString();
        shipmentId = OrderSaga.SHIPMENT_PREFIX + orderId;
    }

    @Test
    void orderPlacedEvtTest() {
        fixture.givenNoPriorActivity()
               .whenAggregate(orderId.toString())
               .publishes(new OrderPlacedEvt(orderId, "goods", "destination"))
               .expectActiveSagas(1)
               .expectAssociationWith("shipmentId", shipmentId)
               .expectDispatchedCommands(new PrepareShipmentCmd(shipmentId, "destination"));
    }

    @Test
    void shipmentPreparedEvtTest() {
        fixture.givenAggregate(orderId.toString())
               .published(new OrderPlacedEvt(orderId, "goods", "destination"))
               .whenPublishingA(new ShipmentPreparedEvt(shipmentId, "destination"))
               .expectActiveSagas(1)
               .expectDispatchedCommands(new RegisterShipmentForOrderPreparedCmd(orderId, shipmentId));
    }

    @Test
    void shipmentArrivedEvtTest() {
        fixture.givenAggregate(orderId.toString())
               .published(new OrderPlacedEvt(orderId, "goods", "destination"),
                          new ShipmentPreparedEvt(shipmentId, "destination"))
               .andThenAggregate(shipmentId)
               .published(new ShipmentPreparedEvt(shipmentId, "destination"))
               .whenPublishingA(new ShipmentArrivedEvt(shipmentId))
               .expectActiveSagas(0)
               .expectDispatchedCommands(new RegisterShipmentForOrderArrivedCmd(orderId, shipmentId));
    }
}
