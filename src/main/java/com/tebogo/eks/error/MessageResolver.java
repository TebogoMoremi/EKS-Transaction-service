package com.tebogo.eks.error;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageResolver {

    private static final String BUNDLE_NAME = "i18n.messages";

    public String resolve(
            String code,
            Locale locale,
            Object... arguments) {

        if (locale == null) {
            locale = Locale.ENGLISH;
        }

        try {

            ResourceBundle bundle =
                    ResourceBundle.getBundle(
                            BUNDLE_NAME,
                            locale
                    );

            String messageTemplate =
                    bundle.getString(code);

            return MessageFormat.format(
                    messageTemplate,
                    arguments
            );

        } catch (MissingResourceException exception) {

            return code;
        }
    }
}