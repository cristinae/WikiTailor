package cat.lump.aq.basics.check;

import org.junit.Test;

public class CHKTest {

	@Test
	public void testCHECKBoolean() {
		CHK.CHECK(true);		
	}

	@Test
	public void testCHECKBooleanString() {
		CHK.CHECK(true, "No message should appear");
	}

	@Test
	public void testCHECK_NOT_NULL() {
		int i = 1;
		CHK.CHECK_NOT_NULL(i);
	}

}
