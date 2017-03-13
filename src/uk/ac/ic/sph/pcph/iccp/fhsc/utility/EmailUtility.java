package uk.ac.ic.sph.pcph.iccp.fhsc.utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named
@ApplicationScoped
public class EmailUtility {

	private static Context envCtx;
    private static Session session;
    private static String emailHost;
    private static String port;
    private static String user;
    private static String password;
    private static String mailFrom;
    private static String protocol;

    //get the mail server details via JNDI look up
    static {
        try {
            Context initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");
            session = (Session) envCtx.lookup("mail/Session");
            emailHost = session.getProperty("mail.smtp.host");
            port = session.getProperty("mail.smtp.port");
            user = session.getProperty("username");
            password = session.getProperty("mail.password");
            mailFrom = session.getProperty("mail.from");
            protocol = session.getProperty("mail.transport.protocol");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param emailSubject subject of the email
     * @param emailContent content of the email
     * @param userName A new user registers with FHSC
     * @throws MessagingException
     */
    public void notifyAdmin(String emailSubject, String emailContent)
            throws MessagingException {

        MimeMessage msg = new MimeMessage(session);
        msg.setSubject(emailSubject);

        msg.setContent(emailContent, "text/html; charset=utf-8");

        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailFrom));
        Transport transport = session.getTransport(protocol);
        transport.connect(emailHost, new Integer(port), user, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    public void notifyUserAsHTML(String emailSubject, String emailContent,
            String userEmailAddress) throws Exception {
    	
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(mailFrom));
        msg.setSubject(emailSubject);
        msg.setContent(emailContent, "text/html; charset=utf-8");
        
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmailAddress));
        Transport transport = session.getTransport(protocol);
        transport.connect(emailHost, new Integer(port), user, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    public void notifyUserAsText(String emailSubject, String emailContent,
            String userEmailAddress) throws Exception {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(mailFrom));
        msg.setSubject(emailSubject);

        msg.setText(emailContent);

        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmailAddress));
        Transport transport = session.getTransport(protocol);
        transport.connect(emailHost, new Integer(port), user, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

}