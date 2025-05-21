package com.example.sandbox.getPet;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.example.sandbox.Common;
import com.example.sandbox.util.Assertions;
import static com.example.sandbox.util.Tools.generateRandomNumber;
import static com.example.sandbox.util.body.pet.JsonBody.createJsonBody;
import com.example.sandbox.util.body.pet.PostCreatePet;
import com.example.sandbox.util.body.pet.PostCreatePetSimple;
import com.example.sandbox.util.constans.PetStatus;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import com.example.sandbox.util.constans.TestData;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import io.restassured.response.Response;
import utils.report.TestListener;

@Listeners(TestListener.class)
public class PetDetailTest extends Common {

    private PostCreatePetSimple postCreatePetSimple;

    @BeforeMethod
    public void setUp() {
        this.postCreatePetSimple = PostCreatePetSimple.builder().build();
    }

    @Test(enabled = true, groups = {SMOKE}, description = "POSITIVE TEST - get pet based on existing pet id")
    public void getPetByIdTest_withExistingPetId() {
        Item category = postCreatePetSimple.createItem(generateRandomNumber(), TestData.DOG_PET_CATEGORY);
        Item tag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);
        String petId = createPet(TestData.DOG_NAME, category, HYDRAIMAGE, tag, PetStatus.AVAILABLE.toString());

        Response response = getPetByPetId(petById, petId);
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(response.jsonPath().get("status"), PetStatus.AVAILABLE.toString());
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - get pet based on NON-EXISTENT pet id")
    public void getPetByIdTest_withNOnExistentPetId() {

        Response response = getPetByPetId(petById, "NON-EXISTENT-PET-ID");
        Assertions.validateReturnCode(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - get pet based on invalid status")
    public void getPetByIdTest_withInvalidStatus() {
        Item category = postCreatePetSimple.createItem(generateRandomNumber(), TestData.DOG_PET_CATEGORY);
        Item tag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);
        String petId = createPet(TestData.DOG_NAME, category, HYDRAIMAGE, tag, "INVALID-STATUS");

        Response response = getPetByPetId(petById, petId);
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(response.jsonPath().get("status"), "INVALID-STATUS");
    }

    private String createPet(String petName, Item category, String photoUrl, Item tag, String status) {
        int petId = generateRandomNumber();
        PetBody petBody = postCreatePetSimple.createPetBodyDto(petId, petName, category, photoUrl, tag, status);

        PostCreatePet body = PostCreatePet.builder().PetBody(petBody).build();

        Response response = postUrl(newPet, createJsonBody(body));
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        return response.jsonPath().get("id").toString();
    }
}
