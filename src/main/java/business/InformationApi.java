package business;

import java.lang.management.ManagementFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InformationApi {

	@RequestMapping(value="/trigger",produces=MediaType.APPLICATION_JSON_VALUE)
	public String trigger() {
		
		long uptimeInMillis=ManagementFactory.getRuntimeMXBean().getUptime();
		System.out.println("New Trigger - Instance started "+(uptimeInMillis/1000)+" seconds ago.");
		return "Instance started "+(uptimeInMillis/1000)+" seconds ago.";
	}
	
	

}
