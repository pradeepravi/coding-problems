package api.v1.dogBreed.data.repository;

import api.v1.dogBreed.data.entity.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {

    @Query("SELECT DISTINCT dogBreed from DogBreed")
    List<String> findAllByDogBreed();

    Optional<List<DogBreed>> findAllByDogBreed(String name);
}
