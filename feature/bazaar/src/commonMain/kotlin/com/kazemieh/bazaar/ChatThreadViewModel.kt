package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.interaction.Message
import com.kazemieh.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatThreadState(
    val messages: AppResult<List<Message>> = AppResult.Loading,
    val input: String = "",
    val sending: Boolean = false,
    val conversationId: Long = 0,
    val myUserId: Long = 0,
)

class ChatThreadViewModel(
    private val interaction: InteractionRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatThreadState())
    val state = _state.asStateFlow()

    private var started = false

    init {
        viewModelScope.launch {
            val res = profileRepository.getProfile()
            if (res is AppResult.Success) _state.update { it.copy(myUserId = res.data.id) }
        }
    }

    /** یا conversationId مستقیم، یا shopId برای ساخت/گرفتنِ گفت‌وگو. */
    fun start(conversationId: Long, shopId: Long) {
        if (started) return
        started = true
        if (conversationId > 0) {
            _state.update { it.copy(conversationId = conversationId) }
            loadMessages()
        } else if (shopId > 0) {
            viewModelScope.launch {
                when (val res = interaction.startConversation(shopId)) {
                    is AppResult.Success -> {
                        _state.update { it.copy(conversationId = res.data.id) }
                        loadMessages()
                    }
                    else -> _state.update { it.copy(messages = AppResult.Error("شروعِ گفت‌وگو ناموفق بود")) }
                }
            }
        }
    }

    fun onInput(value: String) = _state.update { it.copy(input = value) }

    fun refresh() = loadMessages()

    private fun loadMessages() {
        val id = _state.value.conversationId
        if (id <= 0) return
        viewModelScope.launch {
            _state.update { it.copy(messages = interaction.getMessages(id)) }
        }
    }

    fun send() {
        val body = _state.value.input.trim()
        val id = _state.value.conversationId
        if (body.isEmpty() || id <= 0 || _state.value.sending) return
        _state.update { it.copy(sending = true, input = "") }
        viewModelScope.launch {
            interaction.sendMessage(id, body)
            _state.update { it.copy(sending = false) }
            loadMessages()
        }
    }
}
