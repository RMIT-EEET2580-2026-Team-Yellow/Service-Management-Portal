package com.fixinh.smt.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Loads the RSA KeyPair from the shared ASE project keystore for RS256 JWT signing/verification
 * (SRS 3.2.M1-M3) — same mechanism SBT/SBP uses (their KeyStoreManager/JwtService), swapped in to
 * replace the old HMAC-secret approach. A keystore holds a private key (only this backend can
 * sign tokens) paired with a certificate/public key (used to verify), rather than one shared
 * symmetric secret that anyone holding it could also forge tokens with.
 */
@Component
public class KeyStoreManager {

    private final KeyStore keyStore;
    private final String keyAlias;
    private final char[] password;

    public KeyStoreManager(
            @Value("${smt.jwt.keystore-path:classpath:ase_project.keystore}") String keystorePath,
            @Value("${smt.jwt.keystore-password:aseproject}") String keystorePassword) {
        this.password = keystorePassword.toCharArray();
        try {
            this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream tempStream;
            if (keystorePath.startsWith("classpath:")) {
                String location = keystorePath.substring("classpath:".length());
                tempStream = getClass().getClassLoader().getResourceAsStream(location);
                if (tempStream == null) {
                    File file = ResourceUtils.getFile(keystorePath);
                    tempStream = new FileInputStream(file);
                }
            } else {
                File file = new File(keystorePath);
                tempStream = new FileInputStream(file);
            }

            final InputStream inputStream = tempStream;
            try (inputStream) {
                this.keyStore.load(inputStream, this.password);
            }

            this.keyAlias = this.keyStore.aliases().nextElement();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load JWT KeyStore from path: " + keystorePath, e);
        }
    }

    public PublicKey getPublicKey() {
        try {
            Certificate certificate = keyStore.getCertificate(keyAlias);
            return certificate.getPublicKey();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to retrieve public key from KeyStore", ex);
        }
    }

    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(keyAlias, password);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to retrieve private key from KeyStore", ex);
        }
    }
}
