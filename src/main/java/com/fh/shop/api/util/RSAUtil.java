package com.fh.shop.api.util;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.io.UnsupportedEncodingException;

public class RSAUtil {

    public static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKSM40r5Z2kHnfwcGugnwbDO6s0lDee7BLMAFwjkwxl8zzsqKzIa/FQz2a+gM62XpCvwR1KqvpEc1QlKVC4inq+WrlsbLzJC05a1Y/obng8ofTCSR1pSOV1rjFORG7Yd8ATcxT4QodV53i80YupOREcXuXan7tB+ZWhKvCc+xs/wIDAQAB";
    public static final String PRIVATEKEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAIpIzjSvlnaQed/Bwa6CfBsM7qzSUN57sEswAXCOTDGXzPOyorMhr8VDPZr6AzrZekK/BHUqq+kRzVCUpULiKer5auWxsvMkLTlrVj+hueDyh9MJJHWlI5XWuMU5Ebth3wBNzFPhCh1XneLzRi6k5ERxe5dqfu0H5laEq8Jz7Gz/AgMBAAECgYBqdPQygT6vVjhM9yf5BcEvPfx8lYjmToiAyZtCIHot77Q7pDk2+GcN0N8bcP+NjaFewxlWbhxcWHUcZOL5I42qkm9zwYpYCeCOAlL6Ksqw8DzJuNgxXV/JQLlidBBSkGWxFUtT+6oy5tAqa8FLbknfpQAY9BRUMpUPMb8FEbdzMQJBAMZzD7l0cSWymTIxvbIJRxoJ4S90v7Cuz6eLrd+/K9O9Ilkhk2mApIP/aXDP316ywj9TgfoYWVLF2SRcvOcOo6cCQQCyYxMPoT7WkZoF+zKIsqrekSUiqqQH0q0QtA86avAzoCMJ6O11WWM594mzhPE1eRFlVFhAw6dQZBJmxE8N0fbpAkEApdyPPOqUkvRl1Cz8pivY8GtneVlzyYEh75/vz/0g7VS7u5Ezes9ZMTWs8+vZfnWbon/yN4xYQ7U22oxkGh4DKwJBAJhkgR0iIB+H5W/kp8gV8lkO4A2cf2LbfNO/Lx81yhwgWq6VmX5draIQsUd2c+W9lrnc80PooV/RDdNTM0HYNDkCQQCd1D5VMVgeCntox36fZ2DE0JCBAArsMlRcC0Zs2A5my3sk43hQ888FTWLNEXfEUcpRIEIrl2F3hymhmoEwCSuk";

    public static String decrypt(String data) {
        RSA rsa = new RSA(PRIVATEKEY, PUBLICKEY);
        byte[] bytes = rsa.decryptFromBase64(data, KeyType.PrivateKey);
        try {
            String result = new String(bytes, "utf-8");
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
