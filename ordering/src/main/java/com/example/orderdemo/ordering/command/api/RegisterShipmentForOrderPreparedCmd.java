package com.example.orderdemo.ordering.command.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegisterShipmentForOrderPreparedCmd {

    @TargetAggregateIdentifier
    String orderId;

    String shipmentId;
}
