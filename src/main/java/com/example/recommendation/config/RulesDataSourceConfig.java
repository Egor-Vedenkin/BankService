package com.example.recommendation.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
// Исключаем основной пакет контроллеров и сервисов, оставляя только rules.db
@EnableJpaRepositories(
        basePackages = "com.example.recommendation.rules.db",
        entityManagerFactoryRef = "rulesEntityManager",
        transactionManagerRef = "rulesTransactionManager"
)
public class RulesDataSourceConfig {

    @Bean(name = "rulesDataSourceProperties")
    @ConfigurationProperties(prefix = "rules.datasource")
    public DataSourceProperties rulesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "rulesDataSource")
    public DataSource rulesDataSource(@Qualifier("rulesDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "rulesEntityManager")
    public LocalContainerEntityManagerFactoryBean rulesEntityManager(
            @Qualifier("rulesDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.recommendation.rules.db");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTransactionManager(
            @Qualifier("rulesEntityManager") LocalContainerEntityManagerFactoryBean emf) {

        return new JpaTransactionManager(emf.getObject());
    }
}