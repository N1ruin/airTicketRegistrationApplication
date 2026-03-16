package sequrity;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordEncoder {
    public String encode(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean verify(String rawPassword, String encodedPassword) {
        var result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
        return result.verified;
    }
}
