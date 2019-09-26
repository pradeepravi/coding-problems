package com.example.dogbreed.controller.service;

import com.amazonaws.services.lexmodelbuilding.model.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.RandomStringUtils;
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

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class DogBreedService {

    public static final String BREEDS = "breeds/";
    public static final String EMPTY_SPACE = " ";
    public static final String JPG = "jpg";
    private S3UploadsService s3UploadsService;
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${dogBreed.url}")
    private String randomDogBreedUrl;
    @Autowired
    public DogBreedService(S3UploadsService s3Uploads) {
        this.s3UploadsService = s3Uploads;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DogBreedService.class);

    public String getRandomDogBreeds() {
        // TODO generate Random Dog breed
        // Save to S3
        // Return the S3 URL to user

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
                String serviceResponseStatus = result.getBody().get("status").asText();
                if ("success".equalsIgnoreCase(serviceResponseStatus)) {
                    String dogBreedUrl = result.getBody().get("message").asText();
                    final String dogName = extractDogName(dogBreedUrl);
                    saveToS3(dogBreedUrl, dogName);
                    return dogBreedUrl;
                }
            }
        }
        throw new NotFoundException("Not able to generate a Dog Breed for you");
    }

    private String extractDogName(final String imageUrl){
        //https://images.dog.ceo/breeds/sheepdog-shetland/n02105855_18293.jpg
        // Just Getting Dog name out "sheepdog shetland"

        final String dogName = imageUrl.substring(imageUrl.indexOf(BREEDS), imageUrl.lastIndexOf("/")).replaceAll("("+BREEDS+"|-)", EMPTY_SPACE).trim();
        LOG.info("Dog Name - "+dogName);
        return dogName;
    }

    private String saveToS3(String imageUrl, String dogName){
        try {
            final URL url = new URL(imageUrl);
            final BufferedImage img =  ImageIO.read(url);
            final File file = new File(dogName);
            ImageIO.write(img, JPG, file);

            this.s3UploadsService.uploadFile(generateRandomName(), file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getDatePart(){
        return LocalDate.now().toString();
    }

    private static String generateRandomName(){
        return (getDatePart()+"-"+ RandomStringUtils.randomAlphanumeric(8))+".jpg";//TODO Make it more generic
    }
}
