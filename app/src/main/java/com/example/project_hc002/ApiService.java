package com.example.project_hc002;

import android.widget.EditText;

import com.example.project_hc002.models.Message;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/messages")
    Call<List<Message>> getMessages(@Query("sender") String sender);

    @POST("messages")
    Call<Message> sendMessage(@Body Message message);

    @GET("staff")
    Call<List<Staff>> getAllStaff();

    @GET("staff/{id}")
    Call<Staff> getStaffById(@Path("id") int id);

    @POST("staff")
    Call<Staff> createStaff(@Body Staff staff);

    @PUT("staff/{id}")
    Call<Staff> updateStaff(@Path("id") int id, @Body Staff staff);

    @DELETE("staff/{id}")
    Call<Void> deleteStaff(@Path("id") int id);
    @POST("staff/login")
    Call<com.example.project_hc002.models.LoginResponse> loginStaff(@Body Map<String, String> credentials);
    @POST("peserta/login")
    Call<com.example.project_hc002.models.LoginResponse> loginPeserta(@Body Map<String, String> credentials);

    @POST("post")
    Call<PostResponse> createPost(@Body PostRequest post);

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("getev")
    Call<List<Event>> getEvents();
    //    @GET("getev")
//    Call<List<Event>> getEvent(@Query("page") int page, @Query("size") int size);
    @POST("comments")
    Call<CommentResponse> createComment(@Body CommentRequest request);

    @GET("posts/{postId}/comments")
    Call<List<CommentResponse>> getComments(@Path("postId") int postId);

    @POST("posts/{postId}/like")
    Call<LikeResponse> toggleLike(@Path("postId") int postId, @Body LikeRequest request);

    @GET("posts/{postId}/likes")
    Call<LikeStatusResponse> getLikeStatus(@Path("postId") int postId);

    @PUT("posts/{id}")
    Call<PostResponse> updatePost(@Path("id") int id, @Body UpdatePostRequest request);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);

    @DELETE("posts/{postId}/likes")
    Call<Void> deleteLikesForPost(@Path("postId") int postId);

    @DELETE("posts/{postId}/comments")
    Call<Void> deleteCommentsForPost(@Path("postId") int postId);

    @GET("komunitas")
    Call<List<ComGroup>> getComGroup();

    @POST("join-komunitas")
    Call<JoinResponse> joinKomunitas(@Body JoinRequest joinRequest);

    @POST("/api/leave-komunitas")
    Call<JoinResponse> leaveKomunitas(@Body JoinRequest joinRequest);

    @POST("/api/is-user-joined")
    Call<Boolean> isUserJoined(@Body JoinRequest joinRequest);

    @POST("buat-post")
    Call<PostResponse> createPostKomun(@Body PostComRequest post);

    @GET("getpkomun")
    Call<List<PostKomun>> getPostKomunById(@Query("komunitasId") int komunitasId);

    @POST("commentskomun")
    Call<CommentKomunResponse> createCommentKomun(@Body CommentKomunRequest request, @Query("komunitasId") int komunitasId);

    @GET("getpkomun/{postId}/comments")
    Call<List<CommentKomunResponse>> getCommentsKomun(@Path("postId") int postId, @Query("komunitasId") int komunitasId);

    @POST("getpkomun/{postId}/like")
    Call<LikeKomunResponse> toggleLikes(
            @Path("postId") int postId,
            @Body LikeKomunRequest likeKomunRequest
    );

    @GET("getpkomun/{postId}/likes")
    Call<LikeStatusResponse> getLikesStatus(@Path("postId") int postId);

    @PUT("getpkomun/{id}")
    Call<PostResponse> updatePostKomun(@Path("id") int id, @Body UpdatePostRequest request);

    @DELETE("getpkomun/{id}")
    Call<Void> deletePostKomun(@Path("id") int id);

    @DELETE("getpkomun/{postId}/likes")
    Call<Void> deleteLikesForPostKomun(@Path("postId") int postId);

    @DELETE("getpkomun/{postId}/comments")
    Call<Void> deleteCommentsForPostKomun(@Path("postId") int postId);

    // News endpoints
    @GET("news")
    Call<List<News>> getAllNews();

    @GET("news/{id}")
    Call<News> getNewsById(@Path("id") int id);

    @POST("news")
    Call<News> createNews(@Body News news);

    @PUT("news/{id}")
    Call<News> updateNews(@Path("id") int id, @Body News news);

    @DELETE("news/{id}")
    Call<Void> deleteNews(@Path("id") int id);

    @PUT("api/posts/{postId}/block")
    Call<Void> updateBlockStatus(@Path("postId") int postId, @Body Map<String, String> blockStatus);

    @GET("api/posts/{postId}")
    Call<PostResponse> getPost(@Path("postId") int postId);

    @POST("peserta/register")
    Call<Void> registerPeserta(@Body Map<String, String> userData);

    @GET("peserta/{ktpa}")
    Call<Peserta> getPesertaByKTPA(@Path("ktpa") String ktpa);

    @PUT("peserta/update")
    Call<Void> updatePeserta(@Body Map<String, String> userData);

    @POST("/api/peserta/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body Map<String, String> body);


    //    @POST("posts/{postId}/like")
//    Call<LikeResponse> toggleLike(@Path("postId") int postId, @Body LikeRequest request);
//
//    @GET("posts/{postId}/likes")
//    Call<LikeStatusResponse> getLikeStatus(@Path("postId") int postId);
//
//    @PUT("posts/{id}")
//    Call<PostResponse> updatePost(@Path("id") int id, @Body UpdatePostRequest request);
//
//    @DELETE("posts/{id}")
//    Call<Void> deletePost(@Path("id") int id);
//
//    @DELETE("posts/{postId}/likes")
//    Call<Void> deleteLikesForPost(@Path("postId") int postId);
//
//    @DELETE("posts/{postId}/comments")
//    Call<Void> deleteCommentsForPost(@Path("postId") int postId);

}