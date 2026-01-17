package Data;

import org.testng.annotations.DataProvider;

public class DataProviders {
    @DataProvider(name = "DataValidationProvider")
    public static Object[][] dataValidationProvider() {
        return new Object[][] {
                {"Alice", 30, "female"},
                {"Bob", 25, "male"},
                {"Manuchari", 50, "male"},
                {"Lela", 45, "female"},
                {"Rostomi", 77, "male"},
                {"Sopio", 31, "female"},
                {"Gogi", 77, "male"},
        };
    }

    @DataProvider(name = "AgeDataProvider")
    public static Object[][] ageDataProvider() {
        return new Object[][] {
                {30, 1},
                {77, 2},
                {18, 0},
        };
    }

    @DataProvider(name = "GenderDataProvider")
    public static Object[][] genderDataProvider() {
        return new Object[][] {
                {"male", 4},
                {"female", 3},
        };
    }
}
