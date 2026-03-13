package com.memoria.orchestrator.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
public class VectorizationService {

    public String toVectorLiteral(String text) {
        String hash = DigestUtils.sha256Hex(text == null ? "" : text);
        int a = Integer.parseInt(hash.substring(0, 4), 16);
        int b = Integer.parseInt(hash.substring(4, 8), 16);
        int c = Integer.parseInt(hash.substring(8, 12), 16);
        int d = Integer.parseInt(hash.substring(12, 16), 16);
        return "[" + normalize(a) + "," + normalize(b) + "," + normalize(c) + "," + normalize(d) + "]";
    }

    private double normalize(int value) {
        return value / 65535.0;
    }
}
