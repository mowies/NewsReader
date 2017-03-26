package com.xtracteddev.newsreader.nntp;

import android.text.Html;

import java.io.BufferedReader;
import java.io.IOException;

public class NNTPMessageBody {

    private static final String TAG = NNTPMessageBody.class.getSimpleName();

    public String parseBodyData(BufferedReader reader, String charset, String encoding) throws IOException{
        String line;
        StringBuilder sbMessageBody = new StringBuilder();
        while((line=reader.readLine()) != null) {
            sbMessageBody.append(Html.fromHtml(line).toString()).append("\n");
        }
        reader.close();
        return new MessageDecoder().decodeBody(sbMessageBody.toString(), charset, encoding);
    }
}
