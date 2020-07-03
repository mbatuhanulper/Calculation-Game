package com.dtsetr.gamekitdemo.huawei.features.common;
public class Key {
    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmnfJuDHTVjMzT67ABmgMZXKm2y0Z1Ejrs9df0Yagk/5SecS79E+qwRQIDI75iHCRLH9/6lxtJXvv7kfAe+lfx1rgth0PKGPkWF6afntW0+mlO8xnQR1Hn2BFZrJ7PYnkbPRKYdwkxibl6Je9SPKfhC6rUDEXnVfT5l6zK7p6CScswqV5rIDc3bGoCRwjoaPhE0wvKICq8mXkSrSVHDwg7VQddf1OQxfjRSH2dZ3+ook50Pex4arL/PFwGgkukAY540KB5eEJl1ROA8Zm6iaXUkSLvCI5Tu7jtOI/J20lQSF+uedjTN/J093rzD3OleFf4qAvzQajyW1++4NrZXct6QIDAQAB";

    /**
     * get the publicKey of the application
     * During the encoding process, avoid storing the public key in clear text.
     *
     * @return
     */
    public static String getPublicKey() {
        return publicKey;
    }
}
