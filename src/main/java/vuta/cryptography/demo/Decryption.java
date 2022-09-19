package vuta.cryptography.demo;

import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Decryption {
    public static void main(String[] args) {
        try {
            // Đọc file chứa private key
            FileInputStream fis = new FileInputStream("src/main/java/data/privateKey.rsa");
            byte[] b = new byte[fis.available()];

            fis.read(b);
//            System.out.println("HELLO: ");
//            System.out.println(b);

            fis.close();


            // Tạo private key
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);
//            KeyFactory factory = KeyFactory.getInstance("RSA");
//            PrivateKey priKey = factory.generatePrivate(spec);

            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128); // The AES key size in number of bits
            SecretKey secKey = generator.generateKey();

            String plainText = "HelloWorld";
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();

            PublicKey puKey = keyPair.getPublic();
            PrivateKey prKey = keyPair.getPrivate();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.PUBLIC_KEY, puKey);
            byte[] encryptedKey = cipher.doFinal(secKey.getEncoded()/*Seceret Key From Step 1*/);

            cipher.init(Cipher.PRIVATE_KEY, prKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);

            //Convert bytes to AES SecertKey
            SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
            plainText = new String(bytePlainText);
            System.out.println("Chuỗi mã hoá : " + byteCipherText);
            System.out.println("Mã hoá chuỗi ban đầu : " + bytePlainText);
            System.out.println("Chuỗi ban đầu : " + plainText);
//            // Giải mã dữ liệu
//            Cipher c = Cipher.getInstance("RSA");
//            c.init(Cipher.DECRYPT_MODE, priKey);
//            String msg = "RR8WsVCiTUkm67vY8dSfv+eJ1h2JLEulXQZf4t7rxP8HynxMKrYcAmGvIYsrUb77ys4K8uUj48ayT3bSsM3wfnoJLtgww2idNB7r8UeIyIGe/UKoO0co5aJoptt8NwuKNCS0uf7fEEZnAfB1rszXqKQj0IxOdCtYLorO7DltwDM=";
//            byte[] decryptionOut = c.doFinal(msg.getBytes());
//            String outDecryp = Base64.getEncoder().encodeToString(decryptionOut);
//            System.out.println("Dữ liệu sau khi giải mã: " + outDecryp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
