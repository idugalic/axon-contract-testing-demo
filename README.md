# Order Demo - Bounded Contexts with Axon

> This demo was inspired by the [blog post](https://axoniq.io/blog-overview/bounded-contexts-with-axon) which is published on [axoniq.io](https://axoniq.io/)

<table>
    <tr>
        <td><strong>Domain:</strong></td>
        <td>Order Management - Demo</td>
    </tr>
    <tr>
        <td><strong>Concepts:</strong></td>
        <td>Domain Driven Design (Subdomains, Bounded Contexts, Ubiquitous Language, Aggregates, Value Objects)</td>
    </tr>
    <tr>
        <td><strong>Architecture style:</strong></td>
        <td>Event Driven Microservices</td>
    </tr>
    <tr>
        <td><strong>Architectural patterns:</strong></td>
        <td>Eventsourcing, CQRS</td>
    </tr>
    <tr>
        <td><strong>Technology:</strong></td>
        <td>Java, <strong>Axon (AxonFramework,  AxonServer CE)</strong>, Spring (Boot), SQL</td>
    </tr>
</table>

## Domain

Customers use the website application(s) to place orders. Application coordinates order preparation and shipping.

## Sub-domains

- **Order** management
  - Order taking and fulfillment management
- **Shipping** management
  - Managing shipping/delivery of the order.

The `Order` (`Shipping`) objects ([aggregates](https://martinfowler.com/bliki/DDD_Aggregate.html)) in each sub-domain model represent different term of the same 'Order' business concept.

- The Shipping sub-domain has a different view of an Order (Shipment). Its version of an Order could simply consist of a status and a address, which can tell courier how and where to deliver the order.  

We must maintain consistency between these different 'orders' in different sub-domains.

> Typically, we can split complex domain into sub-domains, where each sub-domain corresponds to different part of the business.
> Identifying sub-domains requires understanding of the business, mostly its organizational structure and areas of expertise.

## Domain model

Sub-domain *software programing* models:

 - [ordering](https://github.com/idugalic/orderdemo/tree/master/ordering)
 - [shipping](https://github.com/idugalic/orderdemo/tree/master/shipping)
 
> Domain model is mainly a software programing model which is applied to a specific sub-domain.
> It defines the vocabulary and acts as a communication tool for everyone involved (business and IT), deriving a [Ubiquitous Language](https://martinfowler.com/bliki/UbiquitousLanguage.html).
 
## Bounded Context

Each of this group of applications/services belongs to a specific bounded context:
- [ordering](https://github.com/idugalic/orderdemo/tree/master/ordering) - Order bounded context, with messages serialized to JSON
- [shipping](https://github.com/idugalic/orderdemo/tree/master/shipping) - Shipping bounded context, with messages serialized to JSON

> A goal is to develop a [Ubiquitous Language](https://martinfowler.com/bliki/UbiquitousLanguage.html) as our domain (sub-domain) model within an explicitly Bounded Context.
> Therefore, there are a number of rules for Models and Contexts
> - Explicitly define the context within which a model applies
> - Ideally, keep one sub-domain model per one Bounded Context
> - Explicitly set boundaries in terms of team organization, usage within specific parts of the application, and physical manifestations such as code bases and database schemas
>
> From a run-time perspective, Bounded Contexts represent logical boundaries, defined by contracts within software artifacts where the model is implemented.
>
> In Axon applications/services, the contract (API) is represented as a set of messages (commands, events and queries) which the application publishes and consumes.
> We create runnable applications(services) with contracts (API) published as schema.
> 
> This generally means that if the events/commands/queries are published as JSON, or perhaps a more economical object format, the consumer should consume the messages by parsing them to obtain their data attributes.

### Bounded Context Mappings

These bounded contexts are in the **upstream-downstream** (more specifically: Customer-Supplier) relationship where the `Order` (downstream) depends on the API of the `Shipping` (upstream) only.

![bounded-context-mapping-plantuml](data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+PHN2%0D%0AZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8v%0D%0Ad3d3LnczLm9yZy8xOTk5L3hsaW5rIiBjb250ZW50U2NyaXB0VHlwZT0iYXBwbGljYXRpb24vZWNt%0D%0AYXNjcmlwdCIgY29udGVudFN0eWxlVHlwZT0idGV4dC9jc3MiIGhlaWdodD0iMTY4cHgiIHByZXNl%0D%0AcnZlQXNwZWN0UmF0aW89Im5vbmUiIHN0eWxlPSJ3aWR0aDozMDdweDtoZWlnaHQ6MTY4cHg7IiB2%0D%0AZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMDcgMTY4IiB3aWR0aD0iMzA3cHgiIHpvb21BbmRQ%0D%0AYW49Im1hZ25pZnkiPjxkZWZzPjxmaWx0ZXIgaGVpZ2h0PSIzMDAlIiBpZD0iZjF2bW43Y21jaXQ5%0D%0ANGQiIHdpZHRoPSIzMDAlIiB4PSItMSIgeT0iLTEiPjxmZUdhdXNzaWFuQmx1ciByZXN1bHQ9ImJs%0D%0AdXJPdXQiIHN0ZERldmlhdGlvbj0iMi4wIi8+PGZlQ29sb3JNYXRyaXggaW49ImJsdXJPdXQiIHJl%0D%0Ac3VsdD0iYmx1ck91dDIiIHR5cGU9Im1hdHJpeCIgdmFsdWVzPSIwIDAgMCAwIDAgMCAwIDAgMCAw%0D%0AIDAgMCAwIDAgMCAwIDAgMCAuNCAwIi8+PGZlT2Zmc2V0IGR4PSI0LjAiIGR5PSI0LjAiIGluPSJi%0D%0AbHVyT3V0MiIgcmVzdWx0PSJibHVyT3V0MyIvPjxmZUJsZW5kIGluPSJTb3VyY2VHcmFwaGljIiBp%0D%0AbjI9ImJsdXJPdXQzIiBtb2RlPSJub3JtYWwiLz48L2ZpbHRlcj48L2RlZnM+PGc+PCEtLU1ENT1b%0D%0AYzgyZjdhMmY4MDI3NDFkNzBiMmQ5YWQxYWRlNGMwZTNdCmVudGl0eSBPcmRlcmluZy0tPjxyZWN0%0D%0AIGZpbGw9IiNBREQ4RTYiIGZpbHRlcj0idXJsKCNmMXZtbjdjbWNpdDk0ZCkiIGhlaWdodD0iMzYu%0D%0AMjk2OSIgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjU7IiB3aWR0aD0i%0D%0AODAiIHg9IjIxNi41IiB5PSI4Ii8+PHJlY3QgZmlsbD0iI0FERDhFNiIgaGVpZ2h0PSI1IiBzdHls%0D%0AZT0ic3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuNTsiIHdpZHRoPSIxMCIgeD0iMjEx%0D%0ALjUiIHk9IjEzIi8+PHJlY3QgZmlsbD0iI0FERDhFNiIgaGVpZ2h0PSI1IiBzdHlsZT0ic3Ryb2tl%0D%0AOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuNTsiIHdpZHRoPSIxMCIgeD0iMjExLjUiIHk9IjM0%0D%0ALjI5NjkiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9u%0D%0AdC1zaXplPSIxNCIgbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSI2%0D%0AMCIgeD0iMjI2LjUiIHk9IjMwLjk5NTEiPk9yZGVyaW5nPC90ZXh0PjwhLS1NRDU9WzlmMzE0OTdi%0D%0AY2EyOWJlMjE4MzE4MWM0ZjUzZmU5YzIyXQplbnRpdHkgU2hpcHBpbmctLT48cmVjdCBmaWxsPSIj%0D%0AOTBFRTkwIiBmaWx0ZXI9InVybCgjZjF2bW43Y21jaXQ5NGQpIiBoZWlnaHQ9IjM2LjI5NjkiIHN0%0D%0AeWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS41OyIgd2lkdGg9IjgwIiB4PSIy%0D%0AMTYuNSIgeT0iMTIxIi8+PHJlY3QgZmlsbD0iIzkwRUU5MCIgaGVpZ2h0PSI1IiBzdHlsZT0ic3Ry%0D%0Ab2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuNTsiIHdpZHRoPSIxMCIgeD0iMjExLjUiIHk9%0D%0AIjEyNiIvPjxyZWN0IGZpbGw9IiM5MEVFOTAiIGhlaWdodD0iNSIgc3R5bGU9InN0cm9rZTogI0E4%0D%0AMDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjU7IiB3aWR0aD0iMTAiIHg9IjIxMS41IiB5PSIxNDcuMjk2%0D%0AOSIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlmIiBmb250LXNp%0D%0AemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRMZW5ndGg9IjYwIiB4%0D%0APSIyMjYuNSIgeT0iMTQzLjk5NTEiPlNoaXBwaW5nPC90ZXh0PjxwYXRoIGQ9Ik02LDEzLjUgTDYs%0D%0AMzguNjMyOCBBMCwwIDAgMCAwIDYsMzguNjMyOCBMMTgxLDM4LjYzMjggQTAsMCAwIDAgMCAxODEs%0D%0AMzguNjMyOCBMMTgxLDMxLjUgTDIxNi40MTcsMjYgTDE4MSwyMy41IEwxODEsMjMuNSBMMTcxLDEz%0D%0ALjUgTDYsMTMuNSBBMCwwIDAgMCAwIDYsMTMuNSAiIGZpbGw9IiNGQkZCNzciIGZpbHRlcj0idXJs%0D%0AKCNmMXZtbjdjbWNpdDk0ZCkiIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDog%0D%0AMS4wOyIvPjxwYXRoIGQ9Ik0xNzEsMTMuNSBMMTcxLDIzLjUgTDE4MSwyMy41IEwxNzEsMTMuNSAi%0D%0AIGZpbGw9IiNGQkZCNzciIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4w%0D%0AOyIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlmIiBmb250LXNp%0D%0AemU9IjEzIiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRMZW5ndGg9IjE1NCIg%0D%0AeD0iMTIiIHk9IjMwLjU2NjkiPmRvd25zdHJlYW0gKGN1c3RvbWVyKTwvdGV4dD48cGF0aCBkPSJN%0D%0AMzQsMTI2LjUgTDM0LDE1MS42MzI4IEEwLDAgMCAwIDAgMzQsMTUxLjYzMjggTDE4MSwxNTEuNjMy%0D%0AOCBBMCwwIDAgMCAwIDE4MSwxNTEuNjMyOCBMMTgxLDE0NC41IEwyMTYuNDU4LDEzOSBMMTgxLDEz%0D%0ANi41IEwxODEsMTM2LjUgTDE3MSwxMjYuNSBMMzQsMTI2LjUgQTAsMCAwIDAgMCAzNCwxMjYuNSAi%0D%0AIGZpbGw9IiNGQkZCNzciIGZpbHRlcj0idXJsKCNmMXZtbjdjbWNpdDk0ZCkiIHN0eWxlPSJzdHJv%0D%0Aa2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyIvPjxwYXRoIGQ9Ik0xNzEsMTI2LjUgTDE3%0D%0AMSwxMzYuNSBMMTgxLDEzNi41IEwxNzEsMTI2LjUgIiBmaWxsPSIjRkJGQjc3IiBzdHlsZT0ic3Ry%0D%0Ab2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuMDsiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBm%0D%0Ab250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFj%0D%0AaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSIxMjYiIHg9IjQwIiB5PSIxNDMuNTY2OSI+dXBzdHJl%0D%0AYW0gKHN1cHBsaWVyKTwvdGV4dD48IS0tTUQ1PVszMDdhYjc3Nzg4Y2NlMTEwNWRkNDkzNWQ4OWZi%0D%0AMGQ4YV0KbGluayBPcmRlcmluZyB0byBTaGlwcGluZy0tPjxwYXRoIGQ9Ik0yNTYuNSw0NC4zNDQg%0D%0AQzI1Ni41LDYzLjU2NzcgMjU2LjUsOTQuNjI5NiAyNTYuNSwxMTUuNjYwNiAiIGZpbGw9Im5vbmUi%0D%0AIGlkPSJPcmRlcmluZy0mZ3Q7U2hpcHBpbmciIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9r%0D%0AZS13aWR0aDogMS4wOyBzdHJva2UtZGFzaGFycmF5OiA3LjAsNy4wOyIvPjxwb2x5Z29uIGZpbGw9%0D%0AIiNBODAwMzYiIHBvaW50cz0iMjU2LjUsMTIwLjc3OCwyNjAuNSwxMTEuNzc4LDI1Ni41LDExNS43%0D%0ANzgsMjUyLjUsMTExLjc3OCwyNTYuNSwxMjAuNzc4IiBzdHlsZT0ic3Ryb2tlOiAjQTgwMDM2OyBz%0D%0AdHJva2Utd2lkdGg6IDEuMDsiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fu%0D%0Acy1zZXJpZiIgZm9udC1zaXplPSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0%0D%0AZXh0TGVuZ3RoPSIyMyIgeD0iMjU3LjUiIHk9Ijg3LjA2NjkiPnVzZTwvdGV4dD48IS0tTUQ1PVtk%0D%0AMWRjZWE5OTk2MTQ3OTJiMTU4MGE0NmQxOGQyMmY2OF0KQHN0YXJ0dW1sCltPcmRlcmluZ10gLi4+%0D%0AIFtTaGlwcGluZ10gOiB1c2UKIG5vdGUgbGVmdCBvZiBPcmRlcmluZyA6IGRvd25zdHJlYW0gKGN1%0D%0Ac3RvbWVyKQogbm90ZSBsZWZ0IG9mIFNoaXBwaW5nIDogdXBzdHJlYW0gKHN1cHBsaWVyKQogCiBj%0D%0Ab21wb25lbnQgIFtPcmRlcmluZ10gI0xpZ2h0Qmx1ZQogY29tcG9uZW50ICBbU2hpcHBpbmddICNM%0D%0AaWdodEdyZWVuCkBlbmR1bWwKClBsYW50VU1MIHZlcnNpb24gMS4yMDE5LjEyKFN1biBOb3YgMDMg%0D%0AMTA6MjQ6NTQgVVRDIDIwMTkpCihHUEwgc291cmNlIGRpc3RyaWJ1dGlvbikKSmF2YSBSdW50aW1l%0D%0AOiBKYXZhKFRNKSBTRSBSdW50aW1lIEVudmlyb25tZW50CkpWTTogSmF2YSBIb3RTcG90KFRNKSA2%0D%0ANC1CaXQgU2VydmVyIFZNCkphdmEgVmVyc2lvbjogMS43LjBfMjUtYjE1Ck9wZXJhdGluZyBTeXN0%0D%0AZW06IExpbnV4CkRlZmF1bHQgRW5jb2Rpbmc6IFVURi04Ckxhbmd1YWdlOiBlbgpDb3VudHJ5OiBV%0D%0AUwotLT48L2c+PC9zdmc+)

The Order service is responsible for the order fulfilment process and it will trigger a `command (PrepareShipmentCmd)` to the Shipping service(s) to create/prepare a Shipment.
Once the courier delivers the shipment, the Order service(s) will receive an `event (ShipmentPreparedEvt)` from the Shipping service and will continue with the order fulfilment process.

We coordinate these two services with [OrderSaga.java](/ordering/src/main/java/com/example/orderdemo/ordering/command/OrderSaga.java) to maintain consistency between these different orders (Order, Shipment) from different bounded contexts.

![order-saga-plantuml](data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+PHN2%0D%0AZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8v%0D%0Ad3d3LnczLm9yZy8xOTk5L3hsaW5rIiBjb250ZW50U2NyaXB0VHlwZT0iYXBwbGljYXRpb24vZWNt%0D%0AYXNjcmlwdCIgY29udGVudFN0eWxlVHlwZT0idGV4dC9jc3MiIGhlaWdodD0iMjg5cHgiIHByZXNl%0D%0AcnZlQXNwZWN0UmF0aW89Im5vbmUiIHN0eWxlPSJ3aWR0aDo0NzBweDtoZWlnaHQ6Mjg5cHg7IiB2%0D%0AZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA0NzAgMjg5IiB3aWR0aD0iNDcwcHgiIHpvb21BbmRQ%0D%0AYW49Im1hZ25pZnkiPjxkZWZzPjxmaWx0ZXIgaGVpZ2h0PSIzMDAlIiBpZD0iZnU2MHhya3B2cGZ0%0D%0AMyIgd2lkdGg9IjMwMCUiIHg9Ii0xIiB5PSItMSI+PGZlR2F1c3NpYW5CbHVyIHJlc3VsdD0iYmx1%0D%0Ack91dCIgc3RkRGV2aWF0aW9uPSIyLjAiLz48ZmVDb2xvck1hdHJpeCBpbj0iYmx1ck91dCIgcmVz%0D%0AdWx0PSJibHVyT3V0MiIgdHlwZT0ibWF0cml4IiB2YWx1ZXM9IjAgMCAwIDAgMCAwIDAgMCAwIDAg%0D%0AMCAwIDAgMCAwIDAgMCAwIC40IDAiLz48ZmVPZmZzZXQgZHg9IjQuMCIgZHk9IjQuMCIgaW49ImJs%0D%0AdXJPdXQyIiByZXN1bHQ9ImJsdXJPdXQzIi8+PGZlQmxlbmQgaW49IlNvdXJjZUdyYXBoaWMiIGlu%0D%0AMj0iYmx1ck91dDMiIG1vZGU9Im5vcm1hbCIvPjwvZmlsdGVyPjwvZGVmcz48Zz48cmVjdCBmaWxs%0D%0APSIjRkZGRkZGIiBmaWx0ZXI9InVybCgjZnU2MHhya3B2cGZ0MykiIGhlaWdodD0iMzUuMjk2OSIg%0D%0Ac3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB3aWR0aD0iMTAiIHg9%0D%0AIjQ4LjUiIHk9Ijc5LjQyOTciLz48cmVjdCBmaWxsPSIjRkZGRkZGIiBmaWx0ZXI9InVybCgjZnU2%0D%0AMHhya3B2cGZ0MykiIGhlaWdodD0iMjkuMTMyOCIgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ry%0D%0Ab2tlLXdpZHRoOiAxLjA7IiB3aWR0aD0iMTAiIHg9IjQ4LjUiIHk9IjE0My44NTk0Ii8+PHJlY3Qg%0D%0AZmlsbD0iI0ZGRkZGRiIgZmlsdGVyPSJ1cmwoI2Z1NjB4cmtwdnBmdDMpIiBoZWlnaHQ9IjI5LjEz%0D%0AMjgiIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyIgd2lkdGg9IjEw%0D%0AIiB4PSI0OC41IiB5PSIyMDIuMTI1Ii8+PHJlY3QgZmlsbD0iI0FERDhFNiIgZmlsdGVyPSJ1cmwo%0D%0AI2Z1NjB4cmtwdnBmdDMpIiBoZWlnaHQ9IjMwLjI5NjkiIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7%0D%0AIHN0cm9rZS13aWR0aDogMS41OyIgd2lkdGg9Ijg3IiB4PSI4IiB5PSI0OC4yOTY5Ii8+PHRleHQg%0D%0AZmlsbD0iIzAwMDAwMCIgZm9udC1mYW1pbHk9InNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTQiIGxl%0D%0Abmd0aEFkanVzdD0ic3BhY2luZ0FuZEdseXBocyIgdGV4dExlbmd0aD0iNzMiIHg9IjE1IiB5PSI2%0D%0AOC4yOTIiPk9yZGVyU2FnYTwvdGV4dD48bGluZSBzdHlsZT0ic3Ryb2tlOiAjQTgwMDM2OyBzdHJv%0D%0Aa2Utd2lkdGg6IDEuMDsgc3Ryb2tlLWRhc2hhcnJheTogNS4wLDUuMDsiIHgxPSI1MyIgeDI9IjUz%0D%0AIiB5MT0iODAuOTQ1MyIgeTI9IjI0OS4yNTc4Ii8+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAz%0D%0ANjsgc3Ryb2tlLXdpZHRoOiAxLjA7IHN0cm9rZS1kYXNoYXJyYXk6IDUuMCw1LjA7IiB4MT0iMzQw%0D%0AIiB4Mj0iMzQwIiB5MT0iMzguMjk2OSIgeTI9IjI0OS4yNTc4Ii8+PGxpbmUgc3R5bGU9InN0cm9r%0D%0AZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IHN0cm9rZS1kYXNoYXJyYXk6IDUuMCw1LjA7%0D%0AIiB4MT0iNDIxIiB4Mj0iNDIxIiB5MT0iMzguMjk2OSIgeTI9IjI0OS4yNTc4Ii8+PHJlY3QgZmls%0D%0AbD0iI0FERDhFNiIgZmlsdGVyPSJ1cmwoI2Z1NjB4cmtwdnBmdDMpIiBoZWlnaHQ9IjMwLjI5Njki%0D%0AIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS41OyIgd2lkdGg9Ijg3IiB4%0D%0APSI4IiB5PSIyNDguMjU3OCIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5z%0D%0ALXNlcmlmIiBmb250LXNpemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRl%0D%0AeHRMZW5ndGg9IjczIiB4PSIxNSIgeT0iMjY4LjI1MjkiPk9yZGVyU2FnYTwvdGV4dD48cmVjdCBm%0D%0AaWxsPSIjQUREOEU2IiBmaWx0ZXI9InVybCgjZnU2MHhya3B2cGZ0MykiIGhlaWdodD0iMzAuMjk2%0D%0AOSIgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjU7IiB3aWR0aD0iNTMi%0D%0AIHg9IjMxMiIgeT0iMyIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNl%0D%0AcmlmIiBmb250LXNpemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRM%0D%0AZW5ndGg9IjM5IiB4PSIzMTkiIHk9IjIyLjk5NTEiPk9yZGVyPC90ZXh0PjxyZWN0IGZpbGw9IiNB%0D%0AREQ4RTYiIGZpbHRlcj0idXJsKCNmdTYweHJrcHZwZnQzKSIgaGVpZ2h0PSIzMC4yOTY5IiBzdHls%0D%0AZT0ic3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuNTsiIHdpZHRoPSI1MyIgeD0iMzEy%0D%0AIiB5PSIyNDguMjU3OCIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNl%0D%0AcmlmIiBmb250LXNpemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRM%0D%0AZW5ndGg9IjM5IiB4PSIzMTkiIHk9IjI2OC4yNTI5Ij5PcmRlcjwvdGV4dD48cmVjdCBmaWxsPSIj%0D%0AOTBFRTkwIiBmaWx0ZXI9InVybCgjZnU2MHhya3B2cGZ0MykiIGhlaWdodD0iMzAuMjk2OSIgc3R5%0D%0AbGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjU7IiB3aWR0aD0iODAiIHg9IjM3%0D%0AOSIgeT0iMyIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlmIiBm%0D%0Ab250LXNpemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRMZW5ndGg9%0D%0AIjY2IiB4PSIzODYiIHk9IjIyLjk5NTEiPlNoaXBtZW50PC90ZXh0PjxyZWN0IGZpbGw9IiM5MEVF%0D%0AOTAiIGZpbHRlcj0idXJsKCNmdTYweHJrcHZwZnQzKSIgaGVpZ2h0PSIzMC4yOTY5IiBzdHlsZT0i%0D%0Ac3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuNTsiIHdpZHRoPSI4MCIgeD0iMzc5IiB5%0D%0APSIyNDguMjU3OCIvPjx0ZXh0IGZpbGw9IiMwMDAwMDAiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlm%0D%0AIiBmb250LXNpemU9IjE0IiBsZW5ndGhBZGp1c3Q9InNwYWNpbmdBbmRHbHlwaHMiIHRleHRMZW5n%0D%0AdGg9IjY2IiB4PSIzODYiIHk9IjI2OC4yNTI5Ij5TaGlwbWVudDwvdGV4dD48cmVjdCBmaWxsPSIj%0D%0ARkZGRkZGIiBmaWx0ZXI9InVybCgjZnU2MHhya3B2cGZ0MykiIGhlaWdodD0iMzUuMjk2OSIgc3R5%0D%0AbGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB3aWR0aD0iMTAiIHg9IjQ4%0D%0ALjUiIHk9Ijc5LjQyOTciLz48cmVjdCBmaWxsPSIjRkZGRkZGIiBmaWx0ZXI9InVybCgjZnU2MHhy%0D%0Aa3B2cGZ0MykiIGhlaWdodD0iMjkuMTMyOCIgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tl%0D%0ALXdpZHRoOiAxLjA7IiB3aWR0aD0iMTAiIHg9IjQ4LjUiIHk9IjE0My44NTk0Ii8+PHJlY3QgZmls%0D%0AbD0iI0ZGRkZGRiIgZmlsdGVyPSJ1cmwoI2Z1NjB4cmtwdnBmdDMpIiBoZWlnaHQ9IjI5LjEzMjgi%0D%0AIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyIgd2lkdGg9IjEwIiB4%0D%0APSI0OC41IiB5PSIyMDIuMTI1Ii8+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tl%0D%0ALXdpZHRoOiAxLjA7IiB4MT0iOTkiIHgyPSIxMDkiIHkxPSI2OS40Mjk3IiB5Mj0iNjUuNDI5NyIv%0D%0APjxsaW5lIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyIgeDE9Ijk5%0D%0AIiB4Mj0iMTA5IiB5MT0iNjkuNDI5NyIgeTI9IjczLjQyOTciLz48bGluZSBzdHlsZT0ic3Ryb2tl%0D%0AOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuMDsgc3Ryb2tlLWRhc2hhcnJheTogMi4wLDIuMDsi%0D%0AIHgxPSI5OSIgeDI9IjMzOS41IiB5MT0iNjkuNDI5NyIgeTI9IjY5LjQyOTciLz48dGV4dCBmaWxs%0D%0APSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMyIgbGVuZ3Ro%0D%0AQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSI5OCIgeD0iMTE2IiB5PSI2NC4z%0D%0ANjM4Ij5PcmRlclBsYWNlZEV2dDwvdGV4dD48cmVjdCBmaWxsPSIjQUREOEU2IiBmaWx0ZXI9InVy%0D%0AbCgjZnU2MHhya3B2cGZ0MykiIGhlaWdodD0iMzAuMjk2OSIgc3R5bGU9InN0cm9rZTogI0E4MDAz%0D%0ANjsgc3Ryb2tlLXdpZHRoOiAxLjU7IiB3aWR0aD0iODciIHg9IjgiIHk9IjQ4LjI5NjkiLz48dGV4%0D%0AdCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIg%0D%0AbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSI3MyIgeD0iMTUiIHk9%0D%0AIjY4LjI5MiI+T3JkZXJTYWdhPC90ZXh0PjxsaW5lIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0%0D%0Acm9rZS13aWR0aDogMS4wOyIgeDE9IjQxOSIgeDI9IjQwOSIgeTE9IjExNC43MjY2IiB5Mj0iMTEw%0D%0ALjcyNjYiLz48bGluZSBzdHlsZT0ic3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuMDsi%0D%0AIHgxPSI0MTkiIHgyPSI0MDkiIHkxPSIxMTQuNzI2NiIgeTI9IjExOC43MjY2Ii8+PGxpbmUgc3R5%0D%0AbGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IHN0cm9rZS1kYXNoYXJyYXk6%0D%0AIDIuMCwyLjA7IiB4MT0iNTMuNSIgeDI9IjQyMCIgeTE9IjExNC43MjY2IiB5Mj0iMTE0LjcyNjYi%0D%0ALz48dGV4dCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9udC1zaXpl%0D%0APSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSIxNDIiIHg9%0D%0AIjYwLjUiIHk9IjEwOS42NjA2Ij5QcmVwYXJlU2hpcG1lbnRDbWQ8L3RleHQ+PGxpbmUgc3R5bGU9%0D%0AInN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB4MT0iNTguNSIgeDI9IjY4LjUi%0D%0AIHkxPSIxNDMuODU5NCIgeTI9IjEzOS44NTk0Ii8+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAz%0D%0ANjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB4MT0iNTguNSIgeDI9IjY4LjUiIHkxPSIxNDMuODU5NCIg%0D%0AeTI9IjE0Ny44NTk0Ii8+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRo%0D%0AOiAxLjA7IHN0cm9rZS1kYXNoYXJyYXk6IDIuMCwyLjA7IiB4MT0iNTguNSIgeDI9IjQyMCIgeTE9%0D%0AIjE0My44NTk0IiB5Mj0iMTQzLjg1OTQiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWls%0D%0AeT0ic2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5%0D%0AcGhzIiB0ZXh0TGVuZ3RoPSIxNDAiIHg9Ijc1LjUiIHk9IjEzOC43OTM1Ij5TaGlwbWVudFByZXBh%0D%0AcmVkRXZ0PC90ZXh0PjxsaW5lIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDog%0D%0AMS4wOyIgeDE9IjMzOC41IiB4Mj0iMzI4LjUiIHkxPSIxNzIuOTkyMiIgeTI9IjE2OC45OTIyIi8+%0D%0APGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB4MT0iMzM4%0D%0ALjUiIHgyPSIzMjguNSIgeTE9IjE3Mi45OTIyIiB5Mj0iMTc2Ljk5MjIiLz48bGluZSBzdHlsZT0i%0D%0Ac3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuMDsgc3Ryb2tlLWRhc2hhcnJheTogMi4w%0D%0ALDIuMDsiIHgxPSI1My41IiB4Mj0iMzM5LjUiIHkxPSIxNzIuOTkyMiIgeTI9IjE3Mi45OTIyIi8+%0D%0APHRleHQgZmlsbD0iIzAwMDAwMCIgZm9udC1mYW1pbHk9InNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0i%0D%0AMTMiIGxlbmd0aEFkanVzdD0ic3BhY2luZ0FuZEdseXBocyIgdGV4dExlbmd0aD0iMjU4IiB4PSI2%0D%0AMC41IiB5PSIxNjcuOTI2MyI+UmVnaXN0ZXJTaGlwbWVudEZvck9yZGVyUHJlcGFyZWRDbWQ8L3Rl%0D%0AeHQ+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tlLXdpZHRoOiAxLjA7IiB4MT0i%0D%0ANTguNSIgeDI9IjY4LjUiIHkxPSIyMDIuMTI1IiB5Mj0iMTk4LjEyNSIvPjxsaW5lIHN0eWxlPSJz%0D%0AdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyIgeDE9IjU4LjUiIHgyPSI2OC41IiB5%0D%0AMT0iMjAyLjEyNSIgeTI9IjIwNi4xMjUiLz48bGluZSBzdHlsZT0ic3Ryb2tlOiAjQTgwMDM2OyBz%0D%0AdHJva2Utd2lkdGg6IDEuMDsgc3Ryb2tlLWRhc2hhcnJheTogMi4wLDIuMDsiIHgxPSI1OC41IiB4%0D%0AMj0iNDIwIiB5MT0iMjAyLjEyNSIgeTI9IjIwMi4xMjUiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBm%0D%0Ab250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFj%0D%0AaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSIxMjciIHg9Ijc1LjUiIHk9IjE5Ny4wNTkxIj5TaGlw%0D%0AbWVudEFycml2ZWRFdnQ8L3RleHQ+PGxpbmUgc3R5bGU9InN0cm9rZTogI0E4MDAzNjsgc3Ryb2tl%0D%0ALXdpZHRoOiAxLjA7IiB4MT0iMzM4LjUiIHgyPSIzMjguNSIgeTE9IjIzMS4yNTc4IiB5Mj0iMjI3%0D%0ALjI1NzgiLz48bGluZSBzdHlsZT0ic3Ryb2tlOiAjQTgwMDM2OyBzdHJva2Utd2lkdGg6IDEuMDsi%0D%0AIHgxPSIzMzguNSIgeDI9IjMyOC41IiB5MT0iMjMxLjI1NzgiIHkyPSIyMzUuMjU3OCIvPjxsaW5l%0D%0AIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMS4wOyBzdHJva2UtZGFzaGFy%0D%0AcmF5OiAyLjAsMi4wOyIgeDE9IjUzLjUiIHgyPSIzMzkuNSIgeTE9IjIzMS4yNTc4IiB5Mj0iMjMx%0D%0ALjI1NzgiLz48dGV4dCBmaWxsPSIjMDAwMDAwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgZm9u%0D%0AdC1zaXplPSIxMyIgbGVuZ3RoQWRqdXN0PSJzcGFjaW5nQW5kR2x5cGhzIiB0ZXh0TGVuZ3RoPSIy%0D%0ANDUiIHg9IjYwLjUiIHk9IjIyNi4xOTE5Ij5SZWdpc3RlclNoaXBtZW50Rm9yT3JkZXJBcnJpdmVk%0D%0AQ21kPC90ZXh0PjxsaW5lIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMi4w%0D%0AOyIgeDE9IjQ0LjUiIHgyPSI2Mi41IiB5MT0iMjIyLjI1NzgiIHkyPSIyNDAuMjU3OCIvPjxsaW5l%0D%0AIHN0eWxlPSJzdHJva2U6ICNBODAwMzY7IHN0cm9rZS13aWR0aDogMi4wOyIgeDE9IjQ0LjUiIHgy%0D%0APSI2Mi41IiB5MT0iMjQwLjI1NzgiIHkyPSIyMjIuMjU3OCIvPjwhLS1NRDU9WzU4MDdkN2M4YWIw%0D%0ANGRjZjI0MDI3OGMwODNmYWQ3NjViXQpAc3RhcnR1bWwKcGFydGljaXBhbnQgT3JkZXJTYWdhICNM%0D%0AaWdodEJsdWUKICAgIHBhcnRpY2lwYW50IE9yZGVyICNMaWdodEJsdWUKICAgIHBhcnRpY2lwYW50%0D%0AIFNoaXBtZW50ICNMaWdodEdyZWVuCgogICAgY3JlYXRlIE9yZGVyU2FnYQogICAgT3JkZXItIC0+%0D%0APk9yZGVyU2FnYTogT3JkZXJQbGFjZWRFdnQKICAgIGFjdGl2YXRlIE9yZGVyU2FnYQoKICAgIE9y%0D%0AZGVyU2FnYS0gLT4+U2hpcG1lbnQ6IFByZXBhcmVTaGlwbWVudENtZAogICAgZGVhY3RpdmF0ZSBP%0D%0AcmRlclNhZ2EKICAgIAogICAgU2hpcG1lbnQtIC0+Pk9yZGVyU2FnYTogU2hpcG1lbnRQcmVwYXJl%0D%0AZEV2dAogICAgYWN0aXZhdGUgT3JkZXJTYWdhCiAgICAKICAgIE9yZGVyU2FnYS0gLT4+T3JkZXI6%0D%0AIFJlZ2lzdGVyU2hpcG1lbnRGb3JPcmRlclByZXBhcmVkQ21kCiAgICBkZWFjdGl2YXRlIE9yZGVy%0D%0AU2FnYQogICAgCiAgICBTaGlwbWVudC0gLT4+T3JkZXJTYWdhOiBTaGlwbWVudEFycml2ZWRFdnQK%0D%0AICAgIGFjdGl2YXRlIE9yZGVyU2FnYQogICAgCiAgICBPcmRlclNhZ2EtIC0+Pk9yZGVyOiBSZWdp%0D%0Ac3RlclNoaXBtZW50Rm9yT3JkZXJBcnJpdmVkQ21kCiAgICBkZXN0cm95IE9yZGVyU2FnYQpAZW5k%0D%0AdW1sCgpQbGFudFVNTCB2ZXJzaW9uIDEuMjAxOS4xMihTdW4gTm92IDAzIDEwOjI0OjU0IFVUQyAy%0D%0AMDE5KQooR1BMIHNvdXJjZSBkaXN0cmlidXRpb24pCkphdmEgUnVudGltZTogSmF2YShUTSkgU0Ug%0D%0AUnVudGltZSBFbnZpcm9ubWVudApKVk06IEphdmEgSG90U3BvdChUTSkgNjQtQml0IFNlcnZlciBW%0D%0ATQpKYXZhIFZlcnNpb246IDEuNy4wXzI1LWIxNQpPcGVyYXRpbmcgU3lzdGVtOiBMaW51eApEZWZh%0D%0AdWx0IEVuY29kaW5nOiBVVEYtOApMYW5ndWFnZTogZW4KQ291bnRyeTogVVMKLS0+PC9nPjwvc3Zn%0D%0APg==)

```puml
@startuml
    participant OrderSaga #LightBlue
    participant Order #LightBlue
    participant Shipment #LightGreen

    create OrderSaga
    Order-->>OrderSaga: OrderPlacedEvt
    activate OrderSaga

    OrderSaga-->>Shipment: PrepareShipmentCmd
    deactivate OrderSaga
    
    Shipment-->>OrderSaga: ShipmentPreparedEvt
    activate OrderSaga
    
    OrderSaga-->>Order: RegisterShipmentForOrderPreparedCmd
    deactivate OrderSaga
    
    Shipment-->>OrderSaga: ShipmentArrivedEvt
    activate OrderSaga
    
    OrderSaga-->>Order: RegisterShipmentForOrderArrivedCmd
    destroy OrderSaga
@enduml
```

> There are various patterns used to describe the relationships between different bounded contexts and teams that produce them:
> - Shared Kernel - This is where two teams **share some subset of the domain model**. This shouldn't be changed without the other team being consulted.
> - Customer-Supplier (upstream-downstream) - This is where the **downstream** team acts as a customer to the **upstream** team. The teams define automated acceptance tests which validate the interface the upstream team provide. The upstream team can then make changes to their code without fear of breaking something downstream. I think this is where [Consumer Driven Contracts](https://www.martinfowler.com/articles/consumerDrivenContracts.html) come into play.
> - Conformist (upstream-downstream) - This is where the **downstream** team conforms to the model of the **upstream** team despite that model not meeting their needs. The reason for doing this is so that we will no longer need a complicated anti corruption layer between the two models. This is not the same as customer/supplier because the teams are not using a cooperative approach - the upstream are deriving the interfaces independently of what downstream teams actually need.
> - Partner - The idea is that two teams have a **mutual dependency** on each other for delivery. They therefore need to work together on their modeling efforts.
> - Anti-Corruption Layer (upstream-downstream) - The **downstream** team builds a layer to prevent **upstream** design to 'leak' into their own models, by transforming interactions.
> - Separate Ways - cut them loose.
>
> Inverting [Conway’s Law](https://en.wikipedia.org/wiki/Conway%27s_law) allows us to align our organizational structure to our bounded contexts.
> *"Any organization that designs a system will produce a design whose structure is a copy of the organization’s communication structure."*
>
> There should be one team assigned to work on one Bounded Context. There should also be a separate source code repository for each Bounded Context.
It is possible that one team could work on multiple Bounded Contexts, but multiple teams should not work on a single Bounded Context.
>
> As our organizational structure is changing and our application evolves to microservices, we tend to diverge from `Conformist` and/or `Partner` to `Customer-Supplier` and/or `Anti-Corruption Layer` bounded context relationships, depending only on the schema of the messages. 
> We define automated acceptance tests [(Consumer Driven Contracts)](https://www.martinfowler.com/articles/consumerDrivenContracts.html) which validate the interface the upstream team provide.

### Consumer Driven Contracts

[Pact](https://docs.pact.io/) is used to test message passing contracts. Pact is a code-first tool for testing HTTP and message integrations using contract tests.
As we have serialized our messages (commands, events and queries) to JSON we can utilize Pact nicely.
```properties
axon.serializer.events=jackson
axon.serializer.messages=jackson
```

The [consumer test (ordering)](/ordering/src/test/java/com/example/orderdemo/ordering/command/OrderSagaTest.java) make use of the `JVM Consumer DSL` to describe the message format pacts and provide example data.
Regular Axon Saga fixture test is enriched and extended with the Pact framework to prove that our consumer adheres to the contract.
The contracts are persisted in [pacts](/pacts) folder, upon the consumer test execution.

Now let’s switch over to the [provider (shipping) test](/shipping/src/test/java/com/example/orderdemo/shipping/command/ShipmentTest.java) which needs to verify that it is able to produce the expected messages.
Regular Axon Aggregate fixture test is extended with the Pact framework to verify that the producer of the API (shipping) is able to produce expected events or handle expected commands.

In the real world you should consider using [Pact Broker](https://docs.pact.io/pact_broker/overview) instead of sharing contracts in the [pacts](pacts) folder.

> Contract testing ensures that a pair of applications will work correctly together by checking each application in isolation to ensure the messages it sends or receives conform to a shared understanding that is documented in a "contract".

## Development

This project is driven using [maven].

### Run Axon Server

You can [download](https://download.axoniq.io/axonserver/AxonServer.zip) a ZIP file with AxonServer as a standalone JAR. This will also give you the AxonServer CLI and information on how to run and configure the server.

Alternatively, you can run the following command to start AxonServer in a Docker container:

```
$ docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver
```

### Build, Test and Run locally

You can run the following command(s) to start your project(s) locally:

```
$ mvn clean verify
$ cd ordering && mvn spring-boot:run
$ cd shipping && mvn spring-boot:run
```


### In-memory database

We use H2 SQL database. Web console is enabled and it should be available on `/h2-console` URL (eg. `http://localhost:8080/h2-console`). Check  `application.properties` for the datasource URL.

### References

- https://axoniq.io/blog-overview/bounded-contexts-with-axon
- https://github.com/fransvanbuul/orderdemo
- https://blog.codecentric.de/en/2019/11/message-pact-contract-testing-in-event-driven-applications/

---
Created with :heart: by [Ivan Dugalic](http://idugalic.pro/)

[maven]: https://maven.apache.org/ (Maven)
[axon]: https://axoniq.io/ (Axon)
