package cn.bingoogolapple.rxjava.util;

import android.util.Base64;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;

import cn.bingoogolapple.rxjava.model.JsonResp;
import cn.bingoogolapple.rxjava.model.Person;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/30 下午11:19
 * 描述:
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://your.api-base.url";

    private static OkHttpClient httpClient = new OkHttpClient();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceBaseAuthentication(Class<S> serviceClass) {
        return createServiceBaseAuthentication(serviceClass, null, null);
    }

    public static <S> S createServiceBaseAuthentication(Class<S> serviceClass, String username, String password) {
        if (username != null && password != null) {
            // concatenate username and password with colon for authentication
            String credentials = username + ":" + password;
            // create Base64 encodet string
            final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.interceptors().clear();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "applicaton/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceTokenAuthentication(Class<S> serviceClass) {
        return createServiceTokenAuthentication(serviceClass, null);
    }

    public static <S> S createServiceTokenAuthentication(Class<S> serviceClass, final String authToken) {
        if (authToken != null) {
            httpClient.interceptors().clear();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", authToken)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }


    /**
     * call.enqueue(new Callback<User>() {
     *
     * @Override public void onResponse(Response<User> response, Retrofit retrofit) {
     * if (response.isSuccess()) {
     * // use response data and do some fancy stuff :)
     * } else {
     * // parse the response body …
     * APIError error = ErrorUtils.parseError(response, retrofit);
     * // … and use it to show error information
     * <p>
     * // … or just log the issue like we’re doing :)
     * Log.d("error message", error.message());
     * }
     * }
     * @Override public void onFailure(Throwable t) {
     * // there is more than just a failing request (like: no internet connection)
     * }
     * });
     */
    public static JsonResp parseError(retrofit.Response<Person> response, Retrofit retrofit) {
        Converter<ResponseBody, JsonResp> converter = retrofit.responseConverter(JsonResp.class, new Annotation[0]);

        JsonResp error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new JsonResp();
        }

        return error;
    }
}
