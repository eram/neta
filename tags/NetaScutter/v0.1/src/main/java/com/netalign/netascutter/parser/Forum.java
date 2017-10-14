package com.netalign.netascutter.parser;

import java.util.*;
import com.netalign.sioc.*;

/**
 * A simple implementation for the {@link ISiocForum} interface, overriding methods only.
 * Extends {@link Container} with <i>has_moderator</i> and <i>has_host</i>.
 * @author yoavram
 * @see ISiocForum
 * @see Container
 */
public class Forum extends Container implements ISiocForum, ISiocContainer {
	private ISiocSite host;
	private List<ISiocUser> moderators = Collections.emptyList();
		
	@Override
	public void addHasModerator(ISiocUser user) {
		moderators.add(user);
	}
	@Override
	public ISiocSite getHasHost() {
		return host;
	}
	@Override
	public List<ISiocUser> getHasModerator() {
		return moderators;
	}
	@Override
	public void setHasHost(ISiocSite host) {
		this.host = host;		
	}
	@Override
	public void setHasModerator(List<ISiocUser> users) {
		this.moderators = users;		
	}	
}
