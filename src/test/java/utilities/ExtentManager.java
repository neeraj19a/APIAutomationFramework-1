package utilities;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import static config.ConfigurationManager.configuration;


public class ExtentManager extends APIBase {

    private static com.aventstack.extentreports.ExtentReports extent;
    private static ExtentHtmlReporter htmlReporter;

    public ExtentManager(String uri, String method) {
        super(uri, method);
    }

    public static com.aventstack.extentreports.ExtentReports GetExtent(String filePath) {
        if (extent != null) {
            return extent;
        } else {
            extent = new ExtentReports();
            extent.attachReporter(ExtentManager.getHtmlReporter(filePath));
            extent.setAnalysisStrategy(AnalysisStrategy.CLASS);
            return extent;
        }
    }

    public static ExtentHtmlReporter getHtmlReporter(String filePath) {
        htmlReporter = new ExtentHtmlReporter(filePath);
        htmlReporter.loadXMLConfig(configuration().getextentconfig());
        return htmlReporter;
    }
}