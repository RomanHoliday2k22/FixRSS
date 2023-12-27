package tools;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import keepinmemory.FeedUrlPersistInMemory;

public class ImageTool {

	public static String getOgImageUrl(String articleLink) throws IOException {
		
		if(articleLink==null||articleLink.isBlank()||articleLink.isEmpty()) {
			throw new IOException("Error: Article Link is blank.");
		}
		articleLink=articleLink.trim();
		
		String ogImageFromMemory=FeedUrlPersistInMemory.getArticleOgImageLinkByArticleLink(articleLink);		
		if(ogImageFromMemory!=null) { 
			System.out.println("Fetched from memory : "+ogImageFromMemory);
			return ogImageFromMemory;
		}
		
		try {
			
		    Document doc = Jsoup.connect(articleLink).get();
		    Elements metaOgImage = doc.select("meta[property=og:image]");
		    if (metaOgImage != null && metaOgImage.size() > 0) {
		      String ogImageLinkFetched= metaOgImage.first().attr("content");
		      ogImageLinkFetched=ogImageLinkFetched.trim();
		      if(ogImageLinkFetched!=null&&(!ogImageLinkFetched.isBlank())&&(!ogImageLinkFetched.isEmpty())) {
		    	  FeedUrlPersistInMemory.saveArticleOgImageLinkByArticleLink(articleLink, ogImageLinkFetched);
		    	  System.out.println("Fetched from HTTP : "+ogImageLinkFetched);
			      return ogImageLinkFetched;
		      }
		      
		    }
		    throw new IOException("No OG image found");
		} catch (IOException e) {
			FeedUrlPersistInMemory.reportFailedOgImageFetch(articleLink);
			throw e;
		}
	  }
	
}
