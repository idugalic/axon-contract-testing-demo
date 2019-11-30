package com.example.orderdemo.ordering.command;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactFolder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.model.v3.messaging.Message;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import com.example.orderdemo.ordering.command.api.OrderPlacedEvt;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderArrivedCmd;
import com.example.orderdemo.ordering.command.api.RegisterShipmentForOrderPreparedCmd;
import com.example.orderdemo.shipping.command.api.PrepareShipmentCmd;
import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "shipping", providerType = ProviderType.ASYNCH)
@PactFolder("pacts")
class OrderSagaTest {

    private FixtureConfiguration fixture = new SagaTestFixture<>(OrderSaga.class);
    private String orderId = UUID.randomUUID().toString();
    private String shipmentId = OrderSaga.SHIPMENT_PREFIX + orderId;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    @Pact(consumer = "ordering", provider = "shipping")
    MessagePact prepareShipmentCmdPact(MessagePactBuilder builder) {
        PactDslJsonBody billingJsonBody = new PactDslJsonBody();
        billingJsonBody.stringType("shipmentId", shipmentId);
        billingJsonBody.stringType("destination", "destination");

        return builder
                .hasPactWith("shipping")
                .expectsToReceive("PrepareShipmentCmd")
                .withContent(billingJsonBody)
                .toPact();
    }

    @Pact(consumer = "ordering", provider = "shipping")
    MessagePact shipmentPreparedEvtPact(MessagePactBuilder builder) {
        PactDslJsonBody billingJsonBody = new PactDslJsonBody();
        billingJsonBody.stringType("shipmentId", shipmentId);
        billingJsonBody.stringType("destination", "destination");

        return builder
                .hasPactWith("shipping")
                .expectsToReceive("ShipmentPreparedEvt")
                .withContent(billingJsonBody)
                .toPact();
    }

    @Pact(consumer = "ordering", provider = "shipping")
    MessagePact shipmentArrivedEvtPact(MessagePactBuilder builder) {
        PactDslJsonBody billingJsonBody = new PactDslJsonBody();
        billingJsonBody.stringType("shipmentId", shipmentId);

        return builder
                .hasPactWith("shipping")
                .expectsToReceive("ShipmentArrivedEvt")
                .withContent(billingJsonBody)
                .toPact();
    }


    @Test
    @PactTestFor(pactMethod = "prepareShipmentCmdPact")
    void orderPlacedEvtTest(List<Message> messages) throws IOException {
        for (Message message: messages) {
            PrepareShipmentCmd prepareShipmentCmd = objectMapper.readValue(message.getContents().getValue(), PrepareShipmentCmd.class);

            fixture.givenNoPriorActivity()
                   .whenAggregate(orderId.toString())
                   .publishes(new OrderPlacedEvt(orderId, "goods", "destination"))
                   .expectActiveSagas(1)
                   .expectAssociationWith("shipmentId", shipmentId)
                   .expectDispatchedCommands(prepareShipmentCmd);
        }
    }

    @Test
    @PactTestFor(pactMethod = "shipmentPreparedEvtPact")
    void shipmentPreparedEvtPublishedTest(List<Message> messages) throws IOException {

        for (Message message: messages){
            ShipmentPreparedEvt shipmentPreparedEvt = objectMapper.readValue(message.getContents().getValue(), ShipmentPreparedEvt.class);
            fixture.givenAggregate(orderId.toString())
                   .published(new OrderPlacedEvt(orderId, "goods", "destination"))
                   .whenPublishingA(shipmentPreparedEvt)
                   .expectActiveSagas(1)
                   .expectDispatchedCommands(new RegisterShipmentForOrderPreparedCmd(orderId, shipmentId));
        }


    }

    @Test
    @PactTestFor(pactMethod = "shipmentArrivedEvtPact")
    void shipmentArrivedEvtPublishedTest(List<Message> messages) throws IOException {
        for (Message message: messages) {
            ShipmentArrivedEvt shipmentArrivedEvt = objectMapper.readValue(message.getContents().getValue(), ShipmentArrivedEvt.class);
            fixture.givenAggregate(orderId.toString())
                   .published(new OrderPlacedEvt(orderId, "goods", "destination"),
                              new ShipmentPreparedEvt(shipmentId, "destination"))
                   .andThenAggregate(shipmentId)
                   .published(new ShipmentPreparedEvt(shipmentId, "destination"))
                   .whenPublishingA(shipmentArrivedEvt)
                   .expectActiveSagas(0)
                   .expectDispatchedCommands(new RegisterShipmentForOrderArrivedCmd(orderId, shipmentId));
        }

    }
}
