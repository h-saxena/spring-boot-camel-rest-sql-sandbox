package io.fabric8.quickstarts.camel.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.fabric8.quickstarts.camel.db.model.crmp.CrmViewQuery;
import io.fabric8.quickstarts.camel.db.repo.crmp.CrmViewQueriesRepository;
import io.fabric8.quickstarts.camel.db.repo.siebel.SiebelViewRepo;

@Component("SiebelViewProcessor")
public class SiebelViewProcessor implements Processor  {
	
	@Autowired
	CrmViewQueriesRepository crmpViewQueryRepo;

	@Autowired
	SiebelViewRepo siebelViewQueryRepo;

	  public void process(Exchange exchange) throws Exception {
		  try {
			String routeId = exchange.getFromRouteId();
			  CrmViewQuery q = crmpViewQueryRepo.findBySorAndName("SBL", "HemsDynamicTestQuery");
			  Map<String, Object> parameters = new HashMap<>();
			  parameters.put("crmAccountRowId", "1-2J-4367");
			  //List<Map<String, String>> result = siebelViewQueryRepo.query(q.getText(), parameters);
			  //List<Map<String, String>> result = siebelViewQueryRepo.queryCore(q.getText(), new Object[]{"1-2J-4367"});
			  List<Map<String, String>> result = siebelViewQueryRepo.queryCoreCustomTemplate(q.getText(), parameters);
			  exchange.getOut().setBody(result);
			  exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_OK);
		} catch (Exception e) {
			  exchange.getOut().setBody(e);
			  exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	  }
}