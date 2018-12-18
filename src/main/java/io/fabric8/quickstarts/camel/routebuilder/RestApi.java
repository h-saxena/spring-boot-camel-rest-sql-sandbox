package io.fabric8.quickstarts.camel.routebuilder;

import java.util.concurrent.RejectedExecutionException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
class RestApi extends RouteBuilder {

    @Override
    public void configure() throws Exception {
    	                       
    	//errorHandler(loggingErrorHandler());
        onException(NullPointerException.class)
        //.handled(true)
        .process(
          new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
            	System.out.println("On Exception");
            }
          });
    	
    	from("direct:app-audit").id("crm.audit").doTry().process("AuditProcessor").doCatch(RejectedExecutionException.class).log("failed");
    	
        restConfiguration()
            .contextPath("/camel-rest-sql").apiContextPath("/api-doc")
                .apiProperty("api.title", "Camel REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .bindingMode(RestBindingMode.json);

        rest("/books").description("Books REST service")
            .get("/").description("The list of all the books")
                .route().routeId("books-api")
                .wireTap("direct:app-audit").executorServiceRef("customWireTapPoolExecutorService")
                .to("sql:select description from orders?" +
                    "dataSource=dataSource&" +
                    "outputClass=io.fabric8.quickstarts.camel.model.Book")
                .endRest()
            .get("order/{id}").description("Details of an order by id")
                .route().routeId("order-api")
                .wireTap("direct:app-audit").executorServiceRef("customWireTapPoolExecutorService")
                .to("sql:select * from orders where id = :#${header.id}?" +
                    "dataSource=dataSource&outputType=SelectOne&" +
                    "outputClass=io.fabric8.quickstarts.camel.model.Order");

        rest("/siebel").description("Books REST service")
        .get("/view").description("The list of all the books")
            .route().routeId("siebel.view")
            .wireTap("direct:app-audit").executorServiceRef("customWireTapPoolExecutorService")
            .to("direct:siebel.views.get");
       
        from("direct:siebel.views.get").id("siebel.views.get")
        .process("SiebelViewProcessor");

    }
}