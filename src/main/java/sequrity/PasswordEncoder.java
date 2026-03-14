package sequrity;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordEncoder {
    public String encode(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
