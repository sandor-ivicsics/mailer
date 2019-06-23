/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.accassist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ivicsicssandor
 */
public class Mailer {
    private static final Logger LOGGER = Logger.getLogger(Mailer.class.getName());

    private static final String BASE = "hu.accassist.mailer";
    public static final String USER = BASE + ".user";
    public static final String PASSWORD = BASE + ".password";
    
    private final Properties props;
    private final File file;

    public Mailer(Properties props) {
        this.props = props;
        this.file = null;
    }

    public Mailer(File file) {
        this.props = new Properties();
        this.file = file;
    }

    public Properties getProperties() {
        return props;
    }
    
    private void checkFile() {
        if (file == null) {
            throw new NullPointerException("Reporter was created without file!");
        }
    }
    
    public boolean loadProperties() {
        checkFile();
        try (final FileInputStream inStream = new FileInputStream(file)) {
            props.load(inStream);
            return true;
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.WARNING, "File not found", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO error", ex);
        }
        return false;
    }
    
    public boolean saveProperties() {
        checkFile();
        try (final FileOutputStream outStream = new FileOutputStream(file)) {
            props.store(outStream, null);
            return true;
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "File not found", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO error", ex);
        }
        return false;
    }
    
    private MimeMessage newMimeMessage(String to, String subject, String text, String subtype) {
        if (props.isEmpty()) {
            return null;
        }
        MimeMessage message = new MimeMessage(Session.getInstance(props));
        try {
            message.setFrom();
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set from", ex);
            return null;
        }
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        } catch (AddressException ex) {
            LOGGER.log(Level.SEVERE, "Bad internet address", ex);
            return null;
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set recipient - to", ex);
            return null;
        }
        try {
            message.setSentDate(new Date());
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set sent date", ex);
            return null;
        }
        try {
            message.setSubject(subject);
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set subject", ex);
            return null;
        }
        try {
            message.setText(text, "utf-8", subtype);
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set text", ex);
            return null;
        }
        try {
            message.saveChanges();
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to save changes", ex);
            return null;
        }
        return message;
    }
    
    private boolean sendMimeMessage(MimeMessage message) {
        try {
            Transport.send(message, props.getProperty(USER), props.getProperty(PASSWORD));
            return true;
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to send message", ex);
        }
        return false;
    }
    
    private boolean sendText(String to, String subject, String text, String subtype) {
        MimeMessage message = newMimeMessage(to, subject, text, subtype);
        if (message == null) {
            return false;
        }
        return sendMimeMessage(message);
    }
    
    public boolean sendPlainText(String to, String subject, String plain) {
        return sendText(to, subject, plain, "plain");
    }
    
    public boolean sendHtmlText(String to, String subject, String html) {
        return sendText(to, subject, html, "html");
    }
    
    private String generateText(String to, String subject, String text, String subtype) throws IOException {
        MimeMessage message = newMimeMessage(to, subject, text, subtype);
        if (message == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            message.writeTo(baos);
            return baos.toString();
        }
        catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Failed to write message", ex);
        }
        return null;
    }
    
    String generatePlainText(String to, String subject, String plain) throws IOException {
        return generateText(to, subject, plain, "plain");
    }
    
    String generateHtmlText(String to, String subject, String html) throws IOException {
        return generateText(to, subject, html, "html");
    }
}
