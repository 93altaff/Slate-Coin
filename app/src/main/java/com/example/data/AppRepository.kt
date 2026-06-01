package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val mainUserFlow: Flow<AppUser?> = appDao.getUserByIdFlow(1)
    val topTenUsersFlow: Flow<List<AppUser>> = appDao.getTopTenUsersFlow()
    val allOffersFlow: Flow<List<EarnOffer>> = appDao.getAllOffersFlow()
    val allTransactionsFlow: Flow<List<UpiTransaction>> = appDao.getAllTransactionsFlow()
    val chatMessagesFlow: Flow<List<SecureChat>> = appDao.getChatMessagesFlow()

    suspend fun getMainUser(): AppUser? = appDao.getUserById(1)

    suspend fun insertUser(user: AppUser) = appDao.insertUser(user)

    suspend fun insertUsers(users: List<AppUser>) = appDao.insertUsers(users)

    suspend fun updateUser(user: AppUser) = appDao.updateUser(user)

    suspend fun getOfferById(id: Int): EarnOffer? = appDao.getOfferById(id)

    suspend fun insertOffer(offer: EarnOffer) = appDao.insertOffer(offer)

    suspend fun updateOffer(offer: EarnOffer) = appDao.updateOffer(offer)

    suspend fun insertTransaction(transaction: UpiTransaction) = appDao.insertTransaction(transaction)

    suspend fun insertChatMessage(message: SecureChat) = appDao.insertChatMessage(message)

    suspend fun clearAllChats() = appDao.clearAllChats()
}
