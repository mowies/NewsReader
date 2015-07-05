package org.random_access.newsreader.nntp;

import android.util.Log;
import android.util.Xml;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 30.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class NNTPMessageBody {

    private static final String TAG = NNTPMessageBody.class.getSimpleName();

    public String parseBodyData(BufferedReader reader, String charset, String encoding) throws IOException{
        String line;
        StringBuilder sbMessageBody = new StringBuilder();
        while((line=reader.readLine()) != null) {
            sbMessageBody.append(line).append("\n");
        }
        reader.close();
        return new MessageDecoder().decodeBody(sbMessageBody.toString(), charset, encoding);
    }

}
