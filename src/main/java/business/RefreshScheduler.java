package business;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import keepinmemory.FeedUrlPersistInMemory;
import keepinmemory.RssFeedRecord;
import tools.ImageTool;

@Configuration
@EnableScheduling
public class RefreshScheduler {

	//Every 60 seconds records will be saved in file
	@Scheduled(fixedDelay = 60000)
	public void persistAllToFile() {
		FeedUrlPersistInMemory.persistAllToFile();
	}
	
	//Every half an hour it will check for new articles
	@Scheduled(fixedDelay = 1800000)
	public void refreshRssFeeds() {
		long startTime = System.currentTimeMillis();

		try {
			for (RssFeedRecord feedRecord : FeedUrlPersistInMemory.getAllSavedRssFeedRecords()) {
				String feedLink = feedRecord.getRssFeed();

				SyndFeedInput input = new SyndFeedInput();
				@SuppressWarnings("deprecation")
				SyndFeed feed = input.build(new XmlReader(new URL(feedLink)));

				List<SyndEntry> syndEntries = feed.getEntries();

				syndEntries.forEach(entry -> {
					String articleLink = entry.getLink();
					try {
						ImageTool.getOgImageUrl(articleLink);
					} catch (IOException e) {
						System.out.println("Error Fetching Og image for : " + articleLink);
					}
				});

			}
		} catch (Exception e) {
			System.out.println("Error Refreshing : " + e.getMessage());
		}

		System.out.println("Time taken to complete Reresh : " + (System.currentTimeMillis() - startTime));

	}
}
