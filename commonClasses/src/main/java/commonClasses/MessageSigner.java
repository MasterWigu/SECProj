package commonClasses;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.*;
import java.util.Arrays;

public class MessageSigner {

    private static class AnnToSign implements Serializable {
        private char[] message;
        private PublicKey creatorPk;
        private int board;
        private Announcement[] reffs;

        private AnnToSign(char[] msg, PublicKey cpk, int b, Announcement[] anns) {
            message = msg;
            creatorPk = cpk;
            board = b;
            reffs = anns;
        }
    }

    public static boolean verify(Announcement p) {
        PublicKey pk = p.getCreator().getPk();

        byte[] signature = p.getSignature();

        AnnToSign ann = new AnnToSign(p.getMessage(), p.getCreator().getPk(), p.getBoard(), p.getReffs());
        byte[] hash = getHash(ann);

        //TODO retirar
        if (pk == null) {
            System.out.println("DEBUG: pk is null");
            return Arrays.equals(hash, signature);
        }

        byte[] messageHash = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            messageHash = cipher.doFinal(signature);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Invalid public key!");
        }

        return Arrays.equals(messageHash, hash);
    }

    public static byte[] sign(char[] msg, PublicKey cpk, int b, Announcement[] anns, PrivateKey pk) {

        AnnToSign ann = new AnnToSign(msg, cpk, b, anns);
        byte[] hash = getHash(ann);

        if (pk == null) {
            return hash;
        }

        byte[] signature = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            signature = cipher.doFinal(hash);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Invalid private key!");
        }

        return signature;
    }

    private static byte[] getHash(AnnToSign a) {

        //Create signature
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] hash = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(a);
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
