package org.pfemanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class GenererHash {
    public static void main(String[] args) {
        String motDePasse = "Test123";
        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt(12));
        System.out.println("Hash pour 1234 : " + hash);
    }
}
