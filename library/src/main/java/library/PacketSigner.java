package library;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.Arrays;

import static java.lang.Math.abs;

public class PacketSigner {
    public static boolean verify(Packet p) {
        PublicKey pk = p.getKey();
        long currTime = System.currentTimeMillis();
        if (abs(currTime - p.getTimestamp()) > 5000) {
            return false;
        }

        byte[] signature = p.getSign();

        p.setSign(null);
        byte[] hash = getHash(p);
        byte[] messageHash = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            messageHash = cipher.doFinal(signature);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Crypto error verifying packet");
        } catch (InvalidKeyException e) {
            System.out.println("Invalid private key!");
        }

        return Arrays.equals(messageHash, hash);
    }

    public static Packet sign(Packet p, PrivateKey pk) {
        //Insert timestamp
        p.setTimestamp(System.currentTimeMillis());

        byte[] hash = getHash(p);
        byte[] signature = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            signature = cipher.doFinal(hash);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Crypto error signing packet");
        } catch (InvalidKeyException e) {
            System.out.println("Invalid private key!");
        }
        p.setSign(signature);

        return p;
    }

    private static byte[] getHash(Packet p) {

        //Create signature
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] hash = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(p);
            out.flush();
            byte[] packetBytes = bos.toByteArray();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(packetBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return hash;
    }


}
