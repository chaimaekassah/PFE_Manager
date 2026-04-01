package org.pfemanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
    }

    // Hasher un mot de passe
    public static String hashPassword(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt(12));
    }

    // Vérifier un mot de passe
    public static boolean checkPassword(String motDePasse, String hash) {
        return BCrypt.checkpw(motDePasse, hash);
    }

    // Compatibilité temporaire avec ton ancien code
    public static String hasher(String motDePasse) {
        return hashPassword(motDePasse);
    }

    public static boolean verifier(String motDePasse, String hash) {
        return checkPassword(motDePasse, hash);
    }
}