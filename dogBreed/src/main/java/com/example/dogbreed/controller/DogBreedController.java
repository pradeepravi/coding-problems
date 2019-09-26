package com.example.dogbreed.controller;

import com.example.dogbreed.controller.service.DogBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
public class DogBreedController {
    DogBreedService dogBreedService;

    @Autowired
    public DogBreedController(DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    @GetMapping("/health")
    public String healthCheck(){
        return "I AM ALIVE";
    }

    @GetMapping("/dogbreed")
    public String getDogBreed() {
        return this.dogBreedService.getRandomDogBreeds();
    }
}
