    package fr.delweb.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

    /**
 * Le Main, le point d'entrée c'est tout.
 *
 * @author Miguel Delhelle
 * @version 1.0
 * */

@SpringBootApplication
public class AstIngenierieApplication {

        static {
            if (System.getProperty("log.mode") == null)
                System.setProperty("log.mode", "DETAILED");
            if (System.getProperty("log.level") == null)
                System.setProperty("log.level", "debug");
        }


        public static void main(String[] args) {

        SpringApplication.run(AstIngenierieApplication.class, args);
    }
}
