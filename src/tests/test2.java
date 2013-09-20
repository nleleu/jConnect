package tests;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;

import com.jconnect.core.security.CryptionUtil;
import com.jconnect.impl.message.PingContentMessage;


public class test2 {

	public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException {
		Key k = CryptionUtil.generateKey();
		System.out.println(k);
		String t= "";//message de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\nmessage de test à encoder puis décoder pour l'exemple\n";
		String te = CryptionUtil.encrypt(k, t);
		System.out.println(te);
		
		Key k1 = CryptionUtil.generateKey();
		String tf = CryptionUtil.decrypt(k, te);
		System.out.println("t:"+tf);
		
		//System.out.println(new PingContentMessage());
		
		

	}
}
