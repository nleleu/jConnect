package tests;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;

import com.jconnect.message.content.PingContentMessage;
import com.jconnect.security.CryptionUtil;


public class test2 {

	public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException {
		Key k = CryptionUtil.generateKey();
		System.out.println(k);
		String t= "";//message de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\nmessage de test � encoder puis d�coder pour l'exemple\n";
		String te = CryptionUtil.encrypt(k, t);
		System.out.println(te);
		
		Key k1 = CryptionUtil.generateKey();
		String tf = CryptionUtil.decrypt(k, te);
		System.out.println("t:"+tf);
		
		//System.out.println(new PingContentMessage());
		
		

	}
}
