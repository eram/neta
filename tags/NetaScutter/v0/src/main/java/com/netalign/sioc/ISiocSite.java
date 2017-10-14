package com.netalign.sioc;

import java.util.List;

/**
 *
 * @author yoavram
 */
public interface ISiocSite extends ISiocSpace{
    /**
     * has_administrator - A User who is an administrator of this Site. 
     * @return
     */
    ISiocUser getHasAdministrator();
    void setHasAdministrator(ISiocUser admin);
    /**
     * host_of - A Forum that is hosted on this Site. 
     * @return
     */
    List<ISiocForum> getHostOf();
    void setHostOf(List<ISiocForum> forums);
    void addHostOf(ISiocForum forum);
	String getSeeAlso();
	void setSeeAlso();
}
