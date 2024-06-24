package com.sh.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PostApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext ac = SpringApplication.run(PostApplication.class, args);
//		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
//		for (String beanDefinitionName : beanDefinitionNames) {
//			System.out.println("beanDefinitionName = " + beanDefinitionName);
//		}
	}

}
