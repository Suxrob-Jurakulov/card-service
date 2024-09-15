package org.company.utils;

import org.company.domain.Cards;
import org.company.exp.BadRequestException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ETagGenerator {
    public static String generateETag(Cards card) {
        try {
            String input = card.getCardId() + card.getStatusId() + card.getUpdatedAt().toString();

            // MD5 hashing
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, hash);

            StringBuilder hashtext = new StringBuilder(no.toString(16));

            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
