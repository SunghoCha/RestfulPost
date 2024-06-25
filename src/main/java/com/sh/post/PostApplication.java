package com.sh.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
public class PostApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext ac = SpringApplication.run(PostApplication.class, args);
//		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
//		for (String beanDefinitionName : beanDefinitionNames) {
//			System.out.println("beanDefinitionName = " + beanDefinitionName);
//		}
	}

	@Bean // SessionLocaleResolver는 생소한 클래스
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

}
