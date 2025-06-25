package user.password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHasher {
    private final int iterations;
    private final int keyLength;
    private final String algorithm;
    private final int saltLength;

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16;

    public PasswordHasher(int iterations, int keyLength, String algorithm, int saltLength) {
        this.iterations = iterations;
        this.keyLength = keyLength;
        this.algorithm = algorithm;
        this.saltLength = saltLength;
    }

    public PasswordHasher() {
        this(ITERATIONS, KEY_LENGTH, ALGORITHM, SALT_LENGTH);
    }

    public String hash(String password) {
        byte[] salt = generateSalt();
        return hashWithSalt(password, salt);
    }

    public boolean verify(String enteredPassword, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid hash format");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String newHash = hashWithSalt(enteredPassword, salt);

        return storedHash.equals(newHash);
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
        return salt;
    }

    private String hashWithSalt(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}

