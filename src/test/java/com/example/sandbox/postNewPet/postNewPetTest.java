package com.example.sandbox.postNewPet;

import java.util.List;

import org.apache.http.HttpStatus;
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
public class postNewPetTest extends Common {

    @Test(enabled = true, groups = {SMOKE}, description = "POSITIVE TEST - create a pet with all mandatory and valid data")
    public void postPetTest_withValidData() {
        //WHEN create petBody request
        Item category = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);
        Item secondTag = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = PostCreatePetSimple.createPetBodyDto(generateRandomNumber(), TestData.CAT_NAME, category, List.of(HYDRAIMAGE), List.of(tag, secondTag), PetStatus.SOLD.toString());

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assertions.assertReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(response.jsonPath().get("status"), PetStatus.SOLD.toString());
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - validate response time")
    public void postPetTest_withResponseTimeValidation() {
        Item category = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = PostCreatePetSimple.createPetBodyDto(generateRandomNumber(), TestData.CAT_NAME, category, HYDRAIMAGE, tag, PetStatus.AVAILABLE.toString());

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assertions.assertReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
        Assertions.validateStatus(response.jsonPath().get("status"), PetStatus.AVAILABLE.toString());
        Assertions.asserResponseTime(response);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "NEGATIVE TEST - without pet name")
    public void postPetTest_withoutPetName() {
        Item category = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item tag = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);
        PetBody petBody = PostCreatePetSimple.createPetBodyDto(generateRandomNumber(), null, category, HYDRAIMAGE, tag, PetStatus.AVAILABLE.toString());

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assertions.assertReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(response);
    }

}
