package com.makeprojects.ewallet.useraccounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.makeprojects.ewallet.transactions.database.repository",
        "com.makeprojects.ewallet.useraccounts.database.repository"})
@EntityScan(basePackages = "com.makeprojects.ewallet.shared.database.model")
@ComponentScan(basePackages = {
        "com.makeprojects.ewallet.useraccounts",
        "com.makeprojects.ewallet.shared"
})
//@Import(KafkaConfig.class)
//@EnableKafka
public class UserAccountsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAccountsApplication.class, args);
    }
}
