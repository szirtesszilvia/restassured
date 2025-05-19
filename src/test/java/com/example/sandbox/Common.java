package com.example.sandbox;

import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import utils.report.ReportingFilter;

import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest
public class Common extends Endpoints {

    protected ReportingFilter filter;

    @BeforeMethod(alwaysRun = true)
    public void baseBeforeMethod(ITestContext context) {filter = new ReportingFilter(context);}

    //----------------------------------GET----------------------------------

    public Response getPetByPetId(String endpoint, String petId){
        return given()
                .relaxedHTTPSValidation()
                .pathParam("petId", petId)
                .and()
                .filter(filter)
                .when()
                .get(baseUrl+endpoint)
                .then()
                .extract().response();

    }

    public Response getPetByStatus(String endpoint){


        return given()
                .relaxedHTTPSValidation()
                .and()
                .filter(filter)
                .when()
                .get(baseUrl+endpoint)
                .then()
                .extract().response();

    }
    public Response getPetByStatus(String endpoint, Map<String, String> queryParam ){


        return given()
                .relaxedHTTPSValidation()
                .headers("correlationId","testCorrelid")
                .cookie("session_id", "abc123")
                .param("param","testParam")
                .formParam("asd","testFormParams")
                .queryParams(queryParam)
                .and()
                .filter(filter)
                .when()
                .get(baseUrl+endpoint)
                .then()
                .extract().response();

    }
    public Response getPetByStatus(String endpoint,Map<String, String> headers,Map<String, String> queryParam ){


        return given()
                .relaxedHTTPSValidation()
                .params(queryParam)
                .headers(headers)
                .and()
                .filter(filter)
                .when()
                .get(baseUrl+endpoint)
                .then()
                .extract().response();

    }

    //----------------------------------POST----------------------------------
    public Response postUrl(String endpoint,String body){


        return given()
                .relaxedHTTPSValidation()
                .contentType("application/json; charset=UTF-8")
                .body(body)
                .and()
                .filter(filter)
                .when()
                .post(baseUrl+endpoint)
                .then()
                .extract().response();
    }

    public Response postPetWithPetId(String endpoint, int petId, Map<String, String> queryParams){
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .accept("application/xml")
                .pathParam("petId", petId)
                .queryParams(queryParams)
                .and()
                .filter(filter)
                .when()
                .post(baseUrl+endpoint)
                .then()
                .extract().response();
    }

    //----------------------------------PUT----------------------------------

    public Response putPet(String endpoint,String body){
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/json; charset=UTF-8")
                .body(body)
                .and()
                .filter(filter)
                .when()
                .put(baseUrl+endpoint)
                .then()
                .extract().response();
    }

    //----------------------------------DELETE----------------------------------

    public Response deletePetByPetId(String endpoint, Map<String, String> headers, int petId){
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/json; charset=UTF-8")
                .headers(headers)
                .pathParam("petId", petId)
                .and()
                .filter(filter)
                .when()
                .delete(baseUrl+endpoint)
                .then()
                .extract().response();
    }
}

