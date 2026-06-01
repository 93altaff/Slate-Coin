package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_users")
data class AppUser(
    @PrimaryKey val id: Int,
    val name: String,
    val points: Int,
    val streakCount: Int,
    val lastActiveDate: String, // format: "yyyy-MM-dd" or "Never"
    val referralCode: String,
    val referredBy: String? = null,
    val upiId: String? = null,
    val isBlocked: Boolean = false,
    val isSeed: Boolean = false,
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0
)

@Entity(tableName = "earn_offers")
data class EarnOffer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val category: String, // "Offerwall", "Survey", "Admin", "Reflex Game"
    val isCompleted: Boolean = false,
    val dateAdded: String,
    val isManualAdmin: Boolean = false,
    val taskUrl: String = "https://example.com/start-task"
)

@Entity(tableName = "upi_transactions")
data class UpiTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val txId: String,
    val upiId: String,
    val pointsDeducted: Int,
    val rupeesWithdrawn: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String // "PROCESSING", "SUCCESSUFUL", "FAILED_REJECTED", "ANTI_FRAUD_TRIGGERED"
)

@Entity(tableName = "secure_chats")
data class SecureChat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val avatarColorOrdinal: Int, // for visual styling
    val encryptedPayload: String, // encrypted (simulated AES-256 Base64 representation)
    val decryptedText: String, // original text (for decryption sandbox visualization)
    val timestamp: Long = System.currentTimeMillis(),
    val isUserMessage: Boolean = false
)
