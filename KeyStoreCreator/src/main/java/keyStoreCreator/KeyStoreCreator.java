package keyStoreCreator;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class KeyStoreCreator {

    public static KeyPair createKeyPair() {
        KeyPairGenerator kpg=null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    private static Certificate createCertificate(KeyPair keyPair, String cn) {
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        Security.addProvider(bouncyCastleProvider);

        Date startDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 1 Yr validity
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        X500Name dnName = new X500Name(cn);

        BigInteger certSerialNumber = new BigInteger(Long.toString(System.currentTimeMillis()));

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(dnName,
                certSerialNumber, startDate, endDate, dnName, subjectPublicKeyInfo);

        ContentSigner contentSigner = null;
        try {
            contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").setProvider(bouncyCastleProvider).build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        Certificate cert = null;
        try {
            cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return cert;
    }

    private static void createClientKeyStore(int id, PrivateKey privKey, Certificate clientCert, ArrayList<Certificate> serverCertList) {
        String pass = "DPASsecClient"+id;
        try {

            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(null, pass.toCharArray());



            //Create private key entry
            KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privKey ,new Certificate[] { clientCert });

            //Create a protection parameter used to protect the contents of the keystore
            KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection((pass+"PK").toCharArray());
            ks.setEntry("dpas-privateKey", privateKeyEntry, password);

            ks.setCertificateEntry("dpas-cert", clientCert);

            for (int i=0; i<serverCertList.size(); i++) {
                ks.setCertificateEntry("dpas-cert-server-"+(i+1), serverCertList.get(i));
            }


            FileOutputStream fos = new FileOutputStream("..\\client\\src\\main\\resources\\KeysUser" + id + ".jks");
            ks.store(fos, pass.toCharArray());
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
            e.printStackTrace();
        }
    }


    private static void createServerKeyStore(int id, PrivateKey privKey, Certificate serverCert) {
        String pass = "DPASsecServer"+id;
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(null, pass.toCharArray());

            //Create private key entry
            KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privKey ,new Certificate[] { serverCert });

            //Create a protection parameter used to protect the contents of the keystore
            KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection((pass+"PK").toCharArray());
            ks.setEntry("dpas-privateKey", privateKeyEntry, password);
            ks.setCertificateEntry("dpas-cert-server", serverCert);


            FileOutputStream fos = new FileOutputStream("..\\server\\src\\main\\resources\\KeysServer" + id + ".jks");
            ks.store(fos, pass.toCharArray());
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        ArrayList<Certificate> serverCertList = new ArrayList<>();
        KeyPair serverKeyPair;
        Certificate serverCert;
        for (int id=1; id<=3; id++){
            serverKeyPair = createKeyPair();
            serverCert = createCertificate(serverKeyPair, "CN=Server"+id+"Cert");
            serverCertList.add(serverCert);
            createServerKeyStore(id, serverKeyPair.getPrivate(), serverCert);
        }

        KeyPair clientKeyPair;
        Certificate clientCert;
        for (int id=1; id<=3; id++) {
            clientKeyPair = createKeyPair();
            clientCert = createCertificate(clientKeyPair, "CN=Client"+id+"Cert");
            createClientKeyStore(id, clientKeyPair.getPrivate(), clientCert, serverCertList);
        }
    }
}
