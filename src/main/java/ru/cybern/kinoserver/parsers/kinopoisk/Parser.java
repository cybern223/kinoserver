package ru.cybern.kinoserver.parsers.kinopoisk;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.cybern.kinoserver.parsers.Global;
import ru.cybern.kinoserver.parsers.models.Movie;
import ru.cybern.kinoserver.parsers.models.Soundtrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class);

    private static final String BASE_ADDRESS = "http://www.kinopoisk.ru";

    private HashMap<String,Movie> movieLibrary;

    private boolean saveImages;

    public Parser(boolean saveImages) {
        this.saveImages = saveImages;
    }

    public boolean isSaveImages() {
        return saveImages;
    }

    public void setSaveImages(boolean saveImages) {
        this.saveImages = saveImages;
    }

    private static Document connect(String addr) throws IOException {
        Document doc = Jsoup.connect(addr)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
        return doc;
    }

    public HashMap<String,Movie> parse(int from, int to) throws IOException {
        movieLibrary = new HashMap<String, Movie>();
        String startURL;
        Document page;
        startURL =  BASE_ADDRESS + "/lists/ser/%7B\"soundtrack\"%3A\"ok\"%2C\"all\"%3A\"ok\"%2C\"what\"%3A\"content\"%2C\"count\"%3A%7B\"content\"%3A\"2470\"%7D%2C\"order\"%3A\"name\"%2C\"num\"%3A\"1\"%7D/page/";
        for (int i = from; i <= to; i++) //14
        {
            String url = startURL + i;
            page = connect(url);
            Elements moviesElems = page.select("a.all");
            Elements yearElems = page.select("a.orange");
            for (int j = 0; j < yearElems.size(); j++)
            {
                String movUrl = BASE_ADDRESS + moviesElems.get(j).attr("href");
                String movName = moviesElems.get(j).text();
                logger.info("received " + (j+1) + " movies");
                List<Soundtrack> gotSounds = getSounds(movUrl);
                String image = getImage(movUrl);
                if (gotSounds != null && image != null){
                    Movie curMovie = new Movie(getSounds(movUrl),getImage(movUrl),Integer.parseInt(yearElems.get(j).text()));
                    movieLibrary.put(movName, curMovie);
                }
            }
        }
        return movieLibrary;
    }

    private String extractNumber(String str) {
        Pattern pattern = Pattern.compile("^.*/(\\d+)/");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }

    public int getLastPageNumber() throws IOException {
        String url = BASE_ADDRESS + "/lists/ser/%7B\"soundtrack\"%3A\"ok\"%2C\"all\"%3A\"ok\"%2C\"what\"%3A\"content\"%2C\"count\"%3A%7B\"content\"%3A\"2470\"%7D%2C\"order\"%3A\"name\"%2C\"num\"%3A\"1\"%7D/perpage/200/";
        Document page = connect(url);
        Elements elem = page.select("li.arr");
        String lastPageURL = page.select("li.arr").get(1).select("a").attr("href");
        int pageCnt = Integer.parseInt(extractNumber(lastPageURL));
        return pageCnt;
    }

    private String getImage(String url) throws IOException {
        Document page = connect(url);
        Elements img = page.getElementsByAttributeValue("style", "border:5px solid #ccc");
        if (img.isEmpty()) return null;
        String imgURL = img.first().attr("src");
        String [] URLTokens = imgURL.split("/");
        String filename = Global.KINOPOISK_PREFIX + URLTokens[URLTokens.length - 1];
        if(saveImages)
            saveImage(imgURL, filename);
        return filename;
    }

    private List<Soundtrack> getSounds(String url) throws IOException {
        List<Soundtrack> sounds = new LinkedList<Soundtrack>();
        Document page = connect(url);
        Elements soundBlocks = page.getElementsByAttributeValue("style", "color:#f60");
        if (soundBlocks.isEmpty()) return null;
        for (Element sound : soundBlocks)  {
            String name;
            Elements link = sound.getElementsByTag("a");
            if(!link.isEmpty()) name = link.text();
            else name = sound.ownText();
            String author = sound.getElementsByTag("small").text();
            Soundtrack track = new Soundtrack(name,author);
            sounds.add(track);
        }
        return sounds;
    }

    private void saveImage(String imageUrl, String destinationFile) throws IOException {
        File dir = new File(Global.HOME_PATH + Global.IMG_PATH);
        dir.mkdirs();
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(Global.HOME_PATH + Global.IMG_PATH + destinationFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

}
