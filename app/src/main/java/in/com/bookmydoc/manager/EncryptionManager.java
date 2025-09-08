package in.com.bookmydoc.manager;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionManager {

    private static final String KEY_ALIAS = "ProfileKey";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding"; // More specific transformation

    private KeyStore keyStore;

    // This constructor initializes the Keystore and creates a key if needed.
    public EncryptionManager() throws Exception {
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        createKeyIfNeeded();
    }

    private void createKeyIfNeeded() throws Exception {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(keySpec);
            keyGenerator.generateKey();
        }
    }

    private SecretKey getSecretKey() throws Exception {
        return ((SecretKey) keyStore.getKey(KEY_ALIAS, null));
    }

    // CORRECTED: Made this a non-static instance method
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        byte[] iv = cipher.getIV(); // GCM mode generates a unique IV for each encryption
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Prepend the IV to the encrypted data for decryption
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);
        byte[] cipherMessage = byteBuffer.array();

        return Base64.encodeToString(cipherMessage, Base64.DEFAULT);
    }

    public String decrypt(String encrypted) throws Exception {
        byte[] cipherMessage = Base64.decode(encrypted, Base64.NO_WRAP);

        // Extract the IV from the beginning of the cipher message
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        byte[] iv = new byte[12]; // GCM standard IV size is 12 bytes
        byteBuffer.get(iv);

        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128 is the tag length
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

        byte[] decodedBytes = cipher.doFinal(cipherBytes);

        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}