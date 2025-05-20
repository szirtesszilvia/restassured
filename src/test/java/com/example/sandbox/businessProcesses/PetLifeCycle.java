package com.example.sandbox.businessProcesses;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import utils.report.ReportingFilter;
import utils.report.TestListener;

@Listeners(TestListener.class)
@Test()
public class PetLifeCycle extends Common {

    private static final Logger consoleLogger = LogManager.getLogger(ReportingFilter.class);

    @BeforeClass
    public void loggingClassNameBeforeClass() {
        String className = this.getClass().getSimpleName();
        consoleLogger.info("The current class name is: " + className);
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: creating, modifying and deleting")
    public void BusinessFlowTest() throws JsonProcessingException {

        int petId = generateRandomNumber();

        Item category = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.DOG_PET_CATEGORY);
        Item tag = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.FOOD_DONUT_ID_TAG);
        PetBody petBody = PostCreatePetSimple.createPetBodyDto(petId, TestData.DOG_NAME, category, HYDRAIMAGE, tag, PetStatus.PENDING.toString());

        PostCreatePet body = PostCreatePet.builder().PetBody(petBody).build();

        Response response = postUrl(newPet, createJsonBody(body));
        validateResponse(response, petBody);

        // Create new petBody request and call PUT /pet endpoint
        Item catCategory = PostCreatePetSimple.createItem(generateRandomNumber(), TestData.CAT_PET_CATEGORY);
        PetBody modifiedPetBody = PostCreatePetSimple.createPetBodyDto(petId, TestData.CAT_NAME, catCategory, HYDRAIMAGE, null, null);

        Response modifiedResponse = modifyPetData(modifiedPetBody);
        Assertions.assertReturnCode(modifiedResponse, HttpStatus.SC_OK);

        Response getPetByPetIdResponse = getPetByPetId(petById, String.valueOf(petId));
        Assertions.assertReturnCode(getPetByPetIdResponse, HttpStatus.SC_OK);
        //     Assertions.assertResponseBasedOnRequest(getPetByPetIdResponse, modifiedPetBody);

        // DELETE pet
        Response deleteResponse = deletePetByPetId(deletePetById, Map.of(TestData.APY_KEY_HEADER_KEY, TestData.APY_KEY_HEADER_VALUE), petId);
        Assertions.assertReturnCode(deleteResponse, HttpStatus.SC_OK);

        getPetByPetIdResponse = getPetByPetId(petById, String.valueOf(petId));
        Assertions.assertReturnCode(getPetByPetIdResponse, HttpStatus.SC_NOT_FOUND);

    }

    @Test(enabled = true, groups = {SMOKE}, description = "Business flow testing: call POST /pet/{petId}")
    public void BusinessFlowTest_withPet() throws JsonProcessingException {
        Map<String, String> queryMap = Map.of("name", "PET NAME", "status", "pending");

        postPetWithPetId(petById, generateRandomNumber(), queryMap);
    }

    private void validateResponse(Response response, PetBody petBody) throws JsonProcessingException {
        Assertions.assertReturnCode(response, HttpStatus.SC_OK);
        Assertions.validatePetId(response.jsonPath().getInt("id"), petBody.getId());

        Item categoryItem = TestHelper.convertCategoryMapToItem(response);
        Assertions.validateCategory(categoryItem, petBody.getCategory());

        Assertions.validatePetName(response.jsonPath().get("name"), petBody.getName());
        Assertions.validateStatus(response.jsonPath().get("status"), petBody.getStatus());
    }

    private Response modifyPetData(PetBody petBody) {
        PostCreatePet body = PostCreatePet.builder()
                .PetBody(petBody
                ).build();
        Response response = putPet(putPet, createJsonBody(body));
        return response;
    }
}
