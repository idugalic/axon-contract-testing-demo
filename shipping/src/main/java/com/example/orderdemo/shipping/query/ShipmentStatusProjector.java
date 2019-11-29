package com.example.orderdemo.shipping.query;

import com.example.orderdemo.shipping.command.api.ShipmentArrivedEvt;
import com.example.orderdemo.shipping.command.api.ShipmentPreparedEvt;
import com.example.orderdemo.shipping.query.api.FindAllOpenShipmentsQry;
import com.example.orderdemo.shipping.query.api.OpenShipmentModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
class ShipmentStatusProjector {

    private final EntityManager entityManager;

    @EventHandler
    public void on(ShipmentPreparedEvt evt, @Timestamp Instant timestamp) {
        entityManager.persist(OpenShipmentEntity.builder()
                                                .shipmentId(evt.getShipmentId())
                                                .destination(evt.getDestination())
                                                .registeredOn(timestamp)
                                                .build());
    }

    @EventHandler
    public void on(ShipmentArrivedEvt evt) {
        OpenShipmentEntity openShipment = entityManager.find(OpenShipmentEntity.class, evt.getShipmentId());
        entityManager.remove(openShipment);
    }

    @QueryHandler
    public List<OpenShipmentModel> handle(FindAllOpenShipmentsQry query) {
        return entityManager.createQuery(
                "SELECT e FROM OpenShipmentEntity e ORDER BY registeredOn ASC",
                OpenShipmentEntity.class
        ).getResultStream().map(r -> OpenShipmentModel.builder()
                                                      .destination(r.getDestination())
                                                      .registeredOn(r.getRegisteredOn()).shipmentId(r.getShipmentId())
                                                      .build()).collect(
                Collectors.toList());
    }
}
