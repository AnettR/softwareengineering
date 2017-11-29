package de.dhbw.softwareengineering.utilities;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class Constants {

    public static final ClassPathXmlApplicationContext context;

    static{
        ApplicationContextProvider appContext = new ApplicationContextProvider();
        context = new ClassPathXmlApplicationContext(appContext.getApplicationContext());
    }

    public final static String STATUS_ATTRIBUTE_NAME           = "status";
    public final static String STATUSCODE_PWMISSMATCH          = "pwmissmatch";
    public final static String STATUSCODE_USERNAMETOOLONG      = "nametoolong";
    public final static String STATUSCODE_EMAILTOOLONG         = "emailtoolong";
    public final static String STATUSCODE_EMPTYFORM            = "emptyform";
    public final static String STATUSCODE_SUCREG               = "sucreg";
    public final static String STATUSCODE_PWTOOSHORT           = "pwtooshort";
    public final static String STATUSCODE_PWTOOLONG            = "pwtoolong";
    public final static String STATUSCODE_EMAILINVALID         = "emailinvalid";
    public final static String STATUSCODE_EMAILALREADYINUSE    = "usedemail";
    public final static String STATUSCODE_USERNAMEALREADYINUSE = "useduser";
    public final static String STATUSCODE_MODAL_TEMP = "temp_modal";

    public final static String STATUSCODE_MODAL_HEADER = "temp_modal_header";
    public final static String STATUSCODE_MODAL_BODY = "temp_modal_header";

    public final static String CONFIGURATION_DIRECTORY = "." + File.separator + "conf";
    public final static String MYSQL_CONFIGURATION_NAME = "mysql.conf";
    public final static String EMAIL_CONFIGURATION_NAME = "email.conf";

    public final static String TEMPLATE_DIRECTORY = "." + File.separator + "template";
    public final static String SIGNUP_EMAIL_TEMPLATE = "signup.html";
    public final static String RECOVER_PASSWORD_EMAIL_TEMPLATE = "recover.html";
}
