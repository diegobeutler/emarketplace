package br.edu.utfpr.emarketplace.service.utils;

public class ImageUtils {
    public static byte[] converterDataToByte(String data){
        return java.util.Base64.getDecoder().decode(data.substring(data.indexOf(",") + 1));
    }
}
