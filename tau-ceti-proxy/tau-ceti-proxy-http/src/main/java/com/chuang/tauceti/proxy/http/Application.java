package com.chuang.tauceti.proxy.http;

import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        int port = 4433;
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                logger.warn("get port arg error", e);
            }
        }

        DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(false).withPort(port).start();

    }


}
