package com.example.sandbox.getPetList;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpStatus;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.example.sandbox.Common;
import com.example.sandbox.util.Assertions;
import com.example.sandbox.util.constans.PetStatus;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import com.example.sandbox.util.constans.TestData;
import io.restassured.response.Response;
import utils.report.TestListener;


@Listeners(TestListener.class)
public class PetListTest extends Common {

    @Test(enabled = true, groups = {SMOKE}, description = "POSITIVE TEST - get pet data based on VALID status")
    public void getPetByStatusTest_withValidStatus() {
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(TestData.STATUS_QUERY_PARAM, PetStatus.PENDING.toString());
        Map<String, String> headers = new TreeMap<>();
        headers.put(TestData.APY_KEY_HEADER_KEY, TestData.APY_KEY_HEADER_VALUE);

        Response response = getPetByStatus(findByStatus, headers, queryParams);
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateAllStatuses(response, PetStatus.PENDING.toString());
    }

    @Test(enabled = true,groups = {SMOKE},description ="NEGATIVE TEST - get pet data based on invalid status")
    public void getPetByStatusTest_withInvalidStatus(){
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("status","NON-EXISTENT-STATUS");
        Map<String, String> headers = new TreeMap<>();
        headers.put(TestData.APY_KEY_HEADER_KEY,TestData.APY_KEY_HEADER_VALUE);

        Response  response = getPetByStatus(findByStatus,headers,queryParams);
        Assertions.validateReturnCode(response, HttpStatus.SC_OK);
        Assertions.validateResponseTime(response);
    }
}
