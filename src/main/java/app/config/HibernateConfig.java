package app.config;

import app.entities.*;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {

    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    public static void setTest(Boolean test) { isTest = test; }
    public static Boolean getTest() { return isTest; }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) emf = createEMF(getTest());
        return emf;
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null){
            setTest(true);
            emfTest = createEMF(getTest());
        }
        return emfTest;
    }

    private static void getAnnotationConfiguration(Configuration configuration) {
        // Security (school)
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        // MacroCoach entities
        configuration.addAnnotatedClass(Profile.class);
        configuration.addAnnotatedClass(Recipe.class);
        configuration.addAnnotatedClass(Day.class);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(PantryItem.class);
    }

    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            setBaseProperties(props);
            if (forTest) {
                setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            return sf.unwrap(EntityManagerFactory.class);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void setBaseProperties(Properties props) {
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "update"); // switch to "update" for stable DB / deploy
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
    }

    private static void setDeployedProperties(Properties props) {
        String dbName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + dbName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
    }

    private static String propOrDefault(String key, String file, String def) {
        String v = app.utils.Utils.getPropertyValue(key, file);
        return (v == null || v.isBlank()) ? def : v;
    }

    private static void setDevProperties(Properties props) {
        String DBName = app.utils.Utils.getPropertyValue("DB_NAME", "config.properties");
        String DB_USERNAME = propOrDefault("DB_USERNAME", "config.properties", "postgres");
        String DB_PASSWORD = propOrDefault("DB_PASSWORD", "config.properties", "postgres");

        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", DB_USERNAME);
        props.put("hibernate.connection.password", DB_PASSWORD);
    }

    private static void setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:16.2-alpine:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.archive.autodetection", "class");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
    }
}
