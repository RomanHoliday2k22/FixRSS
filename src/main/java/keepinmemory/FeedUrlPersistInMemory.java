package keepinmemory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tools.PersistObjectOnFile;

public class FeedUrlPersistInMemory {
	
	private static String rssFeedRecordsPath = "RssFeedRecords.ser";
	private static String artitcleRecordsPath = "ArticleRecords.ser";

	private static List<RssFeedRecord> allSavedRssFeedRecords;
	private static HashMap<String, ArticleRecord> allSavedArticleRecord;
	
	public static void persistAllToFile() {
		FeedUrlPersistInMemory.saveAllRssFeedRecordsToFile(FeedUrlPersistInMemory.getAllSavedRssFeedRecords());
		FeedUrlPersistInMemory.saveAllArticleRecordsToFile(FeedUrlPersistInMemory.getAllSavedArticleRecord());
	}

	public static List<RssFeedRecord> getAllSavedRssFeedRecords() {
		if (FeedUrlPersistInMemory.allSavedRssFeedRecords == null) {
			FeedUrlPersistInMemory.allSavedRssFeedRecords = FeedUrlPersistInMemory.fetchAllRssFeedRecordsFromFile();
			if (FeedUrlPersistInMemory.allSavedRssFeedRecords == null) {
				FeedUrlPersistInMemory.allSavedRssFeedRecords = new ArrayList<RssFeedRecord>();
			}
		}
		return FeedUrlPersistInMemory.allSavedRssFeedRecords;
	}

	public static void updateRssFeedLink(String rssFeedLink) {

		for (RssFeedRecord rssFeedRecord : FeedUrlPersistInMemory.getAllSavedRssFeedRecords()) {
			if (rssFeedRecord.getRssFeed().equals(rssFeedLink.trim())) {
				rssFeedRecord.setLastUsedDt(new Date());
				return;
			}
		}
		FeedUrlPersistInMemory.getAllSavedRssFeedRecords().add(new RssFeedRecord(rssFeedLink.trim()));

	}

	public static HashMap<String, ArticleRecord> getAllSavedArticleRecord() {
		if (FeedUrlPersistInMemory.allSavedArticleRecord == null) {
			FeedUrlPersistInMemory.allSavedArticleRecord = FeedUrlPersistInMemory.fetchAllArticleRecordsFromFile();
			if (FeedUrlPersistInMemory.allSavedArticleRecord == null) {
				FeedUrlPersistInMemory.allSavedArticleRecord = new HashMap<String, ArticleRecord>();
			}
		}
		return FeedUrlPersistInMemory.allSavedArticleRecord;
	}

	public static String getArticleOgImageLinkByArticleLink(String articleLink) {

		ArticleRecord articleRecord = FeedUrlPersistInMemory.getAllSavedArticleRecord().get(articleLink);

		if (articleRecord != null) {
			articleRecord.setLastUsedDt(new Date());
		}

		if (articleRecord == null) {
			return null;
		} else if (articleRecord.isIfHoldsValidOgImageLink() == false
				&& articleRecord.getNoOfFailedTryForFetchingOgImageLink() >= 5) {
			return "https://static.toiimg.com/photo/msid-105938467,imgsize-113598.cms";
		} else {
			return articleRecord.getOgImageLink();
		}
	}

	public static void saveArticleOgImageLinkByArticleLink(String articleLink, String ogImageLink) {
		FeedUrlPersistInMemory.getAllSavedArticleRecord().put(articleLink, new ArticleRecord(articleLink, ogImageLink));

	}

	public static void reportFailedOgImageFetch(String articleLink) {
		ArticleRecord articleRecord = FeedUrlPersistInMemory.getAllSavedArticleRecord().get(articleLink.trim());
		if (articleRecord == null) {
			FeedUrlPersistInMemory.getAllSavedArticleRecord().put(articleLink, new ArticleRecord(articleLink));
		} else {
			articleRecord.recordFailedOgImageLink();
		}
	}

	
	

	private static void saveAllRssFeedRecordsToFile(List<RssFeedRecord> rssFeedRecords) {
		PersistObjectOnFile.saveObject(rssFeedRecords, rssFeedRecordsPath);
	}

	@SuppressWarnings("unchecked")
	private static List<RssFeedRecord> fetchAllRssFeedRecordsFromFile() {
		try {
			List<RssFeedRecord> fetchFromSavedObject = (List<RssFeedRecord>) PersistObjectOnFile
					.fetchFromSavedObject(rssFeedRecordsPath);
			return fetchFromSavedObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static void saveAllArticleRecordsToFile(HashMap<String, ArticleRecord> articleRecords) {
		PersistObjectOnFile.saveObject(articleRecords, artitcleRecordsPath);
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, ArticleRecord> fetchAllArticleRecordsFromFile() {
		try {
			HashMap<String, ArticleRecord> fetchFromSavedObject = (HashMap<String, ArticleRecord>) PersistObjectOnFile
					.fetchFromSavedObject(artitcleRecordsPath);
			return fetchFromSavedObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
