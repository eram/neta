package com.netalign.sioc;

import java.util.List;

/**
 *
 * @author yoavram
 */
public interface IFoafPerson extends IFoafAgent {

    void setSchool(String school);

    String getSchool();

    void setWorkHomepage(String url);

    String getWorkHomepage();

    void setWorkInfoPage(String url);

    String getWorkInfoPage();

    String getTitle();

    void setTitle(String title);

    void setDepiction(String depiction);

    String getDepiction();

    void setPhone(String number);

    String getPhone();

    String getFirstName();

    String getSurname();

    String getFamilyname();

    String getGivenname();

    String getSeeAlso();

    void setFirstName(String firstname);

    void setSurname(String surname);

    void setGivenname(String givenname);

    void setFamilyname(String familyname);

    void setSeeAlso(String seeAlso);

    String getMyersBriggs();

    void setMyersBriggs(String myersBriggs);

    String getImg();

    void setImg(String img);

    List<IFoafPerson> getFriends();

    void setFriends(List<IFoafPerson> friends);
}
       
            


