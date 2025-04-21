package org.example.lab4;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class LabFour {
    private static final String BASE_URL = "https://40bd7228-e4c9-4f0d-9ada-1f7a720fae7c.mock.pstmn.io";
    private static final String GET_SUCCESS = "/success";
    private static final String GET_UNSUCCESS = "/unsuccess";
    private static final String POST = "/createSomething";
    private static final String PUT_SERVER_ERROR = "/updateMe";
    private static final String DELETE_GONE = "/deleteWorld";

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyGetSuccess() {
        given()
                .get(GET_SUCCESS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("status", equalTo("success"))
                .body("data", notNullValue());
    }

    @Test
    public void verifyGetUnsuccess() {
        given()
                .get(GET_UNSUCCESS)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("status", equalTo("error"))
                .body("message", containsString("Access denied"));
    }

    @Test
    public void verifyPostSuccess() {
        given()
                .queryParam("permission", "yes")
                .post(POST)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("status", equalTo("created"))
                .body("message", equalTo("World successfully created."));
    }

    @Test
    public void verifyPostBadRequest() {
        given().post(POST)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("status", equalTo("error"))
                .body("message", equalTo("Permission denied. Cannot create world."));
    }

    @Test
    public void verifyPutServerError() {
        Map<String, ?> updateBody = Map.of(
                "id", 123,
                "name", "UpdatedWorld"
        );

        given()
                .body(updateBody)
                .put(PUT_SERVER_ERROR)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("status", equalTo("error"))
                .body("message", containsString("Server failure"));
    }

    @Test
    public void verifyDeleteGone() {
        given()
                .delete(DELETE_GONE)
                .then()
                .statusCode(HttpStatus.SC_GONE)
                .body("status", equalTo("deleted"))
                .body("message", containsString("World no longer available"));
    }
}