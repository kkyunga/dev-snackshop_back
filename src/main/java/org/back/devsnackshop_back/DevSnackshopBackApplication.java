package org.back.devsnackshop_back;

import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.jwt.JwtGenerate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DevSnackshopBackApplication {

    public static void main(String[] args) {
        JwtGenerate jwt = new JwtGenerate();
        log.info( "jwt::::::>>>"+jwt.encryptionKey());

        SpringApplication.run(DevSnackshopBackApplication.class, args);
    }

}
