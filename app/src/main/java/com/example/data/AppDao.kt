package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Users (Main user & Leaders)
    @Query("SELECT * FROM app_users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Int): Flow<AppUser?>

    @Query("SELECT * FROM app_users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): AppUser?

    @Query("SELECT * FROM app_users ORDER BY points DESC LIMIT 10")
    fun getTopTenUsersFlow(): Flow<List<AppUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AppUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<AppUser>)

    @Update
    suspend fun updateUser(user: AppUser)

    // Offers
    @Query("SELECT * FROM earn_offers ORDER BY id DESC")
    fun getAllOffersFlow(): Flow<List<EarnOffer>>

    @Query("SELECT * FROM earn_offers WHERE id = :offerId LIMIT 1")
    suspend fun getOfferById(offerId: Int): EarnOffer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: EarnOffer)

    @Update
    suspend fun updateOffer(offer: EarnOffer)

    // UPI Transactions
    @Query("SELECT * FROM upi_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<UpiTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: UpiTransaction)

    // Secure Encrypted Chat messages
    @Query("SELECT * FROM secure_chats ORDER BY timestamp ASC")
    fun getChatMessagesFlow(): Flow<List<SecureChat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: SecureChat)

    @Query("DELETE FROM secure_chats")
    suspend fun clearAllChats()
}
