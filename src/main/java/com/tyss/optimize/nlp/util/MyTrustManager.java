package com.tyss.optimize.nlp.util;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

public class MyTrustManager implements X509TrustManager {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[]      paramArrayOfX509Certificate, String paramString)
            throws CertificateException {
        // do nothing

    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString)
            throws CertificateException {
        // do nothing
    }
}
