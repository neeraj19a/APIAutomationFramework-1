package utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class TestListeners extends APIBase implements ITestListener {

    public TestListeners(String uri, String method) {
        super(uri, method);
    }

    public void onTestStart(ITestResult result) {

        // create node in middle of the report for each test method(test case) with that
        // test case method name
        ExtentTest test = classLevelLog.get().createNode(result.getName());
        testLevelLog.set(test);

        // log info in report: that test case has been started in the report in that
        // node starting
        testLevelLog.get().log(Status.INFO,
                "<b>" + " Execution of Test Case:- " + result.getName() + " started" + "</b>");

    }

    public void onTestSuccess(ITestResult result) {
        //Print sucessfull message with bold and green color background
        String successMessage = "<b>" + "This Test Case is Passed" + "</b>";
        Markup m = MarkupHelper.createLabel(successMessage, ExtentColor.GREEN);
        testLevelLog.get().pass(m);

    }

    public void onTestFailure(ITestResult result) {

        //Get the exception of the failure method and store in exceptionMessage variable
        String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());

        // click icon to see exception details
        testLevelLog.get()
                .fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Exception Occured:Click to see"
                        + "</font>" + "</b >" + "</summary>" + exceptionMessage.replaceAll(",", "<br>") + "</details>"
                        + " \n");

        // Print failure message in red color background
        String failureLogg = "This Test case got Failed";
        Markup m = MarkupHelper.createLabel(failureLogg, ExtentColor.RED);
        testLevelLog.get().log(Status.FAIL, m);

    }

    public void onTestSkipped(ITestResult result) {

        //Print skipped message
        testLevelLog.get().skip("</b >" + "This test Case got Skipped" + "</b >");

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    public void onStart(ITestContext context) {

    }

    public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub

    }

}
