package com.example.orderdemo.ordering.query;

import com.example.orderdemo.ordering.command.api.OrderPlacedEvt;
import com.example.orderdemo.ordering.command.api.ShipmentForOrderArrivedEvt;
import com.example.orderdemo.ordering.command.api.ShipmentForOrderPreparedEvt;
import com.example.orderdemo.ordering.query.api.FindOrderStatusQry;
import com.example.orderdemo.ordering.query.api.OrderStatusModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
class OrderStatusProjector {

    private final EntityManager entityManager;

    @EventHandler
    public void on(OrderPlacedEvt evt) {
        entityManager.persist(OrderStatusEntity.builder()
                                               .orderId(evt.getOrderId())
                                               .shipmentId(null)
                                               .delivered(false)
                                               .build());
    }

    @EventHandler
    public void on(ShipmentForOrderPreparedEvt evt) {
        OrderStatusEntity status = entityManager.find(OrderStatusEntity.class, evt.getOrderId());
        status.setShipmentId(evt.getShipmentId());
    }

    @EventHandler
    public void on(ShipmentForOrderArrivedEvt evt) {
        OrderStatusEntity status = entityManager.find(OrderStatusEntity.class, evt.getOrderId());
        status.setDelivered(true);
    }

    @QueryHandler
    public OrderStatusModel handle(FindOrderStatusQry query) {
        OrderStatusEntity entity = entityManager.find(OrderStatusEntity.class, query.getOrderId());
        return OrderStatusModel.builder().delivered(entity.isDelivered()).orderId(entity.getOrderId())
                               .shipmentId(entity.getShipmentId()).build();
    }
}
