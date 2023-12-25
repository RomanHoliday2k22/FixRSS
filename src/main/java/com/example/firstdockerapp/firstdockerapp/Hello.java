package com.example.firstdockerapp.firstdockerapp;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.Reference;
import com.rometools.modules.mediarss.types.UrlReference;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

@RestController
public class Hello {

	@RequestMapping(value="/fixrss",produces=MediaType.TEXT_XML_VALUE)
	public String Hello(@RequestParam String url) {
		
		


		try {

			SyndFeedInput input = new SyndFeedInput();
			@SuppressWarnings("deprecation")
			SyndFeed feed = input
					.build(new XmlReader(new URL(url)));

			List<SyndEntry> syndEntries = feed.getEntries();
			
			//Limiting number of article to 20
			syndEntries=syndEntries.subList(0, Integer.min(syndEntries.size(),20));
			
			syndEntries=syndEntries.parallelStream().map(entry -> {

				List<SyndEnclosure> syndEnclosures = entry.getEnclosures();
				for (SyndEnclosure syndEnclosure : syndEnclosures) {
					String type = syndEnclosure.getType();
					if (type.contains("image") || type.contains("img") || type.contains("jpeg") || type.contains("jpg")
							|| type.contains("png")) {
						String imageUrl = syndEnclosure.getUrl();
						//System.out.println(imageUrl);
						if (!(imageUrl.trim().isEmpty())) {
							return entry;
						}
					}
				}

				
				for (Module module : entry.getModules()) {
					
					if (module instanceof MediaEntryModule) {
						MediaEntryModule mediaEntry = (MediaEntryModule) module;
						MediaContent[] mediaContents = mediaEntry.getMediaContents();
						for (MediaContent mediaContent : mediaContents) {
							Reference reference = mediaContent.getReference();
							if (reference instanceof UrlReference) {
								UrlReference urlReference = (UrlReference) reference;
								String imageUrl = urlReference.getUrl().getPath();
								//System.out.println(imageUrl);
								if (!(imageUrl.trim().isEmpty())) {
									return entry;
								}
							}
						}
					}
				}
				SyndEnclosure enclosure=new SyndEnclosureImpl();
				enclosure.setType("image/");
				
				String imageUri;
				try {
					imageUri=this.getOgImageUrl(entry.getLink());
					//System.out.println("No image found in rss feed. OG iamge fetched : "+imageUri);
				} catch (Exception e) {
					imageUri="https://static.toiimg.com/photo/msid-105938467,imgsize-113598.cms";
					//System.out.println("No image found in rss feed, neither OG image was fetched");
				}
				enclosure.setUrl(imageUri);
				entry.setEnclosures(Arrays.asList(enclosure));
				return entry;
			}).collect(Collectors.toList());
			
			feed.setEntries(syndEntries);

			SyndFeedOutput output = new SyndFeedOutput();

			//response.setContentType("text/xml");
			//response.setCharacterEncoding("utf-8");
			//output.output(feed, response.getWriter());
			String outputStr= output.outputString(feed);
			//System.out.println(outputStr);
			return outputStr;
			
		} catch (Exception e) {
			return e.toString();
		}

	
		
		
		
		
		
		
	}
	
	private String getOgImageUrl(String articleLink) throws IOException {
	    Document doc = Jsoup.connect(articleLink).get();
	    Elements metaOgImage = doc.select("meta[property=og:image]");
	    if (metaOgImage != null && metaOgImage.size() > 0) {
	      return metaOgImage.first().attr("content");
	    }
	    throw new IOException("No OG image found");
	  }

}
