package jitter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;

public class JitterCommandTest {

     @Test
     public void testWithCommandLineOption() {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         System.setOut(new PrintStream(baos));

         try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
             String[] args = new String[] { "-v" };
             PicocliRunner.run(JitterCommand.class, ctx, args);

         }
     }

}
