package uk.ac.ic.sph.pcph.iccp.fhsc.utility;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.*;

@Named
@ApplicationScoped
public class FreemarkerHelper {

	private Logger logger = LoggerFactory.getLogger(FreemarkerHelper.class);
	
	public String generateMessage(String templatePath, Map<String, Object>  dataModel) throws IOException, TemplateException {
		
		String message = null;
		String templateString = getTemplateFromPath(templatePath);

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setIncompatibleImprovements(new Version(2, 3, 23));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.UK);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        
        StringTemplateLoader textStringLoader = new StringTemplateLoader();
        textStringLoader.putTemplate("template", templateString);
        configuration.setTemplateLoader(textStringLoader);
        StringWriter stringWriter = new StringWriter();

        try {
            Template htmlTemplate = configuration.getTemplate("template");
            htmlTemplate.process(dataModel, stringWriter);
            message = stringWriter.toString();
           logger.debug("message: " + message);
           System.out.println("message: " + message);
//            Writer file = new FileWriter(new File("C:\\opt\\server\\external_resource_folder\\nea-ticketing\\email_template\\html-mail-template_test.html"));
//            htmlTemplate.process(dataModel, file);
//            file.flush();
//            file.close();
        } catch (Exception exp) {
        	exp.printStackTrace();
        } finally {
            stringWriter.close();
        }        
        return message;
	}
	
	private String getTemplateFromPath(String path) throws IOException {	
		
        String content;
        File file = new File(path);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            content = fileContents.toString();
        } finally {
            scanner.close();
        }

        return content;
    }

}