package eu.asterics.mw.are.parsers;

import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.bundle.IComponentType;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

/**
 * User: Nearchos Paspallis
 * Date: 1/5/11
 * Time: 2:24 PM
 */
public class TestBundleModelParser extends TestCase
{
    public void testParser()
            throws FileNotFoundException, ParseException
    {
        final InputStream inputStream = new FileInputStream("middleware\\src\\test\\resources\\models\\test_bundle_descriptor.xml");
        final Set<IComponentType> allComponentTypes = DefaultBundleModelParser.instance.parseModel(inputStream);
        System.out.println("allComponentTypes: " + allComponentTypes);
    }
}
