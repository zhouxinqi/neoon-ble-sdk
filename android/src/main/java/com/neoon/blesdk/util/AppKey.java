package com.neoon.blesdk.util;

import android.content.Context;
import android.util.Base64;

import com.neoon.blesdk.encapsulation.ble.SNBLESDK;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 作者:东芝(2018/7/13).
 * 功能:这里的代码可能故意写的很烂(绕),只有代码烂(绕)才能混淆反编译者
 */

public class AppKey {
    private final Md5_strClass md5_strClass = new Md5_strClass();
    private final Head_byteClass head_byteClass = new Head_byteClass();
    private String head;
    private String md5;
    public boolean mustAuthorized;

    public boolean isAuthorized(Context context, String appKey) {
        try {
            final B64 b64 = new B64();
            head = b64.key();
            md5 = head + b64.getPackageName(context);
            Jiami jiami = new Jiami();
            for (int i = 0; i < md5_strClass.md5_str[1] - head_byteClass.head_byte[5]; i++) {
                md5 = jiami.mmmddd555(md5);
            }
            return mustAuthorized = md5_strClass.eq(head_byteClass.toSub(md5).equals(appKey));
        } catch (Exception e) {
            e.printStackTrace();
            mustAuthorized = false;
        }
        return false;
    }
}

class Md5_strClass {
    byte[] md5_str = {77, 68, 53};
    protected boolean eq(boolean equals) {
        if (!equals) {
            SNBLESDK.close();
            return false;
        } else {
            return true;
        }
    }
}

class Jiami {
    protected String mmmddd555(  String md5) throws Exception {
        MD5MessageDigest.getInstance().update(md5.getBytes());
        Head_byteClass head_byteClass = new Head_byteClass();
        String new_md5 = new BigInteger(1, MD5MessageDigest.getInstance().digest()).toString(head_byteClass.head_byte[2] - head_byteClass.head_byte[11]);
        MD5MessageDigest.getInstance().reset();
        return new_md5;
    }
}

class Head_byteClass {
    byte[] head_byte = {98, 86, 118, 51, 98, 50, 53, 102, 49, 99, 50, 82, 114};

    String toSub(String md5) {
        return md5.substring(md5.length() - 10, md5.length()) + md5.substring(0, 10);
    }
}

class B64 {
    public String key() {
        return Base64.encodeToString(new Head_byteClass().head_byte, Base64.NO_WRAP);
    }

    public String getPackageName(Context context) {
        return context.getPackageName();
    }
}

class MD5MessageDigest {
    private static MessageDigest instance;

    public static MessageDigest getInstance() throws Exception {
        if (instance == null) {
            instance = MessageDigest.getInstance(new String(new Md5_strClass().md5_str));
        }
        return instance;
    }

}