package com.example.sandbox.postNewPet;

import java.util.List;

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
public class PostNewPetTest extends Common {

    private PostCreatePetSimple postCreatePetSimple;

    @BeforeMethod
    public void setUp() {
        this.postCreatePetSimple = PostCreatePetSimple.builder().build();
    }

    @Test(enabled = true, groups = {SMOKE}, description = "POSITIVE TEST - create a pet with all mandatory and valid data")
    public void postPetTest_withValidData() {
        //WHEN create petBody request
        Item category = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);
        Item secondTag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = postCreatePetSimple.createPetBodyDto(generateRandomNumber(), TestData.CAT_NAME, category, List.of(HYDRAIMAGE), List.of(tag, secondTag), PetStatus.SOLD.toString());


        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.assertResponseBasedOnPetBodyRequest(response,petBody);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(response.jsonPath().get("status"), PetStatus.SOLD.toString());
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - validate response time")
    public void postPetTest_withResponseTimeValidation() {
        Item category = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = postCreatePetSimple.createPetBodyDto(generateRandomNumber(), TestData.CAT_NAME, category, HYDRAIMAGE, tag, PetStatus.AVAILABLE.toString());

        PostCreatePet body = postCreatePetSimple.createPostCreatePet(petBody);

        Response response = postUrl(newPet, createJsonBody(body));
        PetBody petBodyFromResponse = response.getBody().as(PetBody.class);

        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(petBodyFromResponse.getStatus(), PetStatus.AVAILABLE.toString());
        Assertions.validateResponseTime(response);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - without pet name")
    public void postPetTest_withoutPetName() {
        Item category = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = postCreatePetSimple.createPetBodyDto(generateRandomNumber(), null, category, HYDRAIMAGE, tag, PetStatus.AVAILABLE.toString());

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
    }

}
