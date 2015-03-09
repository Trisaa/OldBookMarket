package com.yunjian.image;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageGetFormHttp {
    private static final String LOG_TAG = "ImageGetForHttp";
                                                          
    public static Bitmap downloadBitmap(String url) {
//        final HttpClient client = new DefaultHttpClient();
//        final HttpGet getRequest = new HttpGet(url);
//                                                              
//        try {
//            HttpResponse response = client.execute(getRequest);
//            final int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
//                return null;
//            }
//                                                                  
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream inputStream = null;
//                try {
//                    inputStream = entity.getContent();
//                    FilterInputStream fit = new FlushedInputStream(inputStream);
//                    return BitmapFactory.decodeStream(fit);
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                        inputStream = null;
//                    }
//                    entity.consumeContent();
//                }
//            }
//        } catch (IOException e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
//        } catch (IllegalStateException e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "Incorrect URL: " + url);
//        } catch (Exception e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
//        } finally {
//            client.getConnectionManager().shutdown();
//        }
//        return null;
    	
    	InputStream bitmapIs = null;
    	Bitmap bitmap=null;
		try {
			URL imageURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			bitmapIs = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(bitmapIs);
			System.out.println(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bitmapIs!=null){
				try {
					bitmapIs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bitmapIs=null;
			}
		}
		return bitmap;
    }
                                                      
    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }
                                                      
        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}