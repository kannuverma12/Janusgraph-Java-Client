package com.paytm.digital.education.application;

import com.paytm.digital.education.explore.service.InstituteByProductService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import com.paytm.vault.VaultUtil;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

@SpringBootApplication(exclude = {EmbeddedMongoAutoConfiguration.class})
@Profile("cron")
public class UserOnBoardCron implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserOnBoardCron.class);
    private static final String vaultSourceProperty          = "vault.source";
    private static final String springProfilesActiveProperty = "spring.profiles.active";

    static {
        MainMapLookup.setMainArguments("digital-education-service");
    }

    @Autowired
    private InstituteByProductService instituteByProductService;

    public static void main(String[] args) {
        log.info("started User On board cron to send report, profile : " + System
                .getProperty("spring.profiles.active"));
        SpringApplication springApplication = new SpringApplication(UserOnBoardCron.class);
        springApplication.addListeners(new UserOnBoardCron.FetchVault());
        springApplication.run(args);
        log.info("Exiting from System.");
        System.exit(0);
    }

    private static void loadVaultProperties() throws Exception {
        try {
            String[] profiles = System.getProperty(springProfilesActiveProperty).split(",");
            System.setProperty(springProfilesActiveProperty, profiles[1]);
            VaultUtil.injectVaultProperties(springProfilesActiveProperty);
        } catch (Exception e) {
            log.error("Could not inject vault properties ", e);
            throw e;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting UserOnBoard cron to send report.");
        instituteByProductService.sendReport();
        log.info("User on board report sent!");
    }


    static class FetchVault implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            Boolean isVaultSource = event.getEnvironment()
                    .getProperty(vaultSourceProperty, Boolean.class, Boolean.FALSE);
            log.info("Vault properties flag set for profile? : {}", isVaultSource);
            if (isVaultSource) {
                String profile = event.getEnvironment().getProperty(springProfilesActiveProperty);
                log.info("Profile : {}", profile);
                if (StringUtils.isEmpty(profile)) {
                    log.error("Profile not present in application.properties");
                    System.exit(1);
                }
                System.setProperty(springProfilesActiveProperty, profile);
                log.info("Going to load vault properties");
                try {
                    loadVaultProperties();
                } catch (Exception e) {
                    log.error("Exception while loading vault properties", e);
                    System.exit(1);
                }
            }
        }
    }
}
