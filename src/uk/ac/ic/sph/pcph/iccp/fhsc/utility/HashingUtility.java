package uk.ac.ic.sph.pcph.iccp.fhsc.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtility {
	
	public String hashString(String password, String salt /*username used as salt for login password | first name used as salt for security answer*/)throws Exception
	{
		String stringToHash=password+salt;
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
	    md.update(stringToHash.getBytes());
	
	    byte byteData[] = md.digest();
	
	    //convert the byte to hex format method 1
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}

}
