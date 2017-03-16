package fhscServices;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ic.sph.pcph.iccp.fhsc.utility.HashingUtility;

public class testGeneratepassword {

	@Test
	public void testGeneratepassword() {
		
		try {
			String result=new HashingUtility().hashString("christophe", "cs1");
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1,1);
	}

}
