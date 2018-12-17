package me.dehasi.highload;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Created by Ravil on 17/12/2018. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    public String sex;
    public String status;
    public String[] interests;
    public String country;
    public String cit;
    public long count;
}
