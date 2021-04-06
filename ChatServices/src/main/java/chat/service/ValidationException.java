package chat.service;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException{

    List<String> errors =  new ArrayList<>();

    public ValidationException(){

    }

    public ValidationException(String message){
        super(message);
        errors.add(message);
    }

    public ValidationException(List<String> errors){
        this.errors = errors;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getErrors(){
        StringBuilder errors = new StringBuilder();
        for(String error : this.errors){
            errors.append(error).append("\n");
        }
        return errors.toString();
    }

}
