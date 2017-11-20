package br.org.roger.files.process.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class Crypto {
	
	private static final Logger LOGGER = Logger.getLogger(Crypto.class.getName());
	private static final String AES_KEY = "super-secret-key";
	
	private Cipher aes;
	private boolean encryptEnabled;
	
	public Crypto(final boolean encryptEnabled) {
		this.encryptEnabled = encryptEnabled;
		if (this.encryptEnabled) {
			SecretKey secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
			try {
				this.aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
				this.aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
				LOGGER.error("It was not possible to initialize encrypt engine");
				throw new CryptoInitializationException("It was not possible to initialize encrypt engine", e);
			}
		}
	}
	
	public Optional<String> encrypt(final String flatText) {
		if (!this.encryptEnabled) {
			LOGGER.error("It's not allowed to call encrypt method with encrypt disabled");
			throw new IllegalStateException("It's not allowed to call encrypt method with encrypt disabled");
		}
		try {
			
			byte[] ciphertext = this.aes.doFinal(flatText.getBytes());

			String encrypted = Base64.getEncoder().encodeToString(ciphertext);
			return Optional.of(encrypted);
		} catch (Exception e) {
			LOGGER.error("Error encrypting Json payload", e);
			return Optional.empty();
		}
	}

}
