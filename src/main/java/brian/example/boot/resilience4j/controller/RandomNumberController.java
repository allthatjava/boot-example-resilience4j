package brian.example.boot.resilience4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import brian.example.boot.resilience4j.service.RandomNumberService;

@RestController
public class RandomNumberController {
	@Autowired
	RandomNumberService service;

	/**
	 * Returns the timeDelay that is give if it is less than 5000 (5 seconds).
	 * 										---> Check application.yml for this setup.
	 * Otherwise, it will return error message
	 * 
	 * @param timeDelay
	 * @return
	 */
	@GetMapping(value="/delayedTimeTest/{timeDelay}")
	public String getDelayedTimeCheck(@PathVariable("timeDelay") int timeDelay) {
		
		return service.getDelayedRandomNumber(timeDelay)+"";
	}
}
