package com.jconnect.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptionUtil {

	public static String encrypt(Key key, String content)
			throws InvalidKeyException {

		Cipher aes;
		try {
			aes = Cipher.getInstance("AES/ECB/PKCS5Padding");

			aes.init(Cipher.ENCRYPT_MODE, key);
			byte[] ciphertext = aes.doFinal(content.getBytes());
			return Base64.encodeBytes(ciphertext, Base64.NO_OPTIONS);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String decrypt(Key key, String data)
			throws InvalidKeyException {

		Cipher aes;
		try {
			byte[] mdata = Base64.decode(data, Base64.NO_OPTIONS);
			aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, key);
			String cleartext = new String(aes.doFinal(mdata));
			return cleartext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Key generateKey() {
		Random r = new Random();
		byte[] salt = ("jConnect" + r.nextDouble() + System.currentTimeMillis())
				.getBytes();
		int iterations = 10000;
		try {
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			SecretKey tmp;

			tmp = factory.generateSecret(new PBEKeySpec(("jConnect"
					+ r.nextDouble() + System.currentTimeMillis() * 5)
					.toCharArray(), salt, iterations, 128));
			SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");
			return key;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}
	// Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
	// aes.init(Cipher.ENCRYPT_MODE, key);
	// byte[] ciphertext = aes.doFinal("my cleartext".getBytes());
	//
	// aes.init(Cipher.DECRYPT_MODE, key);
	// String cleartext = new String(aes.doFinal(ciphertext));
	// The key for the cipher should be an instance of
	// javax.crypto.spec.SecretKeySpec. AES in particular requires its key to be
	// created with exactly 128 bits (16 bytes).
	//
	// A simple way to get the required number of bytes is to take a variable
	// length passphrase and hash it with a java.security.MessageDigest such as
	// SHA1. For example:
	//
	// import java.security.*;
	// import javax.crypto.spec.*;
	//
	// String passphrase = "correct horse battery staple";
	// MessageDigest digest = MessageDigest.getInstance("SHA");
	// digest.update(passphrase.getBytes());
	// SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
	// A better way to create a key is with a SecretKeyFactory using a salt:
	//
	// byte[] salt = "choose a better salt".getBytes();
	// int iterations = 10000;
	// SecretKeyFactory factory =
	// SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	// SecretKeySpec tmp = generateSecret(new
	// PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
	// SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");

}
