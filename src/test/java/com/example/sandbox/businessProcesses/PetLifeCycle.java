package com.example.sandbox.businessProcesses;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
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
import com.example.sandbox.util.test.helper.TestHelper;
import io.restassured.response.Response;
import utils.report.ReportingFilter;
import utils.report.TestListener;

@Listeners(TestListener.class)
@Test()
public class PetLifeCycle extends Common {

    private static final Logger consoleLogger = LogManager.getLogger(ReportingFilter.class);
    private PostCreatePetSimple postCreatePetSimple;
    private int petId;

    @BeforeClass
    public void loggingClassNameBeforeClass() {
        String className = this.getClass().getSimpleName();
        consoleLogger.info("The current class name is: " + className);
    }

    @BeforeMethod
    public void setUp() {
        this.postCreatePetSimple = PostCreatePetSimple.builder().build();
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: creating, modifying and deleting", priority = 1)
    public void businessFlowWithCreationTest() {
        // Call POST /pet endpoint
        petId = generateRandomNumber();
        Item dogCategory = postCreatePetSimple.createItem(generateRandomNumber(), TestData.DOG_PET_CATEGORY);
        Item foodDonutTag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);

        PetBody petBody = postCreatePetSimple.createPetBodyDto(petId, TestData.DOG_NAME, dogCategory, HYDRAIMAGE, foodDonutTag, PetStatus.PENDING.toString());
        PostCreatePet body = PostCreatePet.builder().PetBody(petBody).build();

        Response postPetResponse = postUrl(newPet, createJsonBody(body));
        validateCreatePetResponse(postPetResponse, petBody);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: creating, modifying and deleting", priority = 2, dependsOnMethods = {"businessFlowWithCreationTest"})
    public void businessFlowWithModificationTest() {
        // Call POST /pet endpoint
        Item catCategory = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        Item charmsSmallDaisyTag = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CHARMS_SMALL_DAISY_TAG);

        PetBody modifiedPetBody = postCreatePetSimple.createPetBodyDto(petId, TestData.CAT_NAME, catCategory, HYDRAIMAGE, charmsSmallDaisyTag, PetStatus.SOLD.toString());
        Response modifiedResponse = modifyPetData(modifiedPetBody);
        String petIdFromResponse = modifiedResponse.jsonPath().get("id").toString();
        Assertions.validateReturnCode(modifiedResponse, HttpStatus.SC_OK);
        Assertions.validateJsonSchema(modifiedResponse);

        Response getPetByPetIdResponse = getPetByPetId(petById, petIdFromResponse);
        Assertions.validateReturnCode(getPetByPetIdResponse, HttpStatus.SC_OK);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: creating, modifying and deleting", priority = 3, dependsOnMethods = {"businessFlowWithCreationTest", "businessFlowWithModificationTest"})
    public void businessFlowWithDeletionTest() {
        // Call POST /pet endpoint
        Response deleteResponse = deletePetByPetId(petById, Map.of(TestData.APY_KEY_HEADER_KEY, TestData.APY_KEY_HEADER_VALUE), petId);
        Assertions.validateReturnCode(deleteResponse, HttpStatus.SC_OK);

        Response getPetByPetIdResponse = getPetByPetId(petById, String.valueOf(petId));
        Assertions.validateReturnCode(getPetByPetIdResponse, HttpStatus.SC_NOT_FOUND);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: call POST /pet/{petId}", priority = 4)
    public void businessFlowTest_withPetByPetIdEndpoint() {
        int newPetId = generateRandomNumber();
        Map<String, String> queryMap = Map.of("name", "PET NAME", "status", "pending");

        Response response = postPetWithPetId(petById, newPetId, queryMap);
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);

        // Create new petBody request and call PUT /pet endpoint
        Item catCategory = postCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        PetBody modifiedPetBody = postCreatePetSimple.createPetBodyDto(newPetId, TestData.CAT_NAME, catCategory, HYDRAIMAGE, null, null);
        modifyPetDataWithValidation(modifiedPetBody);

        // DELETE pet
        deletePetWithValidation(newPetId);
    }

    private Response modifyPetData(PetBody petBody) {
        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();
        Response response = putPet(putPet, createJsonBody(body));
        return response;
    }

    private void validateCreatePetResponse(Response response, PetBody petBody) {
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validatePetId(response.jsonPath().getInt("id"), petBody.getId());

        Item categoryItem = TestHelper.convertCategoryMapToItem(response);
        Assertions.validateCategory(categoryItem, petBody.getCategory());

        Assertions.validatePetName(response.jsonPath().get("name"), petBody.getName());
        Assertions.validateStatus(response.jsonPath().get("status"), petBody.getStatus());
    }

    private void modifyPetDataWithValidation(PetBody modifiedPetBody) {
        Response modifiedResponse = modifyPetData(modifiedPetBody);
        String petId = modifiedResponse.jsonPath().get("id").toString();
        Assertions.validateReturnCode(modifiedResponse, HttpStatus.SC_OK);

        Response getPetByPetIdResponse = getPetByPetId(petById, petId);
        Assertions.validateReturnCode(getPetByPetIdResponse, HttpStatus.SC_OK);
    }

    private void deletePetWithValidation(int petId) {
        Response deleteResponse = deletePetByPetId(petById, Map.of(TestData.APY_KEY_HEADER_KEY, TestData.APY_KEY_HEADER_VALUE), petId);
        Assertions.validateReturnCode(deleteResponse, HttpStatus.SC_OK);

        Response getPetByPetIdResponse = getPetByPetId(petById, String.valueOf(petId));
        Assertions.validateReturnCode(getPetByPetIdResponse, HttpStatus.SC_NOT_FOUND);
    }

}
