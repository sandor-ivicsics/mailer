/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.accassist.mailer;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivicsicssandor
 */
public class MailerTest {
    
    public MailerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of generatePlainText method, of class Mailer.
     * @throws java.lang.Exception
     */
    @Test
    public void testGeneratePlainText() throws Exception {
        System.out.println("generatePlainText");
        Mailer instance = new Mailer(new File("src/test/resources/mailer.properties"));
        assertTrue(instance.loadProperties());
        String result = instance.generatePlainText("somebody@dummy.com", "Test", "Test");
        System.out.println(result);
        assertTrue(result.contains("Content-Type: text/plain; charset=utf-8"));
    }

    /**
     * Test of generateHtmlText method, of class Mailer.
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateHtmlText() throws Exception {
        System.out.println("generateHtmlText");
        Mailer instance = new Mailer(new File("src/test/resources/mailer.properties"));
        assertTrue(instance.loadProperties());
        String result = instance.generateHtmlText("somebody@dummy.com", "Test", "<h1>Test</h1>");
        System.out.println(result);
        assertTrue(result.contains("Content-Type: text/html; charset=utf-8"));
    }
    
}
