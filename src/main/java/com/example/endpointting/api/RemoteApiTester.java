package com.example.endpointting.api;

import com.example.endpointting.dto.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
public class RemoteApiTester implements CommandLineRunner {

    List<String> names = Arrays.asList("lars", "peter", "sanne", "kim", "david", "maja");

    private Mono<String> callSlowEndpoint(){
        Mono<String> slowResponse = WebClient.create()
                .get()
                .uri("http://localhost:8080/random-string-slow")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e-> System.out.println("UUUPS : "+e.getMessage()));
        return slowResponse;
    }

    public void callSlowEndpointBlocking(){
        long start = System.currentTimeMillis();
        List<String> ramdomStrings = new ArrayList<>();

        Mono<String> slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block()); //Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block());//Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block());//Three seconds spent
        long end = System.currentTimeMillis();
        ramdomStrings. add(0,"Time spent BLOCKING (ms): "+(end-start));

        System.out.println(ramdomStrings.stream().collect(Collectors.joining(",")));
    }
    public void callSlowEndpointNonBlocking(){
        long start = System.currentTimeMillis();
        Mono<String> sr1 = callSlowEndpoint();
        Mono<String> sr2 = callSlowEndpoint();
        Mono<String> sr3 = callSlowEndpoint();

        var rs = Mono.zip(sr1,sr2,sr3).map(t-> {
            List<String> randomStrings = new ArrayList<>();
            randomStrings.add(t.getT1());
            randomStrings.add(t.getT2());
            randomStrings.add(t.getT3());
            long end = System.currentTimeMillis();
            randomStrings.add(0,"Time spent NON-BLOCKING (ms): "+(end-start));
            return randomStrings;
        });
        List<String> randoms = rs.block(); //We only block when all the three Mono's has fulfilled
        System.out.println(randoms.stream().collect(Collectors.joining(",")));
    }
    private Mono<Gender> getGenderForName(String name) {
        WebClient client = WebClient.create();
        Mono<Gender> gender = client.get()
                .uri("https://api.genderize.io?name="+name)
                .retrieve()
                .bodyToMono(Gender.class);
        return gender;
    }


    public void getGendersBlocking() {
        long start = System.currentTimeMillis();
        List<Gender> genders = names.stream().map(name -> getGenderForName(name).block()).toList();
        long end = System.currentTimeMillis();
        System.out.println("Time for six external requests, BLOCKING: "+ (end-start));
    }

    public void getGendersNonBlocking() {
        long start = System.currentTimeMillis();
        var genders = names.stream().map(name -> getGenderForName(name)).toList();
        Flux<Gender> flux = Flux.merge(Flux.concat(genders));
        List<Gender> res = flux.collectList().block();
        long end = System.currentTimeMillis();
        System.out.println("Time for six external requests, NON-BLOCKING: "+ (end-start));
    }

    public CompletableFuture<AgifyResponse> fetchAgifyData(String name) {
        String agifyUrl = "https://api.agify.io/?name=" + name;
        RestTemplate restTemplate = new RestTemplate();
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(agifyUrl, AgifyResponse.class));
    }

    public CompletableFuture<GenderizeResponse> fetchGenderizeData(String name) {
        String genderizeUrl = "https://api.genderize.io/?name=" + name;
        RestTemplate restTemplate = new RestTemplate();
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(genderizeUrl, GenderizeResponse.class));
    }

    public CompletableFuture<NationalizeResponse> fetchNationalizeData(String name) {
        String nationalizeUrl = "https://api.nationalize.io/?name=" + name;
        RestTemplate restTemplate = new RestTemplate();
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(nationalizeUrl, NationalizeResponse.class));
    }

    public Mono<AgifyResponse> ageResponseMono(String name){
        WebClient webClient = WebClient.create();
       Mono<AgifyResponse> response = webClient.get()
               .uri("https://api.agify.io/?name=" + name)
               .retrieve()
               .bodyToMono(AgifyResponse.class);
       return response;
    }

    public Mono<GenderizeResponse> genderResponseMono(String name){
        WebClient webClient = WebClient.create();
        Mono<GenderizeResponse> response = webClient.get()
                .uri("https://api.genderize.io/?name=" + name)
                .retrieve()
                .bodyToMono(GenderizeResponse.class);
        return response;
    }

    public Mono<NationalizeResponse> countryResponseMono(String name){
        WebClient webClient = WebClient.create();
        Mono<NationalizeResponse> response = webClient.get()
                .uri("https://api.nationalize.io/?name=" + name)
                .retrieve()
                .bodyToMono(NationalizeResponse.class);
        return response;
    }

    public Mono<Person> getPersonInfo(String name){
        Mono<GenderizeResponse> gender = genderResponseMono(name);
        Mono<AgifyResponse> age = ageResponseMono(name);
        Mono<NationalizeResponse> country = countryResponseMono(name);


        return Mono.zip(gender, age, country)
                .map(joined -> new Person (joined.getT1(), joined.getT2(), joined.getT3()));
    }

   /* public Person createPersonResponse(AgifyResponse agifyResponse,
                                       GenderizeResponse genderizeResponse,
                                       NationalizeResponse nationalizeResponse) {
        Person response = new Person();
        response.setName(agifyResponse.getName());
        response.setGender(genderizeResponse.getGender());
        response.setGenderProbability((int) (genderizeResponse.getProbability() * 100));
        response.setAge(agifyResponse.getAge());
        response.setAgeCount(agifyResponse.getAgeCount());
        response.setCountry(nationalizeResponse.getCountries().get(0).getCountry_id());
        response.setCountryProbability((int) (nationalizeResponse.getCountries().get(0)
                .getProbability() * 100));
        return response;
    } */







    @Override
    public void run(String... args) throws Exception {
        //String randomStr = callSlowEndpoint().block();
        //System.out.println(randomStr);
        //callSlowEndpointNonBlocking();
        getGenderForName("Bob").subscribe( Gender ->
                System.out.println(Gender.getName()+Gender.getGender())  );
        getGendersBlocking();
        getGendersNonBlocking();

    }
}

