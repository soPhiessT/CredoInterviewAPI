package tests;

import Data.DataProviders;
import Models.UserResponse;
import Util.TestResultListener;
import Util.Utils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static Data.Constants.*;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Listeners(TestResultListener.class)
public class ApiTests {

    @BeforeSuite
    static void setup() {
        Utils.startWireMockServer();
        RestAssured.baseURI = BASE_URL;
        Utils.configureStubs();
    }

    @AfterSuite
    static void tearDown() {
        Utils.stopWireMockServer();
    }

    /**
     * ტესტის მიზანია რომ პოზიტიურ სცენარში გატესტოს getUsers სერვისის სიმულაცია
     * data provider ით ხდება პარამეტრების გადაცემა და რესპონსში მიღებული მონაცემების შემოწმება
     */
    @Test(dataProviderClass = DataProviders.class, dataProvider = "DataValidationProvider")
    public void testGetAllUsers_Positive(String name, int age, String gender) {
        Response response = given()
                .when()
                .get(API_ENDPOINT);

        response
                .then()
                .statusCode(200)
                .contentType("application/json");

        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);

        assertEquals(users.size(), 7, "Expected exactly 7 users in the response");
        UserResponse user = users.stream()
                .filter(item -> name.equals(item.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(user, String.format("%s should be present in the user list", name));
        assertEquals(user.getAge(), age, String.format("%s age should be" + age, name));
        assertEquals(user.getGender(), gender, String.format("%s gender should be" + gender, name));
    }

    /**
     * ტესტის მიზანია რომ პოზიტიურ სცენარში გატესტოს ?age =
     * data provider ით ხდება პარამეტრების გადაცემა და რესპონსში მიღებული მონაცემების შემოწმება
     */
    @Test(dataProviderClass = DataProviders.class, dataProvider = "AgeDataProvider")
    public void testFilterByAge_Positive(int age, int expectedCount) {
        Response response = given()
                .queryParam("age", age)
                .when()
                .get(API_ENDPOINT);

        response
                .then()
                .statusCode(200)
                .contentType("application/json");

        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);
        assertEquals(users.size(), expectedCount, "Expected " + expectedCount + " user(s) with age " + age);
    }

    /**
     * ტესტის მიზანია რომ პოზიტიურ სცენარში გატესტოს ?gender=
     * data provider ით ხდება პარამეტრების გადაცემა და რესპონსში მიღებული მონაცემების შემოწმება
     */
    @Test(dataProviderClass = DataProviders.class, dataProvider = "GenderDataProvider")
    public void testFilterByGender_Positive(String gender, int expectedCount) {
        Response response = given()
                .queryParam("gender", gender)
                .when()
                .get(API_ENDPOINT);

        response
                .then()
                .statusCode(200)
                .contentType("application/json");

        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);

        assertEquals(users.size(), expectedCount, "Expected " + expectedCount + " user(s) with gender " + gender);
    }

    /**
     * ტესტის მიზანია რომ ნეგატიურ სცენარში გატესტოს ?age=-1
     * ვამოწმებთ, სტატუს კოდს და რესონსში დაბრუნებულ ერორ მესიჯს
     */
    @Test
    public void testInvalidAge_Negative() {
        Response response = given()
                .queryParam("age", INVALID_AGE)
                .when()
                .get(API_ENDPOINT);

        String errorMessage = response.jsonPath().getString("error");

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertEquals(errorMessage, INVALID_PAGE_PARAMETER_ERROR, "error message should be" + INVALID_PAGE_PARAMETER_ERROR);
    }

    /**
     * ტესტის მიზანია რომ ნეგატიურ სცენარში გატესტოს internal server error
     * ვამოწმებთ, სტატუს კოდს და რესონსში დაბრუნებულ ერორ მესიჯს
     */
    @Test
    public void testInternalServerError_Negative() {
        Response response = given()
                .header("return-error", "true")
                .when()
                .get(API_ENDPOINT);

        String errorMessage = response.jsonPath().getString("error");

        Assert.assertEquals(response.statusCode(), 500);
        Assert.assertEquals(errorMessage, INTERNAL_SERVER_ERROR, "error message should be" + INTERNAL_SERVER_ERROR);
    }

    /**
     * ტესტის მიზანია რომ ნეგატიურ სცენარში გატესტოს ?gender= unknown
     * ვამოწმებთ, სტატუს კოდს და რესონსს, რომ ცარიელია
     */
    @Test
    public void testInvalidGender_Negative() {
        Response response = given()
                .queryParam("gender", INVALID_GENDER)
                .when()
                .get(API_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 422);

        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);
        Assert.assertTrue(users.isEmpty(), "users should be empty");
    }
}
