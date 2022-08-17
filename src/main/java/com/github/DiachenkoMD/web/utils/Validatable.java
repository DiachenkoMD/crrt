package com.github.DiachenkoMD.web.utils;

import com.github.DiachenkoMD.entities.enums.ValidationParameters;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Validatable {
    private static HashMap<ValidationParameters, Pattern> patterns = new HashMap<>();

    static {
        patterns.put(ValidationParameters.NAME, Pattern.compile("[a-zA-ZА-ЩЬЮЯҐЄІЇа-щьюяґєії'`]+"));
        patterns.put(ValidationParameters.EMAIL, Pattern.compile("\\w+@[a-zA-Z0-9]+\\.[a-z]+"));
        patterns.put(ValidationParameters.PASSWORD, Pattern.compile("(?=.*\\d)[a-zA-Z\\d]{4,}$"));
        patterns.put(ValidationParameters.DOC_NUMBER, Pattern.compile("^\\d{9}$"));
        patterns.put(ValidationParameters.RNTRC, Pattern.compile("^\\d{10}$"));
        patterns.put(ValidationParameters.AUTHORITY, Pattern.compile("^\\d{4}$"));
    }

    private Object data;
    private ValidationParameters validationParameter;

    private boolean isNullAllowed = false;

    public static Validatable of(Object data, ValidationParameters validationParameter){
        Validatable validatable = new Validatable();

        validatable.setData(data);
        validatable.setValidationParameter(validationParameter);

        return validatable;
    }

    public static Validatable of(Object data, ValidationParameters validationParameter, boolean isNullAllowed){
        Validatable val = Validatable.of(data, validationParameter);
        val.setNullAllowed(isNullAllowed);

        return val;
    }

    public boolean validate(){
        if(data == null)
            return isNullAllowed;

        Pattern pattern = patterns.get(validationParameter);

        if(pattern != null){
            String str = String.valueOf(data);
            return pattern != null && pattern.matcher(str).matches();
        }else{
            if(data instanceof LocalDate ld){
                return switch (validationParameter){
                    case DATE_OF_BIRTH -> ld.isAfter(LocalDate.of(1910, Month.JANUARY, 1));
                    case DATE_OF_ISSUE -> ld.isAfter(LocalDate.of(1991, Month.JANUARY, 1));
                    default -> false;
                };
            }
            return false;
        }


    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ValidationParameters getValidationParameter() {
        return validationParameter;
    }

    public void setValidationParameter(ValidationParameters validationParameter) {
        this.validationParameter = validationParameter;
    }

    public boolean isNullAllowed() {
        return isNullAllowed;
    }

    public void setNullAllowed(boolean nullAllowed) {
        isNullAllowed = nullAllowed;
    }


}
