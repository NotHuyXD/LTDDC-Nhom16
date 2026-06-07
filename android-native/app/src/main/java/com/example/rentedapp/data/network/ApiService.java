package com.example.rentedapp.data.network;

import com.example.rentedapp.data.model.AddBookmarkRequest;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.AuthResponse;
import com.example.rentedapp.data.model.Bookmark;
import com.example.rentedapp.data.model.BookmarkCheckResponse;
import com.example.rentedapp.data.model.CreateRoomRequest;
import com.example.rentedapp.data.model.District;
import com.example.rentedapp.data.model.LoginRequest;
import com.example.rentedapp.data.model.Province;
import com.example.rentedapp.data.model.RegisterRequest;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.model.RoomType;
import com.example.rentedapp.data.model.User;
import com.example.rentedapp.data.model.Ward;
import com.example.rentedapp.data.model.Conversation;
import com.example.rentedapp.data.model.ConversationIdResponse;
import com.example.rentedapp.data.model.CreateConversationRequest;
import com.example.rentedapp.data.model.Message;
import com.example.rentedapp.data.model.SendMessageRequest;
import com.example.rentedapp.data.model.CreateAppointmentRequest;
import com.example.rentedapp.data.model.Appointment;
import com.example.rentedapp.data.model.CreateRentalRequestRequest;
import com.example.rentedapp.data.model.RentalRequest;
import com.example.rentedapp.data.model.AcceptRentalRequestRequest;
import com.example.rentedapp.data.model.UpdateProfileRequest;
import com.example.rentedapp.data.model.ChangePasswordRequest;
import com.example.rentedapp.data.model.Contract;
import com.example.rentedapp.data.model.Invoice;
import com.example.rentedapp.data.model.Payment;
import com.example.rentedapp.data.model.CreateInvoiceRequest;
import com.example.rentedapp.data.model.CreatePaymentRequest;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @GET("auth/profile")
    Call<ApiResponse<User>> getProfile();

    @PUT("auth/profile")
    Call<ApiResponse<Void>> updateProfile(@Body UpdateProfileRequest request);

    @POST("auth/change-password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordRequest request);

    @GET("rooms")
    Call<ApiResponse<List<Room>>> getRooms(@QueryMap Map<String, String> filters);

    @GET("rooms/my-rooms")
    Call<ApiResponse<List<Room>>> getMyRooms();

    @GET("rooms/{id}")
    Call<ApiResponse<Room>> getRoomById(@Path("id") String id);

    @POST("rooms")
    Call<ApiResponse<Room>> createRoom(@Body CreateRoomRequest request);

    @GET("amenities/room-types")
    Call<ApiResponse<List<RoomType>>> getRoomTypes();

    @GET("locations/provinces")
    Call<ApiResponse<List<Province>>> getProvinces();

    @GET("locations/districts")
    Call<ApiResponse<List<District>>> getDistricts(@Query("provinceId") String provinceId);

    @GET("locations/wards")
    Call<ApiResponse<List<Ward>>> getWards(@Query("districtId") String districtId);

    @GET("bookmarks")
    Call<ApiResponse<List<Bookmark>>> getBookmarks();

    @POST("bookmarks")
    Call<ApiResponse<Void>> addBookmark(@Body AddBookmarkRequest request);

    @DELETE("bookmarks/{roomId}")
    Call<ApiResponse<Void>> removeBookmark(@Path("roomId") String roomId);

    @GET("bookmarks/check/{roomId}")
    Call<ApiResponse<BookmarkCheckResponse>> checkBookmark(@Path("roomId") String roomId);

    @GET("chat/conversations")
    Call<ApiResponse<List<Conversation>>> getConversations();

    @POST("chat/conversations")
    Call<ApiResponse<ConversationIdResponse>> getOrCreateConversation(@Body CreateConversationRequest request);

    @GET("chat/conversations/{id}/messages")
    Call<ApiResponse<List<Message>>> getMessages(@Path("id") String conversationId);

    @POST("chat/conversations/{id}/messages")
    Call<ApiResponse<Message>> sendMessage(@Path("id") String conversationId, @Body SendMessageRequest request);

    // Viewing Appointments
    @POST("appointments")
    Call<ApiResponse<Void>> createAppointment(@Body CreateAppointmentRequest request);

    @GET("appointments")
    Call<ApiResponse<List<Appointment>>> getAppointments();

    @PATCH("appointments/{id}/cancel")
    Call<ApiResponse<Void>> cancelAppointment(@Path("id") String id);

    @PATCH("appointments/{id}/confirm")
    Call<ApiResponse<Void>> confirmAppointment(@Path("id") String id);

    // Rental Requests
    @POST("rental-requests")
    Call<ApiResponse<Void>> createRentalRequest(@Body CreateRentalRequestRequest request);

    @GET("rental-requests")
    Call<ApiResponse<List<RentalRequest>>> getRentalRequests();

    @PATCH("rental-requests/{id}/accept")
    Call<ApiResponse<Void>> acceptRentalRequest(@Path("id") String id, @Body AcceptRentalRequestRequest request);

    @PATCH("rental-requests/{id}/reject")
    Call<ApiResponse<Void>> rejectRentalRequest(@Path("id") String id);

    @PATCH("rental-requests/{id}/cancel")
    Call<ApiResponse<Void>> cancelRentalRequest(@Path("id") String id);

    // Contracts
    @GET("contracts")
    Call<ApiResponse<List<Contract>>> getContracts(@Query("status") String status);

    @GET("contracts/{id}")
    Call<ApiResponse<Contract>> getContractById(@Path("id") String id);

    @PATCH("contracts/{id}/sign")
    Call<ApiResponse<Void>> signContract(@Path("id") String id);

    @PATCH("contracts/{id}/terminate")
    Call<ApiResponse<Void>> terminateContract(@Path("id") String id);

    // Invoices
    @GET("payments/invoices")
    Call<ApiResponse<List<Invoice>>> getInvoices(
        @Query("status") String status,
        @Query("contractId") String contractId
    );

    @GET("payments/invoices/{id}")
    Call<ApiResponse<Invoice>> getInvoiceById(@Path("id") String id);

    @POST("payments/invoices")
    Call<ApiResponse<Void>> createInvoice(@Body CreateInvoiceRequest request);

    // Payments
    @POST("payments")
    Call<ApiResponse<Void>> createPayment(@Body CreatePaymentRequest request);

    @GET("payments")
    Call<ApiResponse<List<Payment>>> getPayments();
}


