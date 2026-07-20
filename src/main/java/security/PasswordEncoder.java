package security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordEncoder {
    private static final int HASH_ITERATIONS = 12;

    public String encode(String password) {
        return BCrypt.withDefaults()
                .hashToString(HASH_ITERATIONS, password.toCharArray());
    }

    public boolean verify(String rawPassword, String encodedPassword) {
        return BCrypt.verifyer()
                .verify(rawPassword.toCharArray(), encodedPassword)
                .verified;
    }
}
