package com.example.endpointting.api;

import com.example.endpointting.dto.*;
import com.example.endpointting.service.PersonService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class DemoController {

    RemoteApiTester remoteApiTester;
    private final Map<String, Person> cache = new ConcurrentHashMap<>();

    public DemoController(RemoteApiTester remoteApiTester){this.remoteApiTester = remoteApiTester;
    }


    private final int SLEEP_TIME = 1000*3;

    @GetMapping(value = "/random-string-slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        return RandomStringUtils.randomAlphanumeric(10);
    }

   /* @GetMapping("/person")
    public CompletableFuture<Person> getPersonInfo(@RequestParam String name) {
        if (cache.containsKey(name)) {
            return CompletableFuture.completedFuture(cache.get(name));
        }

        CompletableFuture<AgifyResponse> agifyData = remoteApiTester.fetchAgifyData(name);
        CompletableFuture<GenderizeResponse> genderizeData = remoteApiTester.fetchGenderizeData(name);
        CompletableFuture<NationalizeResponse> nationalizeData = remoteApiTester.fetchNationalizeData(name);

        return CompletableFuture.allOf(agifyData, genderizeData, nationalizeData)
                .thenApplyAsync(ignored ->
                        remoteApiTester.createPersonResponse(agifyData.join(), genderizeData.join(), nationalizeData.join()));

}
    */
   @GetMapping("/person")
   public Person personResponse(@RequestParam String name){
       return remoteApiTester.getPersonInfo(name).block();
   }



}


