package business;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

@RestController
public class CleanHtml {

	//Try the link http://localhost:8080/clean1?url=https://<RSS url>
	
	@RequestMapping(value = "/clean1", produces = MediaType.TEXT_XML_VALUE)
	public String FixRssApiHttpReuestHandler(@RequestParam String url) {
		try {

			SyndFeedInput input = new SyndFeedInput();
			@SuppressWarnings("deprecation")
			SyndFeed feed = input.build(new XmlReader(new URL(url)));

			List<SyndEntry> syndEntries = feed.getEntries();

			syndEntries = syndEntries.parallelStream().map(entry -> {

				try {
					String articleLink = (entry.getLink() == null || entry.getLink().isEmpty() ? entry.getUri()
							: entry.getLink());

					String finalContent = "";

					finalContent = this.getCleanHtml(articleLink);

					SyndContent content = new SyndContentImpl();
					content.setType("html");
					content.setValue(finalContent);

					entry.setContents(Arrays.asList(content));

					return entry;

				} catch (Exception e) {
					System.out.println(e.getMessage());
					return entry;
				}
			}).collect(Collectors.toList());

			feed.setEntries(syndEntries);

			SyndFeedOutput output = new SyndFeedOutput();

			String outputStr = output.outputString(feed);
			return outputStr;

		} catch (Exception e) {
			return e.toString();
		}

	}

	public String getCleanHtml(String articleUrl) throws IOException {
		Document doc = Jsoup.connect(articleUrl).userAgent(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
				.header("Accept-Language", "en-US,en;q=0.9").header("Connection", "keep-alive")
				.header("Upgrade-Insecure-Requests", "1").header("Sec-Fetch-Dest", "document")
				.header("Sec-Fetch-Mode", "navigate").header("Sec-Fetch-Site", "cross-site")
				.header("Sec-Fetch-User", "?1").referrer("https://www.google.com/").ignoreHttpErrors(true).get();

		List<String> allImages = new ArrayList<String>();

		Pattern pattern = Pattern.compile("\"(https[^\"]*u002F[^\"]*jumbo[^\\\"]*\\.(jpg|png)[^\"]*)\"");
		Matcher matcher = pattern.matcher(doc.html());
		while (matcher.find()) {
			String link1 = matcher.group(1);
			Properties properties = new Properties();
			properties.load(new StringReader("key=" + link1));
			String link2 = properties.getProperty("key");
			allImages.add(link2);
		}

		Elements divs = doc.select("div.css-nwd8t8[data-testid=lazy-image]");
		int i = allImages.size() - doc.toString().split("data-testid=\"lazyimage-container\"", -1).length + 1;
		for (Element div : divs) {
			try {

				String imageLinkSubsequent = allImages.get(i++);

				Element picture = new Element("picture");
				Element img = new Element("img");
				img.attr("src", imageLinkSubsequent);
				img.attr("style", "width: 100%; height: auto;");
				picture.appendChild(img);
				div.replaceWith(picture);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (Element img : doc.select("img.g-aiImg")) {
			String dataSrc = img.attr("data-src");
			if ((!dataSrc.isEmpty()) && img.attr("src").startsWith("data")) {
				img.attr("src", dataSrc);
			}
		}

		Elements artboardDivs = doc.select("div.g-artboard");
		for (int j = 0; j < artboardDivs.size(); j++) {
			if (j % 2 != 0) {
				artboardDivs.get(j).remove();
			}
		}

		for (Element img : doc.select("img[alt='Video player loading']")) {
			img.parent().attr("style", "filter: none; -webkit-filter: none;");

			Element nextDiv = img.parent().nextElementSibling();
			if (nextDiv != null) {
				nextDiv.removeAttr("class");

				Element firstChildDiv = nextDiv.child(0);
				if (firstChildDiv != null) {
					firstChildDiv.removeAttr("class");
				}
			}
		}

//        Elements h1Tags = doc.select("header h1");
//        for (Element h1 : h1Tags) {
//            Element parent = h1.parent();
//            if (parent != null) {
//                parent.remove();
//            }
//        }

		Elements headers = doc.select("header");
		for (Element header : headers) {
			header.attr("style", "margin-top: 0;");
		}

		Elements scriptTags = doc.select("script,footer");
		for (Element scriptTag : scriptTags) {
			if (!scriptTag.id().equals("interactive-footer")) {
				scriptTag.remove();
			}
		}

		Elements divsToRemove = doc.select(
				"div.css-ec8ke8,div.css-1mc46rn,div.css-uc4bdz,div.bottom-of-article,div.css-sxwst7,div.NYTAppHideMasthead,div.css-1aeqhal,div.css-1lnfix7,div.css-qznc1j,div.css-10cldcv");
		for (Element div : divsToRemove) {
			div.remove();
		}

		return doc.outerHtml();
	}

}
