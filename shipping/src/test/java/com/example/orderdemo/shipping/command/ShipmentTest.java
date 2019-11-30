package com.example.orderdemo.shipping.command;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import com.example.orderdemo.shipping.command.api.PrepareShipmentCmd;
import com.example.orderdemo.shipping.command.api.RegisterShipmentArrivalCmd;
import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.util.Collections;
import java.util.UUID;

// https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-junit5

@Provider("shipping")
@PactFolder("../pacts")
public class ShipmentTest {

    private FixtureConfiguration fixture = new AggregateTestFixture<>(Shipment.class);
    private String shipmentId = UUID.randomUUID().toString();
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void testTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    public void before(PactVerificationContext context) {
        context.setTarget(new AmpqTestTarget(Collections.emptyList()));
    }

    @PactVerifyProvider("PrepareShipmentCmd")
    public String prepareShipmentTest() throws JsonProcessingException {
        PrepareShipmentCmd prepareShipmentCmd = new PrepareShipmentCmd(shipmentId, "destination");
        ShipmentPreparedEvt shipmentPreparedEvt = new ShipmentPreparedEvt(shipmentId, "destination");

        fixture
                .given()
                .when(prepareShipmentCmd)
                .expectEvents(shipmentPreparedEvt);

        return objectMapper.writeValueAsString(prepareShipmentCmd);
    }

    @PactVerifyProvider("ShipmentPreparedEvt")
    public String prepareShipment2Test() throws JsonProcessingException {
        PrepareShipmentCmd prepareShipmentCmd = new PrepareShipmentCmd(shipmentId, "destination-2");
        ShipmentPreparedEvt shipmentPreparedEvt = new ShipmentPreparedEvt(shipmentId, "destination-2");

        fixture
                .given()
                .when(prepareShipmentCmd)
                .expectEvents(shipmentPreparedEvt);

        return objectMapper.writeValueAsString(shipmentPreparedEvt);
    }

    @PactVerifyProvider("ShipmentArrivedEvt")
    public String registerShipmentArrivalTest() throws JsonProcessingException {
        RegisterShipmentArrivalCmd registerShipmentArrivalCmd = new RegisterShipmentArrivalCmd(shipmentId);
        ShipmentPreparedEvt shipmentPreparedEvt = new ShipmentPreparedEvt(shipmentId, "destination");
        ShipmentArrivedEvt shipmentArrivedEvt = new ShipmentArrivedEvt(shipmentId);

        fixture
                .given(shipmentPreparedEvt)
                .when(registerShipmentArrivalCmd)
                .expectEvents(shipmentArrivedEvt);

        return objectMapper.writeValueAsString(shipmentArrivedEvt);
    }
}
