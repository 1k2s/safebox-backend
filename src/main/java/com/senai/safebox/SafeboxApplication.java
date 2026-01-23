
package com.senai.safebox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@IntegrationComponentScan
@ComponentScan(basePackages = {
        "com.senai.safebox",
        "com.senai.safebox.lockerAPI.controller"
})
@SpringBootApplication
public class SafeboxApplication {

    private static final Logger log = LoggerFactory.getLogger(SafeboxApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(SafeboxApplication.class, args);

        log.info("========================================");
        log.info("  ✅ SISTEMA MQTT INICIADO COM SUCESSO!");
        log.info("  📡 MQTT conectado ao HiveMQ");
        log.info("  🌐 API REST disponível em:");
        log.info("     http://localhost:8080/api/box");
        log.info("========================================");
	}

}