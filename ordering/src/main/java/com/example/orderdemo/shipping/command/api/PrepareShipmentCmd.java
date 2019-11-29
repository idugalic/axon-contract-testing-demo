package com.example.orderdemo.shipping.command.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PrepareShipmentCmd {

    @TargetAggregateIdentifier
    String shipmentId;

    String destination;
}
