package com.example.finrecapp.data

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body

interface ApiService {
    @FormUrlEncoded
    @POST("add_transaction.php")
    suspend fun addTransaction(
        @Field("nominal") nominal: Long,
        @Field("keterangan") keterangan: String,
        @Field("jenis") jenis: String,
        @Field("tanggal") tanggal: Long
    ): Response<ApiResponse>

    @FormUrlEncoded
    @POST("add_notification.php")
    suspend fun addNotification(
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("timestamp") timestamp: Long
    ): Response<ApiResponse>

    @FormUrlEncoded
    @POST("login_user.php")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register_user.php")
    suspend fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("full_name") fullName: String,
        @Field("email") email: String
    ): Response<ApiResponse>

    @GET("get_transactions.php")
    suspend fun getTransactions(): Response<List<TransactionEntity>>

    @GET("get_notifications.php")
    suspend fun getNotifications(): Response<List<NotificationEntity>>

    // Business API Endpoints
    @GET("get_products.php")
    suspend fun getProducts(): Response<List<Product>>

    @POST("add_product.php")
    suspend fun addProduct(@Body product: Product): Response<ApiResponse>

    @GET("get_suppliers.php")
    suspend fun getSuppliers(): Response<List<Supplier>>

    @POST("add_supplier.php")
    suspend fun addSupplier(@Body supplier: Supplier): Response<ApiResponse>

    @POST("add_purchase.php")
    suspend fun addPurchase(@Body purchaseData: PurchaseWithItems): Response<ApiResponse>

    @POST("add_sale.php")
    suspend fun addSale(@Body saleData: SaleWithItems): Response<ApiResponse>

    @GET("get_report.php")
    suspend fun getBusinessReport(): Response<BusinessReport>

    @GET("get_purchase_history.php")
    suspend fun getPurchaseHistory(): Response<List<Purchase>>

    @GET("get_sale_history.php")
    suspend fun getSaleHistory(): Response<List<Sale>>
}

data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserEntity?
)

data class PurchaseWithItems(
    val purchase: Purchase,
    val items: List<TransactionItem>
)

data class SaleWithItems(
    val sale: Sale,
    val items: List<TransactionItem>
)
