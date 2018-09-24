package org.mitre.oauth2.repository.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.mitre.host.model.DefaultHostInfo;
import org.mitre.host.model.HostInfo;
import org.mitre.host.repository.HostInfoRepository;
import org.mitre.host.repository.impl.JpaHostInfoRepository;
import org.mitre.host.service.HostInfoService;
import org.mitre.host.service.impl.DefaultHostInfoService;
import org.mitre.host.util.HostUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

public class TestDatabaseConfiguration {

	@Autowired
	private JpaVendorAdapter jpaAdapter;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	public TestDatabaseConfiguration() throws MalformedURLException {
		HostUtils.setCurrentHost(new URL("http://localhost"));
	}

	@Bean
	public JpaOAuth2TokenRepository repository() {
		return new JpaOAuth2TokenRepository();
	}

	@Bean(name = "defaultPersistenceUnit")
	public FactoryBean<EntityManagerFactory> entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPackagesToScan("org.mitre", "org.mitre");
		factory.setPersistenceProviderClass(org.eclipse.persistence.jpa.PersistenceProvider.class);
		factory.setPersistenceUnitName("test" + System.currentTimeMillis());
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaAdapter);
		Map<String, Object> jpaProperties = new HashMap<String, Object>();
		jpaProperties.put("eclipselink.weaving", "false");
		jpaProperties.put("eclipselink.logging.level", "INFO");
		jpaProperties.put("eclipselink.logging.level.sql", "INFO");
		jpaProperties.put("eclipselink.cache.shared.default", "false");
		factory.setJpaPropertyMap(jpaProperties);

		return factory;
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder(new DefaultResourceLoader() {
			@Override
			public Resource getResource(String location) {
				String sql;
				try {
					sql = new String(Files.readAllBytes(Paths.get("..", "openid-connect-server-webapp", "src", "main",
							"resources", "db", "hsql", location)), UTF_8);
				} catch (IOException e) {
					throw new RuntimeException("Failed to read sql-script " + location, e);
				}

				return new ByteArrayResource(sql.getBytes(UTF_8));
			}
		}).generateUniqueName(true).setScriptEncoding(UTF_8.name()).setType(EmbeddedDatabaseType.HSQL)
				.addScripts("hsql_database_tables.sql").build();
	}

	@Bean
	public JpaVendorAdapter jpaAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		adapter.setDatabase(Database.HSQL);
		adapter.setShowSql(true);
		return adapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager platformTransactionManager = new JpaTransactionManager();
		platformTransactionManager.setEntityManagerFactory(entityManagerFactory);
		return platformTransactionManager;
	}
	
	@Bean
	public HostInfoRepository hostInfoRepository() {
		return new JpaHostInfoRepository();
	}
	
	@Bean
	public HostInfoService hostInfoService() {
		return new DefaultHostInfoService();
	}
}
