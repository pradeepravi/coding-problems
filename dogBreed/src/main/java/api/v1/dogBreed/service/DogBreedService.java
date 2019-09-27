package api.v1.dogBreed.service;

import api.v1.dogBreed.data.entity.DogBreed;
import api.v1.dogBreed.data.repository.DogBreedRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class DogBreedService {

    public static final String BREEDS = "breeds/";
    public static final String EMPTY_SPACE = " ";
    public static final String JPG = "jpg";
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String JPG_FILE_EXTENSION = ".jpg";
    public static final String TEMP_FILES_LOCATION = "/tmp/";
    private S3UploadsService s3UploadsService;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${dogBreed.url}")
    private String randomDogBreedUrl;

    private DogBreedRepository dogBreedRepository;

    @Autowired
    public DogBreedService(S3UploadsService s3Uploads, DogBreedRepository dogBreedRepository) {
        this.s3UploadsService = s3Uploads;
        this.dogBreedRepository = dogBreedRepository;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DogBreedService.class);

    /**
     * Method generates a new Dog breed from the URL configured in system. Stores the image in S3 storage and creates a
     * new entry in database.
     *
     * @return {@link DogBreed}
     * @throws DogBreedException if not able to generate a new Dog breed
     */
    public DogBreed getRandomDogBreeds() throws DogBreedException {
        /*
        Response Like
        {
          "message": "https://images.dog.ceo/breeds/bouvier/n02106382_1598.jpg",
          "status": "success"
        }
         */
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(ACCEPT, APPLICATION_JSON_VALUE);

        final ResponseEntity<JsonNode> result = restTemplate.exchange(randomDogBreedUrl, GET, new HttpEntity<>(headers), JsonNode.class);
        if(result.getStatusCode() == HttpStatus.OK){
            //All good
            if(result.hasBody()) {
                String serviceResponseStatus = result.getBody().get(STATUS).asText();
                if (SUCCESS.equalsIgnoreCase(serviceResponseStatus)) {
                    final String dogBreedUrl = result.getBody().get(MESSAGE).asText();
                    final String dogName = extractDogName(dogBreedUrl);
                    final String fileName = generateRandomName();
                    this.saveToS3Storage(dogBreedUrl, dogName, fileName);

                    DogBreed dogBreed = populateDogBreed(dogName, fileName);
                    dogBreed = createDogBreedEntry(dogBreed);

                    LOG.info("Dog Breed"+dogBreed.getId());
                    //return this.s3UploadsService.getEndpointUrl() +"/"+ fileName;
                    return dogBreed;
                }
            }
        }
        throw new DogBreedException("Not able to generate a new Dog Breed. Try Again Later");
    }

    /**
     * Method populates a {@link DogBreed} object
     *
     * @param dogName
     * @param fileName
     * @return {@link DogBreed}
     */
    private DogBreed populateDogBreed(String dogName, String fileName) {
        final DogBreed dogBreed = new DogBreed();
        dogBreed.setCreatedDate(OffsetDateTime.now());
        dogBreed.setDogBreed(dogName);
        dogBreed.setImageLocation(fileName);

        return dogBreed;
    }

    /**
     * Method to extract Dog Breed name from the image URL passed in pram
     *
     * @param imageUrl
     * @return - String
     */
    private String extractDogName(final String imageUrl) throws DogBreedException {

        if(!imageUrl.contains(BREEDS)) {
            LOG.error("Random URL passed not in expected format from service provider - ["+imageUrl+"]");
            throw new DogBreedException("Unable to generate Dog Breed name");
        } else if (StringUtils.isBlank(imageUrl)) {
            LOG.error("No image URL Passed");
            throw new DogBreedException("Unable to generate Dog Breed name");
        }

        //https://images.dog.ceo/breeds/sheepdog-shetland/n02105855_18293.jpg
        // Just Getting Dog name out "sheepdog shetland"
        final String dogName = imageUrl.substring(imageUrl.indexOf(BREEDS), imageUrl.lastIndexOf("/"))
                .replaceAll("(" + BREEDS + "|-)", EMPTY_SPACE).trim();
        LOG.info("Dog Name Extracted [" + dogName+"]");
        return dogName;
    }

    /**
     * Private method to store image to AWS S3 and returns the saved image's fileName
     *
     * @param imageUrl
     * @param dogName
     * @param fileName
     * @return String
     */
    private String saveToS3Storage(String imageUrl, String dogName, String fileName) throws DogBreedException {
        File file = null;
        try {
            final URL url = new URL(imageUrl);
            final BufferedImage img =  ImageIO.read(url);
            file = new File(TEMP_FILES_LOCATION +dogName);
            ImageIO.write(img, JPG, file);
            this.s3UploadsService.uploadFile(fileName, file);
            return fileName;
        } catch (IOException e) {
            LOG.error("IO Exception when trying to store file "+fileName+"to S3 Storage");
            throw new DogBreedException("Failed to store image ["+imageUrl+"] to S3");
        } finally {
            if(file != null) {
                LOG.info("Deleted local file status - "+file.delete());
            }
        }

    }

    /**
     * Method deletes the Dog breed entry from database and removes the Image from S3 storage
     * @param id
     * @throws {@link DogBreedException}
     */
    public void deleteDogBreed(Long id) throws DogBreedException {
        DogBreed dogBreedToDelete = this.dogBreedRepository.findById(id).orElseThrow(
                () -> new DogBreedException("No entry found for Dog Breed ID - "+id));
        String s3ImageLocation = dogBreedToDelete.getImageLocation();

        this.s3UploadsService.delete(s3ImageLocation);

        this.dogBreedRepository.delete(dogBreedToDelete);
    }

    /**
     * Method returns the DogBreed object for the ID passed. If not found will throw an exception
     * @param id
     * @return - {@link DogBreed}
     * @throws DogBreedException
     */
    public DogBreed getDogBreed(Long id) throws DogBreedException {
        return this.dogBreedRepository.findById(id).orElseThrow(
                () -> new DogBreedException("No entry found for Dog Breed ID - "+id));
    }

    /**
     * Method returns a list of Dog breed names available in our system. Returns an empty list if none found
     *
     * @return - List<String>
     */
    public List<String> getAllDogBreedNames() {
        return this.dogBreedRepository.findAllByDogBreed();
    }

    /**
     * method returns all database entries for the Dog breed name passed.
     *
     * @param dogBreedName
     * @return
     */
    public List<DogBreed> getAllEntries(String dogBreedName) throws DogBreedException {
        return this.dogBreedRepository.findAllByDogBreed(dogBreedName).orElseThrow(() -> new DogBreedException("No Dog Breeds" +
                " Found in System for the Name ["+dogBreedName+"]"));
    }

    /**
     * Method returns today's date
     *
     * @return - String
     */
    private static String getDatePart(){
        return LocalDate.now().toString();
    }

    /**
     * Method generates a random name for saving the file against
     *
     * @return - String
     */
    private static String generateRandomName(){
        return (getDatePart()+"-"+ RandomStringUtils.randomAlphanumeric(8))+ JPG_FILE_EXTENSION;//TODO Make it more generic image type
    }

    /**
     * Method saves Dog Breed to database
     * @param dogBreed
     * @return - {@link DogBreed}
     */
    private DogBreed createDogBreedEntry(DogBreed dogBreed){
        return this.dogBreedRepository.save(dogBreed);
    }
}
