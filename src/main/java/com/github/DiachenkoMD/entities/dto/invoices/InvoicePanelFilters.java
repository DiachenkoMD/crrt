package com.github.DiachenkoMD.entities.dto.invoices;

import com.github.DiachenkoMD.entities.dto.DatesRange;
import com.github.DiachenkoMD.entities.dto.Filters;
import com.github.DiachenkoMD.entities.enums.InvoiceStatuses;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.DiachenkoMD.entities.DB_Constants.*;
import static com.github.DiachenkoMD.web.utils.Utils.clean;
import static com.github.DiachenkoMD.web.utils.Utils.cleanGetString;

public class InvoicePanelFilters extends Filters {

    private String code;
    private String carName;
    private DatesRange datesRange;
    private String driverCode;
    private String clientEmail;

    private InvoiceStatuses status;

    @Override
    public HashMap<String, String> getDBPresentation() {
        HashMap<String, String> representation = new HashMap<>();


        // Automatically select non-empty strings
        // (from js comes an object where non-empty fields have an empty string, not null, so this cleanup is necessary)
        representation.put("tbl_invoices."+TBL_INVOICES_CODE, code);
        representation.put("driver_u."+TBL_DRIVERS_CODE, driverCode);
        representation.put("client_u."+TBL_USERS_EMAIL, clientEmail);

        HashMap<String, String> res = representation.entrySet()
                .parallelStream()
                .filter(x -> x.getValue() != null && !x.getValue().isBlank())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> "%"+clean(entry.getValue())+"%",
                                (prev, next) -> next,
                                HashMap::new
                        )
                );

        // Car naming, which consists of Brand and Model, is filtered in db with use of MATCH AGAINST so it should have special characters included like asterisks
        String cleanName = cleanGetString(this.carName);
        if(cleanName != null){
            res.put("carName",
                    Arrays.stream(cleanName.split("[ ,]"))
                            .parallel()
                            .filter(x -> !x.isBlank())
                            .map(x -> "*"+x+"*")
                            .collect(Collectors.joining(" ")));
        }

        // Dates should be formatted before use in db query
        if(datesRange != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            res.put(TBL_INVOICES_DATE_START, formatter.format(datesRange.getStart()));
            res.put(TBL_INVOICES_DATE_END, formatter.format(datesRange.getEnd()));
        }

        return res;
    }


    @Override
    public String toString() {
        return String.format("{\n" +
                "   Code: %s,\n" +
                "   Name: %s,\n" +
                "   DatesRange: %s,\n" +
                "   DriverCode: %s,\n" +
                "   ClientEmail: %s,\n" +
                "   Status: %s\n" +
                "}", this.getCode(), this.getCarName(), this.getDatesRange(), this.getDriverCode(), this.getClientEmail(), this.getStatus());
    }

    private String dgp(String glued, String str){
        return glued + str + glued;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public DatesRange getDatesRange() {
        return datesRange;
    }

    public void setDatesRange(DatesRange datesRange) {
        this.datesRange = datesRange;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public InvoiceStatuses getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatuses status) {
        this.status = status;
    }
}