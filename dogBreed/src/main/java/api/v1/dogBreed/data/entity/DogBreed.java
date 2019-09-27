package api.v1.dogBreed.data.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="dog_breed")
public class DogBreed {
    @Id
    @GeneratedValue
    Long id;

    @Column(name="dog_breed")
    String dogBreed;

    @Column(name="created_date")
    OffsetDateTime createdDate;

    @Column(name="image_location")
    String imageLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
