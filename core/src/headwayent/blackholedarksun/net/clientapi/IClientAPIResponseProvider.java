/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;

import headwayent.blackholedarksun.net.clientapi.tables.*;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by Sebastian on 10.04.2015.
 */
public interface IClientAPIResponseProvider {

    @GET("/get_session_list/{game_type}")
//    @Headers("Content-type: Application/JSON")
    List<Session> getSessionList(@Path("game_type") int gameType);

    @GET("/get_map_list")
    List<Map> getMapList();

    @POST("/create_account")
    User createUser(@Body User user);

    @POST("/login")
    User login(@Body User user);

    @POST("/create_session/{user_token}")
    Server createSession(@Path("user_token") String userToken, @Body Session session);

    @POST("/join_session/{user_token}")
    Server joinSession(@Path("user_token") String userToken, @Body Session session);

    @POST("/leave_session/{user_token}")
    User leaveSession(@Path("user_token") String userToken);

    @POST("/update_account")
    User updateAccount(@Body User user);

    @POST("/get_friend_id")
    UserFriend getFriendId(@Body UserFriend userFriend);

    @POST("/add_friend")
    UserFriend addFriend(@Path("user_token") String userToken, @Body UserFriend userFriend);

    @GET("/get_friends_list/{user_token}")
    UserFriendList getFriendsList(@Path("user_token") String userToken);

    @GET("/get_friend_invitations/{user_token}")
    UserFriendInvitationList getFriendInvitations(@Path("user_token") String userToken);

    @POST("/accept_invitation")
    UserFriendInvitation acceptFriendInvitation(@Body UserFriendInvitation userFriendInvitation);

    @POST("/create_lobby/{user_token}")
    Lobby createLobby(@Path("user_token") String userToken, @Body List<FriendData> friendDataList);

    // For use by clients who look to join a lobby.
    @GET("/get_lobby_invitations/{user_token}")
    LobbyList getLobbyInvitations(@Path("user_token") String userToken);

    // For use by clients who are in a lobby to see if others have joined.
    // If you have a body then you have a POST.
    @POST("/get_lobby_status/{user_token}")
    LobbyInvitationList getLobbyStatus(@Path("user_token") String userToken, @Body Lobby lobby);

    // Joining a lobby also updates the lobby invitations and should be reflected to other users in lobby.
    @POST("/join_lobby/{user_token}")
    Lobby joinLobby(@Path("user_token") String userToken, @Body Lobby lobby);

    @POST("/exit_lobby/{user_token}")
    Lobby exitLobby(@Path("user_token") String userToken, @Body Lobby lobby);
}
