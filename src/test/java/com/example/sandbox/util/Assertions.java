package com.example.sandbox.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;

import com.example.sandbox.util.constans.PetStatus;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.response.Response;
import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {

    /**
     * Validate HTTP status code
     *
     * @param response
     *         Response
     * @param httpCode
     *         int
     */
    public static void assertReturnCode(Response response, int httpCode) {
        assertThat(response.getStatusCode()).as("HTTP status code should be %s", httpCode).isEqualTo(httpCode);
    }

    /**
     * Validate response time
     *
     * @param response
     *         Response
     */
    public static void asserResponseTime(Response response) {
        System.out.println("RESPONSE TIME IS: " + response.getTimeIn(TimeUnit.MILLISECONDS));
        assertThat(response.getTimeIn(TimeUnit.MILLISECONDS)).isLessThanOrEqualTo(500L);
    }

    /**
     * Validate Json schema
     *
     * @param response
     *         Response
     */
    public static void validateJsonSchema(Response response) {
        MatcherAssert.assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("petBodyJsonSchema.json"));
    }

    // Assert all fields in the response based on request
    // There is difference between swagger PetBody and 'restAssured' petBody fields
    public static void assertResponseBasedOnRequest(Response response, PetBody petBodyRequest) {
        List<Item> responseTags = response.jsonPath().get("tags");
        assertThat(responseTags).usingRecursiveComparison().isEqualTo(petBodyRequest);
    }

    /**
     * Validate id in response
     *
     * @param actualPetId
     *         int
     * @param expectedPetId
     *         int
     */
    public static void validatePetId(int actualPetId, int expectedPetId) {
        assertThat(actualPetId).as("Pet id is different in request and response").isEqualTo(expectedPetId);
    }

    /**
     * Validate category in response
     *
     * @param actualCategoryItem
     *         Item
     * @param expectedCategoryItem
     *         Item
     */
    public static void validateCategory(Item actualCategoryItem, Item expectedCategoryItem) {
        assertThat(actualCategoryItem.getId()).as("Category id is different in request and response").isEqualTo(expectedCategoryItem.getId());
        assertThat(actualCategoryItem.getName()).as("Category name is different in request and response").isEqualTo(expectedCategoryItem.getName());
    }

    /**
     * Validate name in response
     *
     * @param actualPetName
     *         String
     * @param expectedPetName
     *         String
     */
    public static void validatePetName(String actualPetName, String expectedPetName) {
        assertThat(actualPetName).as("Category name is different in request and response").isEqualTo(expectedPetName);
    }

    public static void validateAllStatuses(Response response, String expectedStatus, int expectedPetNumber){
        List<PetBody> actualStatuses = response.jsonPath().getList("pet");
        assertThat(actualStatuses.stream().filter(c -> c.getStatus().equals(expectedStatus)).collect(Collectors.toList())).hasSize(expectedPetNumber);
    }

    /**
     * Validate status comes from {@link PetStatus}
     *
     * @param actualStatus
     *         String
     * @param expectedStatus
     *         String
     */
    public static void validateStatus(String actualStatus, String expectedStatus) {
        assertThat(actualStatus).as("Status is different in request and response").isEqualTo(expectedStatus);
        assertThat(actualStatus).as("Status should came from PetStatus enum").containsAnyOf("available", "pending", "sold");
    }

}
