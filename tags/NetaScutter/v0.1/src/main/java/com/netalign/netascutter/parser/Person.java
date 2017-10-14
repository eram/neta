package com.netalign.netascutter.parser;

import com.netalign.netascutter.Constants;
import com.netalign.sioc.*;

import java.util.*;

import com.netalign.sioc.IFoafPerson;

/**
 * A simple implementation for the {@link ISiocUser} interface, overriding methods only.
 * 
 * @author yoavram
 */
public class Person extends Agent implements IFoafPerson {

    private String title = Constants.EMPTY_STRING;
    private String firstname = Constants.EMPTY_STRING;
    private String surname = Constants.EMPTY_STRING;
    private String givenname = Constants.EMPTY_STRING;
    private String familyname = Constants.EMPTY_STRING;
    private String depiction = Constants.EMPTY_STRING;
    private String phone = Constants.EMPTY_STRING;
    private String seeAlso = Constants.EMPTY_STRING;
    private String school = Constants.EMPTY_STRING;
    private String work = Constants.EMPTY_STRING;
    private String workinfo = Constants.EMPTY_STRING;
    private String myersBriggs = Constants.EMPTY_STRING;
    private String img = Constants.EMPTY_STRING;

    private List<IFoafPerson> friends = Collections.emptyList();

    public Person() {
    }

    @Override
    public void setSchool(String school) {
        this.school = school;        
    }

    @Override
    public String getSchool() {
        return school;
    }

    @Override
    public void setWorkHomepage(String url) {
    	this.work = url;
    }

    @Override
    public String getWorkHomepage() {
        return work;
    }

    @Override
    public void setWorkInfoPage(String url) {
    	this.workinfo = url;
    }

    @Override
    public String getWorkInfoPage() {
        return workinfo;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDepiction(String depiction) {
    	this.depiction = depiction;
    }

    @Override
    public String getDepiction() {
        return depiction;
    }

    @Override
    public void setPhone(String number) {
    	this.phone = number;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getFirstName() {
        return firstname;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getFamilyname() {
        return familyname;
    }

    @Override
    public String getGivenname() {
        return givenname;
    }

    @Override
    public String getSeeAlso() {
        return seeAlso;
    }

    @Override
    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public void setFamilyname(String familyname) {
    	this.familyname = familyname;
    }

    @Override
    public void setGivenname(String givenname) {
    	this.givenname = givenname;
    }

    @Override
    public void setSeeAlso(String seeAlso) {
    	this.seeAlso = seeAlso;
    }

    @Override
    public String getMyersBriggs() {
        return myersBriggs;
    }

    @Override
    public void setMyersBriggs(String myersBriggs) {
    	this.myersBriggs = myersBriggs;
    }

    @Override
    public String getImg() {
        return img;
    }

    @Override
    public void setImg(String img) {
    	this.img = img;
    }

    @Override
    public List<IFoafPerson> getFriends() {
        return friends;
    }

    @Override
    public void setFriends(List<IFoafPerson> friends) {
    	this.friends = friends;
    }

    @Override
    public List<IFoafOnlineAccount> getHoldsAccount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHoldsAccount(List<IFoafOnlineAccount> accounts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addHoldsAccount(IFoafOnlineAccount account) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}