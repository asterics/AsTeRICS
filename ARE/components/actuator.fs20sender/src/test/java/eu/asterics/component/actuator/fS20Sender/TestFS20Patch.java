package eu.asterics.component.actuator.fS20Sender;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFS20Patch {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			FS20Utils.copyBatchFromJar(FS20Utils.FILENAME_REGPATCH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail();
		}
	}

}
