package io.fabric8.quickstarts.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("AuditProcessor")
public class CrmAuditProcessor implements Processor  {
	
	  public void process(Exchange exchange) throws Exception {
		  System.out.println("Audit Processor called");
	  }
}