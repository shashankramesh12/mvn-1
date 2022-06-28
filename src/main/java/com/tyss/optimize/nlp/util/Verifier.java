package com.tyss.optimize.nlp.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class Verifier  implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
