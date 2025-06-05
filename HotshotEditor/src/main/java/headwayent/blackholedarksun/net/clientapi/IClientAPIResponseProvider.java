package headwayent.blackholedarksun.net.clientapi;

import headwayent.blackholedarksun.net.clientapi.tables.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Sebastian on 10.04.2015.
 * When using retrofit2 the idiots creating it switched the / behavior. Now here we don't have leading / but we have it in the
 * URL when creating the restAdapter.
 */
public interface IClientAPIResponseProvider {

    @POST("create_account")
    User createUser(@Body User user);

    @POST("login")
    User login(@Body User user);

//    @POST("/create_session/{user_token}")
//    Server createSession(@Path("user_token") String userToken, @Body Session session);
//
//    @POST("/join_session/{user_token}")
//    Server joinSession(@Path("user_token") String userToken, @Body Session session);

    @POST("leave_session/{user_token}")
    User leaveSession(@Path("user_token") String userToken);

    @POST("update_account")
    User updateAccount(@Body User user);

    @POST("update_gps_position/{user_token}/{long}/{lat}")
    Call<TransportStatus> updatePosition(
            @Path("user_token") String userToken,
            @Path("long") double longitude,
            @Path("lat") double latitude);

    @POST("update_gps_position_test/{user_token}/{long}/{lat}/{transport_taken}")
    Call<GenericTransient> updatePositionTest(
            @Path("user_token") String userToken,
            @Path("long") double longitude,
            @Path("lat") double latitude,
            @Path("transport_taken") boolean transportTaken);

    // For testing
    @POST("clear_session_data")
    Call<GenericTransient> clearSessionData();
}
