This repository contains a rest api that accepts link of any rss feed and checks whether images are available for all of the articles.
If image is not available for any article it will fetch the full article html, and extract OG image (google it) and populate the rss feed with the images for each article,
then it sends back the rss as original with added image.
An instance is already hosted. If you want to test it please go ahead.

Example: you already use rss of https://example.com/rss. Then try https://fixrss.onrender.com/fixrss?url=https://example.com/rss . Just prefix https://fixrss.onrender.com/fixrss?url=
If the rss feed was missing images using this new url will fix the problem.

Thanks.
*Not for commercial use. I hosted for testing purpose only.
