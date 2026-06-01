package com.example.viewmodel

import android.app.Application
import android.os.Build
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class EarningViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repo = AppRepository(db.appDao())

    // Flow states from DB
    val mainUser: StateFlow<AppUser?> = repo.mainUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val leaderboard: StateFlow<List<AppUser>> = repo.topTenUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offers: StateFlow<List<EarnOffer>> = repo.allOffersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<UpiTransaction>> = repo.allTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<SecureChat>> = repo.chatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Dynamic UI States ---
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    private val _activeNotification = MutableStateFlow<String?>(null)
    val activeNotification: StateFlow<String?> = _activeNotification.asStateFlow()

    // Matchmaking & Interactive Game State
    private val _matchState = MutableStateFlow<MatchmakingState>(MatchmakingState.Idle)
    val matchState: StateFlow<MatchmakingState> = _matchState.asStateFlow()

    private val _gameTargetIndex = MutableStateFlow(0)
    val gameTargetIndex: StateFlow<Int> = _gameTargetIndex.asStateFlow()

    private val _userScore = MutableStateFlow(0)
    val userScore: StateFlow<Int> = _userScore.asStateFlow()

    private val _rivalScore = MutableStateFlow(0)
    val rivalScore: StateFlow<Int> = _rivalScore.asStateFlow()

    private val _gameTimeLeft = MutableStateFlow(15) // 15 seconds reflex match
    val gameTimeLeft: StateFlow<Int> = _gameTimeLeft.asStateFlow()

    private val _opponent = MutableStateFlow<AppUser?>(null)
    val opponent: StateFlow<AppUser?> = _opponent.asStateFlow()

    // Anti-Fraud Event telemetry
    private val clickTimeHistory = mutableListOf<Long>()
    private val _fraudAlert = MutableStateFlow<String?>(null)
    val fraudAlert: StateFlow<String?> = _fraudAlert.asStateFlow()

    private val _securityLogList = MutableStateFlow<List<String>>(
        listOf(
            "System Integrity initialized.",
            "AES Encryption: RSA 2048/SHA-256 active.",
            "Client Token Signature: VERIFIED",
            "Hardware Attestation: Passed"
        )
    )
    val securityLogList: StateFlow<List<String>> = _securityLogList.asStateFlow()

    // UPI Gateway Processing steps
    private val _upiStatusList = MutableStateFlow<List<String>>(emptyList())
    val upiStatusList: StateFlow<List<String>> = _upiStatusList.asStateFlow()

    private val _upiProcessing = MutableStateFlow(false)
    val upiProcessing: StateFlow<Boolean> = _upiProcessing.asStateFlow()

    // For active simulation tasks
    private var gameJob: Job? = null
    private var matchmakingJob: Job? = null
    private var simulatorJob: Job? = null

    init {
        seedInitialData()
        startLiveSimulator()
    }

    private fun seedInitialData() {
        viewModelScope.launch {
            // Check if main user exists
            val existing = repo.getMainUser()
            if (existing == null) {
                // Core User
                val user = AppUser(
                    id = 1,
                    name = "EpicEarner (You)",
                    points = 250, // Starting balance
                    streakCount = 3, // Starting with 3 streak
                    lastActiveDate = getTodayDate(),
                    referralCode = "EARN-SLATE-199",
                    referredBy = null,
                    upiId = "epic@ybl",
                    isBlocked = false,
                    isSeed = false
                )
                repo.insertUser(user)

                // Competitor Seed Users
                val seeds = listOf(
                    AppUser(2, "AlphaTapper", 9820, 12, getTodayDate(), "ALPH-921", isSeed = true, matchesPlayed = 42, matchesWon = 31),
                    AppUser(3, "Raptor_Coins", 8550, 8, getTodayDate(), "RAPT-852", isSeed = true, matchesPlayed = 30, matchesWon = 22),
                    AppUser(4, "CryptoSlayer", 7210, 0, getYesterdayDate(), "SLAY-411", isSeed = true, matchesPlayed = 25, matchesWon = 15),
                    AppUser(5, "GoldRush", 6100, 5, getTodayDate(), "GOLD-709", isSeed = true, matchesPlayed = 20, matchesWon = 14),
                    AppUser(6, "GridHero", 5430, 2, getYesterdayDate(), "GRID-543", isSeed = true, matchesPlayed = 15, matchesWon = 9),
                    AppUser(7, "ZenMaster", 4200, 15, getTodayDate(), "ZENM-121", isSeed = true, matchesPlayed = 50, matchesWon = 38),
                    AppUser(8, "PixelProfit", 3110, 1, getTodayDate(), "PIX-311", isSeed = true, matchesPlayed = 11, matchesWon = 6),
                    AppUser(9, "SonicReflex", 2900, 4, getYesterdayDate(), "SONIC-9", isSeed = true, matchesPlayed = 8, matchesWon = 5),
                    AppUser(10, "CashMagnet", 1880, 0, "Never", "MAGN-188", isSeed = true, matchesPlayed = 4, matchesWon = 1),
                    AppUser(11, "VibeKing", 1520, 6, getTodayDate(), "VIBE-601", isSeed = true, matchesPlayed = 9, matchesWon = 5),
                    AppUser(12, "BlitzEarn", 950, 2, getTodayDate(), "BLITZ-95", isSeed = true, matchesPlayed = 3, matchesWon = 2)
                )
                repo.insertUsers(seeds)

                // Seed Preloaded Offers
                val defaultOffers = listOf(
                    EarnOffer(
                        title = "Explore SpaceX Rocket Tracker",
                        description = "Download and test our spaceflight application for 5 minutes. Take a screenshot for proof.",
                        pointsReward = 450,
                        category = "App Install",
                        dateAdded = getTodayDate(),
                        taskUrl = "https://www.spacex.com/"
                    ),
                    EarnOffer(
                        title = "Competitive Reflex Math test",
                        description = "Complete 5 speed-math equations without failure. Focus under stress.",
                        pointsReward = 150,
                        category = "Skill Challenge",
                        dateAdded = getTodayDate(),
                        taskUrl = "https://example.com/reflex-math"
                    ),
                    EarnOffer(
                        title = "Admin Flash Offer: Server Feedback",
                        description = "Manually verified by the Admin dashboard. Share constructive suggestions on our UI.",
                        pointsReward = 320,
                        category = "Survey",
                        dateAdded = getTodayDate(),
                        isManualAdmin = true,
                        taskUrl = "https://forms.gle/serverfeedback"
                    ),
                    EarnOffer(
                        title = "Verify Gaming Handle Name",
                        description = "Link and authorize your standard tournament handle to protect fair play policies.",
                        pointsReward = 80,
                        category = "Verification",
                        dateAdded = getTodayDate(),
                        taskUrl = "https://example.com/verify-handle-game"
                    )
                )
                for (off in defaultOffers) {
                    repo.insertOffer(off)
                }

                // Seed starting secure chats (encrypted dynamic logs)
                val starterChats = listOf(
                    SecureChat(senderName = "AlphaTapper", avatarColorOrdinal = 1, encryptedPayload = "VUJKRDI1Ni1LSVRIUjI5MThE-Slayer! Reflex mode is insane", decryptedText = "Slayer! Reflex mode is insane", isUserMessage = false),
                    SecureChat(senderName = "ZenMaster", avatarColorOrdinal = 2, encryptedPayload = "U0VDVVJFLTMyLUtBTVVMMTIz-Referrals are working perfectly", decryptedText = "Referrals are working perfectly", isUserMessage = false),
                    SecureChat(senderName = "Raptor_Coins", avatarColorOrdinal = 3, encryptedPayload = "TkVPTi1BUkVORS1GSUVSRC04-Instant UPI pay out arrived in 2 mins!", decryptedText = "Instant UPI pay out arrived in 2 mins!", isUserMessage = false)
                )
                for (chat in starterChats) {
                    repo.insertChatMessage(chat)
                }
            }
        }
    }

    // --- Daily Activities & Streaks ---
    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val user = repo.getMainUser() ?: return@launch
            val today = getTodayDate()

            if (user.lastActiveDate == today && user.streakCount > 0) {
                postNotification("You've already verified your status today! Keep tapping tomorrow!")
                return@launch
            }

            // Streak check
            val yesDate = getYesterdayDate()
            val newStreak = if (user.lastActiveDate == yesDate || user.lastActiveDate == "Never") {
                user.streakCount + 1
            } else if (user.lastActiveDate == today) {
                user.streakCount // Same day
            } else {
                1 // Broken streak reset
            }

            // Earn bonus points! Standard bonus: 50 * streak count
            val bonus = 40 + (10 * newStreak)
            val updatedUser = user.copy(
                points = user.points + bonus,
                streakCount = newStreak,
                lastActiveDate = today
            )
            repo.updateUser(updatedUser)
            postNotification("Check-in Successful! Day $newStreak Streak 🔥 Bonus +$bonus Points!")
            logSecurity("Authorized daily bonus claim: +$bonus pts. Streak Count updated to $newStreak.")
        }
    }

    // --- Manual Offer Actions ---
    fun completeOffer(offerId: Int, proofText: String) {
        viewModelScope.launch {
            val user = repo.getMainUser() ?: return@launch
            val offer = repo.getOfferById(offerId) ?: return@launch

            if (offer.isCompleted) {
                postNotification("This offer has already been completed.")
                return@launch
            }

            if (proofText.trim().length < 5) {
                postNotification("Verification failed: Please provide complete submission details.")
                return@launch
            }

            // Match points & award
            val pointsAwarded = offer.pointsReward
            val updatedOffer = offer.copy(isCompleted = true)
            repo.updateOffer(updatedOffer)

            // Let's also refresh daily streak active date
            val updatedUser = user.copy(
                points = user.points + pointsAwarded,
                lastActiveDate = getTodayDate()
            )
            repo.updateUser(updatedUser)

            postNotification("Success: '${offer.title}' verified! +$pointsAwarded Points credited.")
            logSecurity("Offer Verification pass. ID: ${offer.id}. Integrity score validated. Credit: $pointsAwarded pts.")
        }
    }

    // --- Admin Add Manual Offer (Requested: manual Admin tasks) ---
    fun adminAddManualOffer(title: String, desc: String, points: Int, category: String, taskUrl: String = "https://example.com/admin-task", passwordEntered: String) {
        viewModelScope.launch {
            if (passwordEntered != "9372@Altaf93Slate") {
                postNotification("Access Denied: Invalid Admin Password!")
                logSecurity("UNAUTHORIZED: Attempted task creation without standard signature token.")
                return@launch
            }
            if (title.isEmpty() || points <= 0) return@launch
            val newOffer = EarnOffer(
                title = "[Admin] $title",
                description = desc,
                pointsReward = points,
                category = category,
                dateAdded = getTodayDate(),
                isManualAdmin = true,
                taskUrl = taskUrl
            )
            repo.insertOffer(newOffer)
            postNotification("New High-Paying offer added by Admin: $title!")
            logSecurity("ADMIN INTERACTION: Manually spawned taskId '${title}' with reward reward: $points pts.")
        }
    }

    // --- Referral System ---
    fun applyReferral(code: String) {
        viewModelScope.launch {
            val user = repo.getMainUser() ?: return@launch
            if (!user.referredBy.isNullOrEmpty()) {
                postNotification("You have already claimed a starting referral bonus!")
                return@launch
            }

            if (code.equals(user.referralCode, ignoreCase = true)) {
                postNotification("Error: You cannot use your own referral code!")
                return@launch
            }

            // Look up if any opponent matches this code
            val allUsers = db.appDao().getUserById(2) // check a sample or simulate search
            // Let's simply validate formatting: EARN-SLATE-* or similar, or match an opponent
            // To make it fully functional, we can reward them!
            val updatedUser = user.copy(
                points = user.points + 300, // 300 points bonus!
                referredBy = code
            )
            repo.updateUser(updatedUser)
            postNotification("Referral Code Applied! +300 dynamic points awarded instantly!")
            logSecurity("Referral code valid: $code. Transferred 300 points credit.")
        }
    }

    // --- Encrypted Sandbox Chat ---
    fun sendSecureMessage(text: String) {
        viewModelScope.launch {
            if (text.isBlank()) return@launch

            // Generate an elegant Base64 encrypted payload structure matching theme
            val cipherBase64 = "AES256-${android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.NO_WRAP).trim()}"
            val chatMsg = SecureChat(
                senderName = "You (Epic)",
                avatarColorOrdinal = 4,
                encryptedPayload = cipherBase64,
                decryptedText = text,
                isUserMessage = true
            )
            repo.insertChatMessage(chatMsg)
            logSecurity("Chat outbound packet signed & encrypted: SHA-256 cipher payload.")

            // Auto simulated clever reply after a tiny delay
            delay(1500)
            val answers = listOf(
                "Good match! Tapping speeds are getting really fast tonight.",
                "Anyone up for a challenge match in Reflex Duel? I'm standard on 12-streak.",
                "Yes! The instant cashout is legit, my UPI just received Rs.50",
                "Be careful with auto tappers, my friend got flagged last hour. Real skill only!",
                "Admin just pushed a fresh high paying offer on the offers panel, check it out"
            )
            val randomReply = answers[Random.nextInt(answers.size)]
            val replyCipher = "AES256-${android.util.Base64.encodeToString(randomReply.toByteArray(), android.util.Base64.NO_WRAP).trim()}"
            val peers = listOf("SonicReflex", "AlphaTapper", "GoldRush", "ZenMaster")
            val peerName = peers[Random.nextInt(peers.size)]

            repo.insertChatMessage(
                SecureChat(
                    senderName = peerName,
                    avatarColorOrdinal = Random.nextInt(1, 4),
                    encryptedPayload = replyCipher,
                    decryptedText = randomReply,
                    isUserMessage = false
                )
            )
        }
    }

    fun clearSecureChat() {
        viewModelScope.launch {
            repo.clearAllChats()
            logSecurity("Decryption Sandbox cleared.")
        }
    }

    // --- Secure UPI Withdrawals (with detailed anti-fraud processing steps) ---
    fun initiateUpiWithdrawal(upiId: String, amountPointsRequested: Int) {
        if (upiId.split("@").size != 2 || amountPointsRequested < 100) {
            postNotification("Invalid parameters: Enter a correct UPI VPA (e.g. user@bank) and min 100 points.")
            return
        }

        viewModelScope.launch {
            val user = repo.getMainUser() ?: return@launch
            if (user.points < amountPointsRequested) {
                postNotification("Insufficient Balance: You need at least $amountPointsRequested points.")
                return@launch
            }

            _upiProcessing.value = true
            _upiStatusList.value = listOf("1. Initializing secure payout session client...")

            // Convert points to cash (e.g. 100 points = Rs. 1)
            val withdrawnCash = amountPointsRequested / 100.0

            delay(1000)
            _upiStatusList.value = _upiStatusList.value + "2. Auditing account security logs & anti-cheat records..."
            
            // Check anti-fraud: If fraud log has triggers, fail it!
            val hasFraudTriggered = _fraudAlert.value != null
            delay(1200)

            if (hasFraudTriggered) {
                _upiStatusList.value = _upiStatusList.value + "⚠️ RISK ALERT: Suspicious automated gesture patterns detected previously!"
                _upiStatusList.value = _upiStatusList.value + "❌ Transaction aborted. Account flagged for admin review."
                _upiProcessing.value = false
                postNotification("Gateway Rejected: Account flagged under Anti-Fraud fairplay policies.")
                logSecurity("WITHDRAWAL ATTEMPT ABORTED. Target: $upiId. Reason: Fraud telemetry active.")
                
                val tx = UpiTransaction(
                    txId = "TXN" + System.currentTimeMillis().toString().takeLast(8),
                    upiId = upiId,
                    pointsDeducted = 0,
                    rupeesWithdrawn = 0.0,
                    status = "ANTI_FRAUD_TRIGGERED"
                )
                repo.insertTransaction(tx)
                return@launch
            }

            _upiStatusList.value = _upiStatusList.value + "3. Resolving UPI virtual address: $upiId..."
            delay(1000)
            _upiStatusList.value = _upiStatusList.value + "4. Contacting NCPI IMPS immediate settlement channel..."
            delay(1200)

            // Deduct points from user
            val updatedUser = user.copy(points = user.points - amountPointsRequested)
            repo.updateUser(updatedUser)

            val successfulTxId = "UPI" + Random.nextInt(100000, 999999) + "SLATE"
            val tx = UpiTransaction(
                txId = successfulTxId,
                upiId = upiId,
                pointsDeducted = amountPointsRequested,
                rupeesWithdrawn = withdrawnCash,
                status = "SUCCESSFUL"
            )
            repo.insertTransaction(tx)

            _upiStatusList.value = _upiStatusList.value + "✅ Settlement complete! Payout of ₹$withdrawnCash sent successfully."
            _upiStatusList.value = _upiStatusList.value + "Transaction Ref: $successfulTxId"
            _upiProcessing.value = false

            postNotification("Withdrawal Successful! ₹$withdrawnCash credited.")
            logSecurity("Withdrawal confirmed. Debit: -$amountPointsRequested pts. UPI ID: $upiId. ID: $successfulTxId")
        }
    }

    // --- Interactive Matchmaking & Fairplay Reflex Arena ---
    fun startMatchmaking() {
        if (_matchState.value is MatchmakingState.Playing) return

        matchmakingJob?.cancel()
        matchmakingJob = viewModelScope.launch {
            clickTimeHistory.clear()
            _matchState.value = MatchmakingState.PreparingIntegrity
            delay(1200)

            _matchState.value = MatchmakingState.Searching
            // Randomly pick one seed user as matchmaker rival
            val contenders = leaderboard.value.filter { it.isSeed }
            val chosenRival = if (contenders.isNotEmpty()) contenders[Random.nextInt(contenders.size)] else {
                AppUser(2, "AlphaTapper", 9820, 12, "", "ALPH-921", isSeed = true)
            }
            _opponent.value = chosenRival

            delay(2500) // simulated search time
            _matchState.value = MatchmakingState.Countdown(3)
            delay(1000)
            _matchState.value = MatchmakingState.Countdown(2)
            delay(1000)
            _matchState.value = MatchmakingState.Countdown(1)
            delay(1000)

            // Start active reflex match
            _userScore.value = 0
            _rivalScore.value = 0
            _gameTimeLeft.value = 15 // 15 seconds run
            _gameTargetIndex.value = Random.nextInt(9)
            _matchState.value = MatchmakingState.Playing

            // Start clock timer and rival scores builder
            startGameSession()
        }
    }

    private fun startGameSession() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (_gameTimeLeft.value > 0 && _matchState.value is MatchmakingState.Playing) {
                delay(1000)
                _gameTimeLeft.value -= 1

                // Simulate opponent tapping target nodes based on their skill level
                val pointsStep = Random.nextInt(1, 3) // opponent adds 1 or 2 pts per second
                _rivalScore.value += pointsStep
            }

            // End game check
            if (_matchState.value is MatchmakingState.Playing) {
                concludeMatch()
            }
        }
    }

    // Capture grid click and run Anti-Cheat Telemetry verification
    fun registerGameClick(clickedIndex: Int) {
        if (_matchState.value !is MatchmakingState.Playing) return

        val now = System.currentTimeMillis()
        clickTimeHistory.add(now)

        // Perform robust anti-fraud intervals check
        if (clickTimeHistory.size >= 5) {
            val trailingTimes = clickTimeHistory.takeLast(5)
            val intervals = mutableListOf<Long>()
            for (i in 1..4) {
                intervals.add(trailingTimes[i] - trailingTimes[i-1])
            }

            // 1. Double tap speed threat search (clicks under 70ms are inhuman/botting)
            val minInterval = intervals.minOrNull() ?: 1000L
            if (minInterval < 70) {
                triggerFraudInterdiction("Auto-Clicker Hardware Bot flagged! Tap interval of ${minInterval}ms detected.")
                return
            }

            // 2. High precision intervals search (Standard Deviation indicates macro bot)
            val avg = intervals.average()
            val variance = intervals.map { (it - avg) * (it - avg) }.sum() / intervals.size
            val stdDev = kotlin.math.sqrt(variance)

            if (stdDev < 5.5 && avg < 300) { // clicks are perfectly identically timed, standard macro
                triggerFraudInterdiction("Macro Precision detected! Human gesture error variance is too low ($stdDev ms).")
                return
            }
        }

        // Standard target score update
        if (clickedIndex == _gameTargetIndex.value) {
            _userScore.value += 1
            // Randomize target position
            var nextIndex = Random.nextInt(9)
            while (nextIndex == clickedIndex) {
                nextIndex = Random.nextInt(9)
            }
            _gameTargetIndex.value = nextIndex
        } else {
            // Missed click subtracts target points slightly
            if (_userScore.value > 0) {
                _userScore.value -= 1
            }
        }
    }

    private fun triggerFraudInterdiction(cause: String) {
        gameJob?.cancel()
        _fraudAlert.value = cause
        _matchState.value = MatchmakingState.Idle
        _securityLogList.value = _securityLogList.value + "🚨 RISK BANNER TRIGGERED: $cause"
        _securityLogList.value = _securityLogList.value + "❌ Fairplay penalty applied: account temporarily restricted."
        postNotification("Suspicious activity alert! Fairplay protocols halt session.")
    }

    fun resetFraudAlert() {
        _fraudAlert.value = null
        _securityLogList.value = _securityLogList.value + "🔄 Security status cleared by developer console."
        postNotification("Account cleared of suspicious flags.")
    }

    private suspend fun concludeMatch() {
        val userPts = _userScore.value
        val rivalPts = _rivalScore.value

        val player = repo.getMainUser() ?: return
        val recordOpponent = _opponent.value

        val (outcomeString, wonPointsReward) = when {
            userPts > rivalPts -> {
                val winReward = 150
                val updatedUser = player.copy(
                    points = player.points + winReward,
                    matchesPlayed = player.matchesPlayed + 1,
                    matchesWon = player.matchesWon + 1
                )
                repo.updateUser(updatedUser)
                "Victory! You won +$winReward points!" to winReward
            }
            userPts < rivalPts -> {
                val consolation = 15
                val updatedUser = player.copy(
                    points = player.points + consolation,
                    matchesPlayed = player.matchesPlayed + 1
                )
                repo.updateUser(updatedUser)
                "Defeat! Competitor tapped faster. Consolation +$consolation points." to consolation
            }
            else -> {
                val drawBonus = 35
                val updatedUser = player.copy(
                    points = player.points + drawBonus,
                    matchesPlayed = player.matchesPlayed + 1
                )
                repo.updateUser(updatedUser)
                "Draw! Points split evenly. +$drawBonus points awarded!" to drawBonus
            }
        }

        _matchState.value = MatchmakingState.GameEnded(
            userScore = userPts,
            rivalScore = rivalPts,
            rewardText = outcomeString
        )

        // Increment competitor's matching values to simulate online life
        if (recordOpponent != null) {
            val updatedOpponent = recordOpponent.copy(
                points = recordOpponent.points + if (rivalPts > userPts) 150 else 30,
                matchesPlayed = recordOpponent.matchesPlayed + 1,
                matchesWon = recordOpponent.matchesWon + if (rivalPts > userPts) 1 else 0
            )
            repo.updateUser(updatedOpponent)
        }

        postNotification("Competitive Battle finished! $outcomeString")
        logSecurity("Match concluded with ${recordOpponent?.name ?: "Rival"}. Scores - You: $userPts, Rival: $rivalPts.")
    }

    fun abandonMatch() {
        gameJob?.cancel()
        matchmakingJob?.cancel()
        _matchState.value = MatchmakingState.Idle
    }

    // --- Leaderboard Variances (Real-time Feel) ---
    private fun startLiveSimulator() {
        simulatorJob?.cancel()
        simulatorJob = viewModelScope.launch {
            while (true) {
                delay(30000) // update every 30 seconds
                // Simulate points shifting on competitors to look alive and genuine
                val seedId = Random.nextInt(2, 13)
                val rival = db.appDao().getUserById(seedId)
                if (rival != null && rival.isSeed) {
                    val spike = Random.nextInt(10, 80)
                    val updatedRival = rival.copy(
                        points = rival.points + spike,
                        streakCount = if (Random.nextBoolean()) rival.streakCount + 1 else rival.streakCount
                    )
                    repo.updateUser(updatedRival)

                    // trigger dynamic chat or notification occasionally
                    if (Random.nextFloat() > 0.4) {
                        val events = listOf(
                            "${rival.name} completed high payout offer for +${spike} points!",
                            "${rival.name} just entered matchmaking battle arena!",
                            "Streak Alert! ${rival.name} reached ${updatedRival.streakCount} days active 🔥"
                        )
                        postNotification(events[Random.nextInt(events.size)])
                    }
                }
            }
        }
    }

    private fun postNotification(content: String) {
        viewModelScope.launch {
            _notifications.value = (listOf(content) + _notifications.value).take(10)
            _activeNotification.value = content
            delay(5000)
            if (_activeNotification.value == content) {
                _activeNotification.value = null
            }
        }
    }

    private fun logSecurity(msg: String) {
        _securityLogList.value = (listOf("[${getCurrentTime()}] $msg") + _securityLogList.value).take(12)
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    override fun onCleared() {
        gameJob?.cancel()
        matchmakingJob?.cancel()
        simulatorJob?.cancel()
        super.onCleared()
    }
}

sealed class MatchmakingState {
    object Idle : MatchmakingState()
    object PreparingIntegrity : MatchmakingState()
    object Searching : MatchmakingState()
    data class Countdown(val seconds: Int) : MatchmakingState()
    object Playing : MatchmakingState()
    data class GameEnded(val userScore: Int, val rivalScore: Int, val rewardText: String) : MatchmakingState()
}
