package keepinmemory;

import java.io.Serializable;
import java.util.Date;

public class ArticleRecord implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String articleLink;
	private String ogImageLink;
	private String articleHtml;
	private String articleHtmlParsedWithReadabilityJS;
	private Date entryDt;
	private Date lastUsedDt;
	private boolean ifHoldsValidOgImageLink=false;
	private int noOfFailedTryForFetchingOgImageLink=0;
	
	
	
	public String getArticleLink() {
		return articleLink;
	}

	public void setArticleLink(String articleLink) {
		this.articleLink = articleLink;
	}

	public String getOgImageLink() {
		return ogImageLink;
	}

	public void setOgImageLink(String ogImageLink) {
		this.ogImageLink = ogImageLink;
	}

	public String getArticleHtml() {
		return articleHtml;
	}

	public void setArticleHtml(String articleHtml) {
		this.articleHtml = articleHtml;
	}

	public String getArticleHtmlParsedWithReadabilityJS() {
		return articleHtmlParsedWithReadabilityJS;
	}

	public void setArticleHtmlParsedWithReadabilityJS(String articleHtmlParsedWithReadabilityJS) {
		this.articleHtmlParsedWithReadabilityJS = articleHtmlParsedWithReadabilityJS;
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

	public boolean isIfHoldsValidOgImageLink() {
		return ifHoldsValidOgImageLink;
	}

	public void setIfHoldsValidOgImageLink(boolean ifHoldsValidOgImageLink) {
		this.ifHoldsValidOgImageLink = ifHoldsValidOgImageLink;
	}

	public int getNoOfFailedTryForFetchingOgImageLink() {
		return noOfFailedTryForFetchingOgImageLink;
	}

	public void setNoOfFailedTryForFetchingOgImageLink(int noOfFailedTryForFetchingOgImageLink) {
		this.noOfFailedTryForFetchingOgImageLink = noOfFailedTryForFetchingOgImageLink;
	}
	

	
	@Override
	public String toString() {
		return "ArticleRecord [articleLink=" + articleLink + ", ogImageLink=" + ogImageLink + ", articleHtml="
				+ articleHtml + ", articleHtmlParsedWithReadabilityJS=" + articleHtmlParsedWithReadabilityJS
				+ ", entryDt=" + entryDt + ", lastUsedDt=" + lastUsedDt + ", ifHoldsValidOgImageLink="
				+ ifHoldsValidOgImageLink + ", noOfFailedTryForFetchingOgImageLink="
				+ noOfFailedTryForFetchingOgImageLink + "]";
	}

	//For successful OG iamge fetch
	public ArticleRecord(String articleLink, String ogImageLink) {
		this.entryDt=new Date();
		this.lastUsedDt=new Date();
		
		this.articleLink = articleLink;
		
		if(ogImageLink==null||ogImageLink.isBlank()||ogImageLink.isEmpty()) {
			this.ifHoldsValidOgImageLink=false;
			this.noOfFailedTryForFetchingOgImageLink++;
		} else {
			this.ifHoldsValidOgImageLink=true;
			this.ogImageLink = ogImageLink;
		}
	}
	
	//For failed OG image fetch
	public ArticleRecord(String articleLink) {
		
		this.entryDt=new Date();
		this.lastUsedDt=new Date();
		
		this.articleLink=articleLink;
		this.noOfFailedTryForFetchingOgImageLink++;
	}
	
	//Unnecessary, instead a new object will be put in hashmap
	public void recordSuccessfulOgImageFetch(String ogImageLink) {
		this.lastUsedDt=new Date();
		
		if(ogImageLink==null||ogImageLink.isBlank()||ogImageLink.isEmpty()) {
			this.ifHoldsValidOgImageLink=false;
			this.noOfFailedTryForFetchingOgImageLink++;
		} else {
			this.ogImageLink = ogImageLink;
			this.ifHoldsValidOgImageLink=true;
			this.noOfFailedTryForFetchingOgImageLink=0;
		}
	}
	
	public void recordFailedOgImageLink() {
		this.lastUsedDt=new Date();
		
		this.ifHoldsValidOgImageLink=false;
		this.noOfFailedTryForFetchingOgImageLink++;
		
	}
}
