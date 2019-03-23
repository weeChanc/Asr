package com.weechan.asr;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {

        Socket socket = new Socket("127.0,0,1", 1234);
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(bufferedReader.readLine());

    }

}