package keepinmemory;

import java.io.Serializable;
import java.util.Date;

public class RssFeedRecord implements Serializable{

	private static final long serialVersionUID = 1L;
	private String rssFeed;
	private Date entryDt;
	private Date lastUsedDt;
	
	
	
	public String getRssFeed() {
		return rssFeed;
	}

	public void setRssFeed(String rssFeed) {
		this.rssFeed = rssFeed;
	}

	public Date getEntryDt() {
		return entryDt;
	}

	public void setEntryDt(Date entryDt) {
		this.entryDt = entryDt;
	}

	public Date getLastUsedDt() {
		return lastUsedDt;
	}

	public void setLastUsedDt(Date lastUsedDt) {
		this.lastUsedDt = lastUsedDt;
	}

	public RssFeedRecord(String rssFeed) {
		entryDt=new Date();
		lastUsedDt=new Date();
		this.rssFeed=rssFeed.trim();
	}

	@Override
	public String toString() {
		return "RssFeedRecord [rssFeed=" + rssFeed + ", entryDt=" + entryDt + ", lastUsedDt=" + lastUsedDt + "]";
	}
	
	

}
