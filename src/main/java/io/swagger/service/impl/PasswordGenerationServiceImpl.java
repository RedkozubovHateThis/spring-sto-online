package io.swagger.service.impl;

import io.swagger.service.PasswordGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PasswordGenerationServiceImpl implements PasswordGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordGenerationService.class);

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_}{[]";
    private static final boolean USE_UPPER = true;
    private static final boolean USE_DIGITS = true;
    private static final boolean USE_SPECIAL = true;
    private static final int PASSWORD_LENGTH = 6;

    @Override
    public String generatePassword() {

        StringBuilder password = new StringBuilder( PASSWORD_LENGTH );
        Random random = new Random( System.nanoTime() );

        List<String> charCategories = new ArrayList<>();
        charCategories.add(LOWER);
        if ( USE_UPPER )
            charCategories.add(UPPER);
        if ( USE_DIGITS )
            charCategories.add(DIGITS);
        if ( USE_SPECIAL )
            charCategories.add(SPECIAL);

        for ( int i = 0; i < PASSWORD_LENGTH; i++ ) {

            String charCategory = charCategories.get( random.nextInt( charCategories.size() ) );
            int position = random.nextInt( charCategory.length() );

            password.append( charCategory.charAt(position) );

        }

        return new String(password);
    }

}
