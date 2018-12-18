package io.fabric8.quickstarts.camel.routebuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
class Backend extends RouteBuilder {

    @Override
    public void configure() {
        // A first route generates some orders and queue them in DB
        from("timer:new-order?repeatCount=3&delay=1s&period={{quickstart.generateOrderPeriod:2s}}")
            .routeId("generate-order")
            .bean("orderService", "generateOrder")
            .to("sql:insert into orders (id, item, amount, description, processed) values " +
                "(:#${body.id} , :#${body.item}, :#${body.amount}, :#${body.description}, false)?" +
                "dataSource=dataSource")
            .log("Inserted new order ${body.id}");

        // A second route polls the DB for new orders and processes them
        from("sql:select * from orders where processed = false?" +
            "consumer.onConsume=update orders set processed = true where id = :#id&" +
            "consumer.delay={{quickstart.processOrderPeriod:5s}}&" +
            "dataSource=dataSource")
            .routeId("process-order")
            .bean("orderService", "rowToOrder")
            .log("Processed order #id ${body.id} with ${body.amount} copies of the «${body.description}» book");
    }
}