package at.tugraz.ist.qs2021;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MessageBoardMutantTest {
    public final static String TEST_URL = "https://qs2021.herokuapp.com/";
    public final static String API_KEY = "hX8mOs44";

    @Test
    public void testModelMutant1() throws IOException {
        testModelMutant(1);
    }

    @Test
    public void testModelMutant2() throws IOException {
        testModelMutant(2);
    }

    @Test
    public void testModelMutant3() throws IOException {
        testModelMutant(3);
    }

    @Test
    public void testModelMutant4() throws IOException {
        testModelMutant(4);
    }

    @Test
    public void testModelMutant5() throws IOException {
        testModelMutant(5);
    }

    // BONUS
//    @Test
//    public void testModelMutant81() throws IOException {
//        testModelMutant(81);
//    }
//
//    @Test
//    public void testModelMutant82() throws IOException {
//        testModelMutant(82);
//    }

    public void testModelMutant(int mutant) throws IOException {
        File testsFile = new File("src/test/scala/at/tugraz/ist/qs2021/MessageBoardSpecification.scala");
        byte[] tests = Files.readAllBytes(testsFile.toPath());

        URL url = new URL(TEST_URL + "messageboard/model?key=" + API_KEY + "&mutant=" + Integer.toString(mutant));
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        http.setFixedLengthStreamingMode(tests.length);
        http.addRequestProperty("Accept", "text/plain, text/html");
        http.addRequestProperty("Content-Type", "text/plain");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(tests);
        }

        int status = http.getResponseCode();

        String out = "";
        try (InputStream is = (status >= 300) ? http.getErrorStream() : http.getInputStream()) {
            out = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }


        if (status != HttpURLConnection.HTTP_OK) {
            Assert.fail(String.format("Error %d: %s", status, out));
        }

        http.disconnect();

        Assert.assertEquals("text/plain", http.getHeaderField("Content-Type"));

        String[] split = out.split("\r\n", 2);
        Assert.assertEquals("Failed to kill mutant " + mutant, "KILLED", split[0]);

        System.out.println(split[1]);
    }
}
