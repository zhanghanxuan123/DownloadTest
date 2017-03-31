package com.zhx.downloadtest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Author   :zhx
 * Create at 2017/3/27
 * Description:
 */
public class DownloadHttp {
    private static DownloadHttp sInstance;
    private String url;
    private File destination;
    private DownloadApi mDownloadApi;

    static final DownloadHttp getInstance() {
        if (sInstance == null) {
            synchronized (DownloadHttp.class) {
                if (sInstance == null) {
                    sInstance = new DownloadHttp();
                }
            }
        }
        return sInstance;
    }


    public DownloadHttp() {
        mDownloadApi = new Retrofit.Builder()
                .baseUrl("https://github.com/zhanghanxuan123")
                .client(getDownloadOkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(DownloadApi.class);
    }

    static  OkHttpClient getDownloadOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new DownloadResponseBody(originalResponse.body()))
                                .build();
                    }
                });
        return builder.build();
    }

    DownloadApi getDownloadApi() {
        return mDownloadApi;
    }

    public interface DownloadApi {
        @Streaming
        @GET
        Observable<ResponseBody> downloadFile(@Url String url);
    }
}
