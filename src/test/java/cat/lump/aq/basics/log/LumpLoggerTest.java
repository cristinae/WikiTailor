package cat.lump.aq.basics.log;

import org.junit.Before;
import org.junit.Test;

public class LumpLoggerTest {

	private LumpLogger logger;
	
	@Before
	public void setUp() throws Exception {
		logger = new LumpLogger( LumpLogger.class.getName() );	
	}

	@Test
	public void testInfo() {
		logger.info("This is an INFO message");		
	}

	@Test
	public void testWarn() {
		logger.warn("This is a WARNING message");		
	}

	@Test
	public void testError() {
		logger.error("This is an ERROR message");
	}
}
