package eu.asterics.mw.are.parsers;

import eu.asterics.mw.are.exceptions.ParseException;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: Nearchos Paspallis
 * Date: 1/5/11
 * Time: 2:42 PM
 */
public class TestDeploymentModelParser extends TestCase
{
    public void testParser()
            throws FileNotFoundException, ParseException
    {
        final InputStream inputStream = new FileInputStream("middleware\\src\\test\\resources\\models\\test_deployment_model.xml");
        DefaultDeploymentModelParser.instance.parseModel(inputStream);
        System.out.println("test");
    }
}