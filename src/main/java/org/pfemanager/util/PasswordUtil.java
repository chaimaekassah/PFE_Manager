package org.pfemanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hasher un mot de passe (lors du register)
    public static String hasher(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt(12));
    }

    // Vérifier un mot de passe (lors du login)
    public static boolean verifier(String motDePasse, String hash) {
        return BCrypt.checkpw(motDePasse, hash);
    }
}