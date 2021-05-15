package io.swagger.response.api;

import io.swagger.postgres.model.security.User;
import lombok.Data;

import java.util.Date;

@Data
public class PasswordRestoreData {

    private static int RESTORE_TIME_LIMIT = 1000 * 60 * 15; //Ссылка на восстановление действительна 15 минут

    private Date date;
    private User user;

    public PasswordRestoreData(User user) {
        this.user = user;
        this.date = new Date();
    }

    public boolean isValid() {
        return ( System.currentTimeMillis() - date.getTime() ) <= RESTORE_TIME_LIMIT;
    }

}
