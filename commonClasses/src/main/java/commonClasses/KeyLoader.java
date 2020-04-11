package commonClasses;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyLoader {
    private static KeyStore getKeystore(String path, String password ) throws KeyException {

        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(new FileInputStream(path+".jks"), password.toCharArray());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new KeyException(e.getMessage());
        }

        return keyStore;
    }


    public static PrivateKey getPrivateKey(String path, String password) throws KeyException {
        //Creating the KeyStore object
        KeyStore keyStore = getKeystore(path, password);

        try {
            return (PrivateKey) keyStore.getKey("dpas-privateKey", (password+"PK").toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new KeyException(e.getMessage());
        }
    }

    public static PublicKey getPublicKey(String path, String password) throws KeyException {
        //Creating the KeyStore object
        KeyStore keyStore = getKeystore(path, password);
        Certificate cert;

        try {
            cert = keyStore.getCertificate("dpas-cert");
        } catch (KeyStoreException e) {
            throw new KeyException();
        }
        return cert.getPublicKey();
    }

    public static PublicKey getServerPublicKey(String path, String password) throws KeyException {
        //Creating the KeyStore object
        KeyStore keyStore = getKeystore(path, password);
        Certificate cert;

        try {
            cert = keyStore.getCertificate("dpas-cert-server");
        } catch (KeyStoreException e) {
            throw new KeyException();
        }
        return cert.getPublicKey();
    }
}
