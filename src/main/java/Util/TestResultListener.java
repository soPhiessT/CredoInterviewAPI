package Util;

import Data.DatabaseManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {

    private final DatabaseManager databaseManager;

    public TestResultListener() {
        this.databaseManager = DatabaseManager.getInstance();
        System.out.println("TestResultListener initialized");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        try {
            databaseManager.saveTestResult(testName, "PASSED");
        } catch (Exception e) {
            System.out.format("Failed to save test result for %s: %s", testName, e.getMessage());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();
        System.out.format("Test FAILED: %s - Reason: %s",
                    testName,
                    throwable != null ? throwable.getMessage() : "Unknown");

        try {
            databaseManager.saveTestResult(testName, "FAILED");
            System.out.format("Successfully saved FAILED result for test: %s", testName);
        } catch (Exception e) {
            System.out.format("Failed to save test result for %s: %s", testName, e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        try {
            databaseManager.saveTestResult(testName, "SKIPPED");
            System.out.format("Successfully saved SKIPPED result for test: %s", testName);
        } catch (Exception e) {
            System.out.format("Failed to save test result for %s: %s", testName, e.getMessage());
        }
    }


    private String getTestName(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder(methodName);
            sb.append("_");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) sb.append("_");
                sb.append(params[i] != null ? params[i].toString() : "null");
            }
            return sb.toString();
        }
        return methodName;
    }

    @Override
    public void onFinish(ITestContext context) {
        databaseManager.printResults();
    }
}
