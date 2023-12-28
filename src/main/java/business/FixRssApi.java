package business;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

import keepinmemory.FeedUrlPersistInMemory;
import tools.ImageTool;

@RestController
public class FixRssApi {

	@RequestMapping(value="/fixrss",produces=MediaType.TEXT_XML_VALUE)
	public String FixRssApiHttpReuestHandler(@RequestParam String url) {
		try {

			SyndFeedInput input = new SyndFeedInput();
			@SuppressWarnings("deprecation")
			SyndFeed feed = input
					.build(new XmlReader(new URL(url)));

			List<SyndEntry> syndEntries = feed.getEntries();
			
			//If number of feed article above 1 then it will keep in memory
			if(syndEntries.size()>1) {
				FeedUrlPersistInMemory.updateRssFeedLink(url.trim());
			}
			
			//Limiting number of article to 20
			//syndEntries=syndEntries.subList(0, Integer.min(syndEntries.size(),20));
			
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
					imageUri=ImageTool.getOgImageUrl(entry.getLink());
				} catch (Exception e) {
					imageUri="https://static.toiimg.com/photo/msid-105938467,imgsize-113598.cms";
				}
				enclosure.setUrl(imageUri);
				entry.setEnclosures(Arrays.asList(enclosure));
				return entry;
			}).collect(Collectors.toList());
			
			
			
			
			feed.setEntries(syndEntries);

			SyndFeedOutput output = new SyndFeedOutput();

			String outputStr= output.outputString(feed);
			return outputStr;
			
		} catch (Exception e) {
			return e.toString();
		}
		
		
	}
	
	

}
