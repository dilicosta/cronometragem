package com.taurus.racingTiming;

import com.taurus.racingTiming.controller.IPrincipalController;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.ContextUtil;
import java.util.Locale;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

public class TaurusRacingTimingApplication extends Application {
    
    private static final Log LOG = LogFactory.getLog(TaurusRacingTimingApplication.class);
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("pt", "BR"));
        ApplicationContext appContext = new ClassPathXmlApplicationContext("/META-INF/spring-config.xml");
        
        ContextUtil.setAppContext(appContext);
        
        String name = appContext.getEnvironment().getProperty("application.name");
        String version = appContext.getEnvironment().getProperty("application.version");
        
        LOG.info(String.format("Aplicacao: %s - versão: %s Iniciada", name, version));
        
        ListaSistema listaSistema = appContext.getBean(ListaSistema.class);
        listaSistema.atualizarTodas();
        
        IPrincipalController controller = appContext.getBean(IPrincipalController.class);
        controller.setPrimaryStage(primaryStage);
        
        primaryStage.setScene(new Scene((Parent) controller.getView()));
        primaryStage.setTitle(String.format("%s %s", name, version));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            controller.aoFecharJanela();
        });
        primaryStage.show();
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
