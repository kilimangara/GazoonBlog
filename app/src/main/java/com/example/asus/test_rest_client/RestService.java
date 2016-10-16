package com.example.asus.test_rest_client;

import com.example.asus.test_rest_client.model.Avatar;
import com.example.asus.test_rest_client.model.Comments;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.OwnComments;
import com.example.asus.test_rest_client.model.Post;
import com.example.asus.test_rest_client.model.Posts;
import com.example.asus.test_rest_client.model.User;
import com.example.asus.test_rest_client.model.UserPosts;
import com.example.asus.test_rest_client.model.Users;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface RestService {
    @FormUrlEncoded
    @POST("token-auth/")
    Call<Object> auth(@FieldMap Map<String,Object> map);
    @GET("users/{id}/")
    Call<User> getOwnInfo(@Path("id") int id, @Header("Authorization") String auth);
    @FormUrlEncoded
    @POST("users/")
    Call<User> register(@FieldMap Map<String, Object> map);
    @FormUrlEncoded
    @PATCH("users/{id}/")
    Call<User> patchOwnInfo(@Path("id") int id, @Header("Authorization") String auth, @FieldMap Map<String, Object> user);
    @GET("users/{id}/comments/")
    Call<OwnComments> getComments(@Path("id") int id, @Header("Authorization") String auth);
    @GET("users/{id}/posts/")
    Call<UserPosts> getUserPosts(@Path("id") int id, @Header("Authorization") String auth, @Query("limit") int limit);

    @GET("users/")
    Call<Users> getUsers(@Header("Authorization") String auth,@Query("offset") int page, @Query("limit") int limit);
    @GET("users/")
    Call<Users> getUsers (@Header("Authorization") String auth, @Query("query") String query, @Query("offset") int page);


    @DELETE("posts/{id}/")
    Call<Void> deleteOPost(@Path("id") int id,@Header("Authorization") String auth );
    @FormUrlEncoded
    @PATCH("posts/{id}/")
    Call<Post> patchPost(@Path("id") int id,@Header("Authorization") String auth, @FieldMap Map<String, Object> post );
    @GET("posts/")
    Observable<Posts> getPosts (@Header("Authorization") String auth, @Query("query") String query, @Query("offset") int page);
    @GET("posts/")
    Observable<Posts> getPosts (@Header("Authorization") String auth, @Query("offset") int page);
    @POST("posts/{id}/")
    Call<Post> publishPost(@Path("id") int id, @Header("Authorization") String auth);
    @FormUrlEncoded
    @POST("posts/")
    Call<Post> savePostOnServer(@FieldMap Map<String, Object> post, @Header("Authorization") String auth);
    @GET("posts/{id}/comments/")
    Call<Commentss> getPostComments(@Path("id") int id,@Header("Authorization") String auth );
    @GET("posts/{id}/comments/")
    Call<Commentss> getPostAllComments(@Path("id") int id,@Header("Authorization") String auth,@Query("limit") int limit );
    @FormUrlEncoded
    @POST("posts/{id}/comments/")
    Call<Comments> addComment(@Path("id") int id,@Header("Authorization") String auth,@FieldMap Map<String, Object> comment);
    @Multipart
    @POST("storage/images/")
    Call<Avatar> loadImage(@Part MultipartBody.Part imageEncoded, @Header("Authorization") String auth);



}
