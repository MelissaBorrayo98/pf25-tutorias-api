package com.example.pf25tutorias;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Esta prueba arranca toda la aplicación y necesita PostgreSQL en ejecución.
// Está desactivada para que "mvn test" funcione sin base de datos; actívela quitando @Disabled.
@Disabled("Requiere PostgreSQL en ejecución")
@SpringBootTest
class Pf25TutoriasApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
