package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.ttv.facerecog.R;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class EncryptActivity extends AppCompatActivity {

    public static String encrypt(String value) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted,Base64.DEFAULT);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String value) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(value,Base64.DEFAULT));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }




        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        //String mytext = "HELLOWORLD" ;
        String mytext = "BTECH"+"CSE"+"2"+"4"+"A"+"26032023"+"80009000"+"CS251"+"421243"+"RANDOM_SALT" ;
        //Toast.makeText(this, /*"ORIGINAL : " +*/ mytext, Toast.LENGTH_SHORT).show();
        String encryptedText = encrypt(mytext);
       // Toast.makeText(this, /*"Encrypted : " +*/ encryptedText, Toast.LENGTH_SHORT).show();
        String decryptedText = decrypt(encryptedText);
       // Toast.makeText(this, /*"Decrypted : " +*/ decryptedText, Toast.LENGTH_SHORT).show();
        TextView original,encrypted,decrypted;

    }
}