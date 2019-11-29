package com.example.orderdemo.shipping.web;

import com.example.orderdemo.shipping.command.api.RegisterShipmentArrivalCmd;
import com.example.orderdemo.shipping.query.api.FindAllOpenShipmentsQry;
import com.example.orderdemo.shipping.query.api.OpenShipmentModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @GetMapping
    public List<OpenShipmentModel> getOpenShipments() {
        return queryGateway.query(new FindAllOpenShipmentsQry(),
                                  ResponseTypes.multipleInstancesOf(OpenShipmentModel.class))
                           .join();
    }

    @PostMapping
    public void registerArrival(String shipmentId) {
        commandGateway.sendAndWait(new RegisterShipmentArrivalCmd(shipmentId));
    }
}
