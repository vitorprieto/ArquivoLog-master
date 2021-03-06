package br.com.robertWalters.ArquivoLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

@SpringBootApplication
@ComponentScan(basePackages = { "br.com.robertWalters.ArquivoLog" })
@EntityScan(basePackages = { "br.com.robertWalters.ArquivoLog.model" })
@EnableJpaRepositories(basePackages = { "br.com.robertWalters.ArquivoLog.repository" })
public class ArquivoLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArquivoLogApplication.class, args);
	}

}
