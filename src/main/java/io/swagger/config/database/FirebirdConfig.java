package io.swagger.config.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "firebirdEntityManagerFactory",
        transactionManagerRef = "firebirdTransactionManager",
        basePackages = { "io.swagger.firebird.repository" }
)
public class FirebirdConfig {

    @Bean(name = "firebirdDataSource")
    @ConfigurationProperties(prefix = "firebird.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "firebirdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firebirdEntityManagerFactory(@Qualifier("firebirdDataSource") DataSource dataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.FirebirdDialect");
        vendorAdapter.setShowSql(true);

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.FirebirdDialect");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.connection.charSet", "utf-8");

        HibernateJpaDialect jpaDialect = new HibernateJpaDialect();

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setPersistenceUnitName("firebird");
        em.setPackagesToScan("io.swagger.firebird.model");
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(properties);
        em.setJpaDialect(jpaDialect);

        return em;
    }

    @Bean(name = "firebirdTransactionManager")
    public JpaTransactionManager firebirdTransactionManager(@Qualifier("firebirdEntityManagerFactory") EntityManagerFactory firebirdEntityManagerFactory) {
        return new JpaTransactionManager(firebirdEntityManagerFactory);
    }

}
