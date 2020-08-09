package com.fh.shop.api;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

public class TestRSA {

    @Test
    public void test1() {
        RSA rsa = new RSA();
        String privateKeyBase64 = rsa.getPrivateKeyBase64(); // 私钥
        String publicKeyBase64 = rsa.getPublicKeyBase64(); // 公钥

        System.out.println(publicKeyBase64);
        System.out.println(privateKeyBase64);
    }

    @Test
    public void test2() throws UnsupportedEncodingException {

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKSM40r5Z2kHnfwcGugnwbDO6s0lDee7BLMAFwjkwxl8zzsqKzIa/FQz2a+gM62XpCvwR1KqvpEc1QlKVC4inq+WrlsbLzJC05a1Y/obng8ofTCSR1pSOV1rjFORG7Yd8ATcxT4QodV53i80YupOREcXuXan7tB+ZWhKvCc+xs/wIDAQAB";
        String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAIpIzjSvlnaQed/Bwa6CfBsM7qzSUN57sEswAXCOTDGXzPOyorMhr8VDPZr6AzrZekK/BHUqq+kRzVCUpULiKer5auWxsvMkLTlrVj+hueDyh9MJJHWlI5XWuMU5Ebth3wBNzFPhCh1XneLzRi6k5ERxe5dqfu0H5laEq8Jz7Gz/AgMBAAECgYBqdPQygT6vVjhM9yf5BcEvPfx8lYjmToiAyZtCIHot77Q7pDk2+GcN0N8bcP+NjaFewxlWbhxcWHUcZOL5I42qkm9zwYpYCeCOAlL6Ksqw8DzJuNgxXV/JQLlidBBSkGWxFUtT+6oy5tAqa8FLbknfpQAY9BRUMpUPMb8FEbdzMQJBAMZzD7l0cSWymTIxvbIJRxoJ4S90v7Cuz6eLrd+/K9O9Ilkhk2mApIP/aXDP316ywj9TgfoYWVLF2SRcvOcOo6cCQQCyYxMPoT7WkZoF+zKIsqrekSUiqqQH0q0QtA86avAzoCMJ6O11WWM594mzhPE1eRFlVFhAw6dQZBJmxE8N0fbpAkEApdyPPOqUkvRl1Cz8pivY8GtneVlzyYEh75/vz/0g7VS7u5Ezes9ZMTWs8+vZfnWbon/yN4xYQ7U22oxkGh4DKwJBAJhkgR0iIB+H5W/kp8gV8lkO4A2cf2LbfNO/Lx81yhwgWq6VmX5draIQsUd2c+W9lrnc80PooV/RDdNTM0HYNDkCQQCd1D5VMVgeCntox36fZ2DE0JCBAArsMlRcC0Zs2A5my3sk43hQ888FTWLNEXfEUcpRIEIrl2F3hymhmoEwCSuk";

        RSA rsa = new RSA(privateKey, publicKey);
        String result = rsa.encryptBase64("zhangsan", KeyType.PublicKey);
        System.out.println("加密后:"+result);
        String pwd = rsa.encryptBase64("123", KeyType.PublicKey);
        System.out.println("加密后:"+pwd);
//        byte[] res = rsa.decryptFromBase64(result, KeyType.PrivateKey);
//        String info = new String(res, "utf-8");
//        System.out.println("解密后:"+info);
    }
}
