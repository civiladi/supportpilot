package org.chat.supportpilot;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

public final class Auth {
    private static final Pattern USER = Pattern.compile("^[A-Za-z0-9-]{1,64}$");
    private Auth(){}
    public static void checkValid(String userId){
        if(userId == null || !USER.matcher(userId).matches()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
//        return userId;
    }
}
