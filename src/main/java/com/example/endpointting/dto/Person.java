package com.example.endpointting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Person{

private String name;
private String gender;
private double genderProbability;
private int age;
private int ageCount;
private String country;
private double countryProbability;

    public Person(GenderizeResponse genderizeResponse ,AgifyResponse agifyResponse,
                  NationalizeResponse nationalizeResponse){
        this.name = genderizeResponse.getName();
        this.gender = genderizeResponse.getGender();
        this.genderProbability = genderizeResponse.getProbability()*100;
        this.age = agifyResponse.getAge();
        this.ageCount = agifyResponse.getAgeCount();
        this.country = nationalizeResponse.getCountry().get(0).getCountry_id();
        this.countryProbability = nationalizeResponse.getCountry().get(0).getProbability()*100;

    }

}
