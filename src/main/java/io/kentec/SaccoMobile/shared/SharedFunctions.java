package io.kentec.SaccoMobile.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SharedFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedFunctions.class);

    public static String encryptPassword(String password){
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }


    public static String formatPhoneNumber(String msisdn) {
        String start_char = String.valueOf(msisdn.charAt(0));

        int msisdn_length = msisdn.length();
//        LOGGER.info("Starting char" + start_char);
//        LOGGER.info("MSISDN Length" + msisdn_length);

        if (start_char.equals("+") && msisdn_length == 13) {
            msisdn = msisdn.substring(4);
        } else if (start_char.equals("2") && msisdn_length == 12) {
            msisdn = msisdn.substring(3);
        } else if (start_char.equals("0") && msisdn_length == 10) {
            msisdn = msisdn.substring(1);
        } else if (start_char.equals("7") && msisdn_length == 9) {
            msisdn = msisdn;
        } else {
            return "0";
        }
        return "254" + msisdn;
    }
}
