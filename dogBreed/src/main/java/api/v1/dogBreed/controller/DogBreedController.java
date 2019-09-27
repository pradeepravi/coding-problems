package api.v1.dogBreed.controller;

import api.v1.dogBreed.data.entity.DogBreed;
import api.v1.dogBreed.service.DogBreedException;
import api.v1.dogBreed.service.DogBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/dog-breeds")
public class DogBreedController {
    private DogBreedService dogBreedService;

    @Autowired
    public DogBreedController(DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    /**
     * Method generates a new Dog Breed and returns it
     * @return
     */
    @GetMapping("/generate")
    public ResponseEntity getGeneratedDogBreed() {
        try {
            return ResponseEntity.ok(this.dogBreedService.getRandomDogBreeds());
        } catch (DogBreedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error. " +
                    "Not able to generate Dog Breed at the moment. Please try again later");
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> getDogBreeds() {
        return ResponseEntity.ok(this.dogBreedService.getAllDogBreedNames());
    }

    /**
     * Method Returns unique list of Dog breed names. Empty list if none found
     * @param name
     * @return
     */
    @GetMapping("/{name}/all")
    public ResponseEntity getAllDogBreeds(@PathVariable String name){
        try {
            return ResponseEntity.ok(this.dogBreedService.getAllEntries(name));
        } catch (DogBreedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Dog Breeds found for name ["+name+"]");
        }
    }


    /**
     * Method returns Dog Breed entry for ID passed in path. Responds with a 404 if an invalid ID is passed
     * @param id
     * @return - ResponseEntity with {@link DogBreed}
     */
    @GetMapping("/{id}")
    public ResponseEntity getDogBreed(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.dogBreedService.getDogBreed(id));
        } catch (DogBreedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Method removes Dog Breed entry for ID passed in path. Responds with a 404 if an invalid ID is passed
     * @param id
     * @return - An Ok response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity removeDogBreed(@PathVariable Long id) {
        try {
            this.dogBreedService.deleteDogBreed(id);
        } catch (DogBreedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok("Deleted Dog Breed ID - "+id);
    }

    /**
     * Method returns a Health check response
     *
     * @return
     */
    @GetMapping("/health")
    public String healthCheck(){
        return "I AM ALIVE";
    }
}
