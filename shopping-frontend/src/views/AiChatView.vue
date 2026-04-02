<template>
  <div class="ai-chat-page">
    <!-- Left: Sessions Panel -->
    <div class="sessions-panel">
      <div class="sessions-header">
        <span class="panel-title">AI 助手</span>
        <el-button type="danger" size="small" @click="createNewSession">
          <el-icon><Plus /></el-icon> 新建对话
        </el-button>
      </div>

      <el-button class="recommend-btn" @click="showRecommendDialog = true">
        <el-icon><MagicStick /></el-icon> 获取商品推荐
      </el-button>

      <div class="sessions-list">
        <div
          v-for="session in sessions"
          :key="session.sessionId"
          class="session-item"
          :class="{ active: currentSessionId === session.sessionId }"
          @click="switchSession(session)"
        >
          <el-icon class="session-icon"><ChatDotRound /></el-icon>
          <div class="session-info">
            <div class="session-title">{{ session.title }}</div>
            <div class="session-time">{{ formatTime(session.createTime) }}</div>
          </div>
          <el-icon class="delete-icon" @click.stop="deleteSession(session.sessionId)">
            <Delete />
          </el-icon>
        </div>
        <div class="sessions-empty" v-if="sessions.length === 0">
          点击上方按钮开始对话
        </div>
      </div>
    </div>

    <!-- Right: Chat Window -->
    <div class="chat-panel">
      <!-- Chat Header -->
      <div class="chat-header">
        <div class="chat-title">
          <el-icon :size="20" color="#c0392b"><ChatDotRound /></el-icon>
          <span>{{ currentSession?.title || 'AI 购物助手' }}</span>
        </div>
        <div class="chat-controls">
          <span class="stream-label">流式对话</span>
          <el-switch v-model="streamMode" active-color="#c0392b" />
          <el-button size="small" link @click="clearCurrentHistory" v-if="currentMessages.length">
            <el-icon><Delete /></el-icon> 清空
          </el-button>
        </div>
      </div>

      <!-- Messages -->
      <div class="messages-area" ref="messagesAreaRef">
        <div class="welcome-msg" v-if="currentMessages.length === 0 && !currentSessionId">
          <el-icon :size="60" color="#c0392b"><ChatDotRound /></el-icon>
          <h3>你好！我是潮流商城 AI 助手</h3>
          <p>我可以帮您推荐商品、解答购物问题、分析商品对比等</p>
          <div class="quick-prompts">
            <el-button
              v-for="q in quickPrompts"
              :key="q"
              size="small"
              round
              @click="sendQuickPrompt(q)"
            >{{ q }}</el-button>
          </div>
        </div>

        <div
          v-for="(msg, idx) in currentMessages"
          :key="idx"
          class="message-row"
          :class="msg.role === 'user' ? 'user-row' : 'ai-row'"
        >
          <!-- AI Avatar -->
          <div class="avatar ai-avatar" v-if="msg.role !== 'user'">
            <el-icon :size="18" color="#fff"><ChatDotRound /></el-icon>
          </div>

          <div class="bubble-wrap">
            <div
              class="bubble"
              :class="msg.role === 'user' ? 'user-bubble' : 'ai-bubble'"
            >
              <div class="msg-content" v-html="renderContent(msg.content)"></div>
              <div class="typing-cursor" v-if="msg.isTyping"></div>
            </div>
            <div class="msg-time">{{ formatTime(msg.time) }}</div>
          </div>

          <!-- User Avatar -->
          <div class="avatar user-avatar" v-if="msg.role === 'user'">
            <el-icon :size="18" color="#fff"><UserFilled /></el-icon>
          </div>
        </div>

        <!-- Loading indicator -->
        <div class="loading-row ai-row" v-if="chatLoading">
          <div class="avatar ai-avatar">
            <el-icon :size="18" color="#fff"><ChatDotRound /></el-icon>
          </div>
          <div class="bubble ai-bubble loading-bubble">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <div class="input-box">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            placeholder="输入您的问题，例如：推荐一款性价比高的手机..."
            resize="none"
            @keydown.enter.exact.prevent="handleSend"
            @keydown.enter.shift.exact="() => {}"
          />
          <div class="input-actions">
            <span class="input-hint">Enter 发送，Shift+Enter 换行</span>
            <el-button
              type="danger"
              :loading="chatLoading"
              :disabled="!inputText.trim()"
              @click="handleSend"
            >
              <el-icon><Promotion /></el-icon>
              发 送
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Recommend Dialog -->
    <el-dialog
      v-model="showRecommendDialog"
      title="AI 商品推荐"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form :model="recommendForm" label-width="80px">
        <el-form-item label="商品类别">
          <el-input v-model="recommendForm.category" placeholder="例如：手机、笔记本电脑、运动鞋" clearable />
        </el-form-item>
        <el-form-item label="预算上限">
          <el-input-number
            v-model="recommendForm.budget"
            :min="0"
            :max="100000"
            :step="100"
            placeholder="请输入预算"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="偏好需求">
          <el-input
            v-model="recommendForm.preference"
            type="textarea"
            :rows="2"
            placeholder="例如：续航好、轻薄、游戏性能强..."
          />
        </el-form-item>
      </el-form>

      <div class="recommend-result" v-if="recommendResult">
        <div class="result-title">AI 推荐结果：</div>
        <div class="result-content" v-html="renderContent(recommendResult)"></div>
      </div>
      <div class="recommend-loading" v-if="recommendLoading">
        <el-icon class="loading-icon"><Loading /></el-icon> AI 正在分析中...
      </div>

      <template #footer>
        <el-button @click="showRecommendDialog = false">关闭</el-button>
        <el-button type="danger" @click="fetchRecommendations" :loading="recommendLoading">
          <el-icon><MagicStick /></el-icon> 获取推荐
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { chat, getChatHistory, clearHistory, getRecommendations } from '@/api/ai'

const messagesAreaRef = ref(null)
const inputText = ref('')
const streamMode = ref(true)
const chatLoading = ref(false)
const showRecommendDialog = ref(false)
const recommendLoading = ref(false)
const recommendResult = ref('')

const sessions = ref([])
const currentSessionId = ref('')
const currentMessages = ref([])

const recommendForm = reactive({
  category: '',
  budget: 1000,
  preference: ''
})

const quickPrompts = [
  '推荐一款性价比高的手机',
  '最近有什么爆款商品？',
  '1000元以内的蓝牙耳机推荐',
  '如何选购笔记本电脑？'
]

const currentSession = computed(() =>
  sessions.value.find(s => s.sessionId === currentSessionId.value)
)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diffDays = Math.floor((now - d) / 86400000)
  if (diffDays === 0) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

const renderContent = (text) => {
  if (!text) return ''
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/`(.+?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br>')
    .replace(/#{1,3}\s(.+)/g, '<strong>$1</strong>')
    .replace(/^\d+\.\s(.+)/gm, '<span>• $1</span>')
    .replace(/^[-*]\s(.+)/gm, '<span>• $1</span>')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesAreaRef.value) {
    messagesAreaRef.value.scrollTop = messagesAreaRef.value.scrollHeight
  }
}

const createNewSession = () => {
  const sessionId = `session_${Date.now()}`
  const session = {
    sessionId,
    title: '新对话',
    messages: [],
    createTime: Date.now()
  }
  sessions.value.unshift(session)
  currentSessionId.value = sessionId
  currentMessages.value = []
}

const switchSession = async (session) => {
  currentSessionId.value = session.sessionId
  currentMessages.value = session.messages || []
  if (session.messages.length === 0) {
    try {
      const res = await getChatHistory(session.sessionId)
      const history = res.data || []
      const msgs = history.map(h => ({
        role: h.role,
        content: h.content,
        time: h.createTime || Date.now()
      }))
      session.messages = msgs
      currentMessages.value = msgs
    } catch {
      // ignore
    }
  }
  scrollToBottom()
}

const deleteSession = async (sessionId) => {
  await ElMessageBox.confirm('确定要删除该对话吗？', '提示', { type: 'warning' })
  try {
    await clearHistory(sessionId)
  } catch {
    // ignore
  }
  sessions.value = sessions.value.filter(s => s.sessionId !== sessionId)
  if (currentSessionId.value === sessionId) {
    currentSessionId.value = ''
    currentMessages.value = []
    if (sessions.value.length > 0) {
      switchSession(sessions.value[0])
    }
  }
  ElMessage.success('对话已删除')
}

const clearCurrentHistory = async () => {
  await ElMessageBox.confirm('确定要清空当前对话吗？', '提示', { type: 'warning' })
  try {
    if (currentSessionId.value) {
      await clearHistory(currentSessionId.value)
    }
  } catch {
    // ignore
  }
  currentMessages.value = []
  const session = sessions.value.find(s => s.sessionId === currentSessionId.value)
  if (session) session.messages = []
  ElMessage.success('对话已清空')
}

const addMessage = (role, content, isTyping = false) => {
  const msg = { role, content, time: Date.now(), isTyping }
  currentMessages.value.push(msg)
  const session = sessions.value.find(s => s.sessionId === currentSessionId.value)
  if (session) {
    session.messages.push(msg)
    if (role === 'user' && session.title === '新对话') {
      session.title = content.slice(0, 20) + (content.length > 20 ? '...' : '')
    }
  }
  scrollToBottom()
  return msg
}

const sendQuickPrompt = (text) => {
  inputText.value = text
  handleSend()
}

const handleSend = async () => {
  const content = inputText.value.trim()
  if (!content || chatLoading.value) return

  if (!currentSessionId.value) {
    createNewSession()
    await nextTick()
  }

  inputText.value = ''
  addMessage('user', content)
  chatLoading.value = true

  if (streamMode.value) {
    await sendStreamMessage(content)
  } else {
    await sendNormalMessage(content)
  }
}

const sendNormalMessage = async (content) => {
  try {
    const res = await chat({ content, sessionId: currentSessionId.value })
    const reply = res.data?.content || res.data?.message || res.data || '收到您的消息！'
    addMessage('ai', typeof reply === 'string' ? reply : JSON.stringify(reply))
  } catch {
    addMessage('ai', '抱歉，AI 服务暂时不可用，请稍后重试。')
  } finally {
    chatLoading.value = false
  }
}

const sendStreamMessage = async (content) => {
  const aiMsg = addMessage('ai', '', true)
  const msgIndex = currentMessages.value.length - 1

  try {
    const token = localStorage.getItem('token')
    const url = `/api/ai/stream?content=${encodeURIComponent(content)}&sessionId=${encodeURIComponent(currentSessionId.value)}`
    const response = await fetch(url, {
      headers: token ? { Token: token } : {}
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let fullContent = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          const dataStr = line.slice(5).trim()
          if (dataStr === '[DONE]') continue
          try {
            const parsed = JSON.parse(dataStr)
            const delta =
              parsed.choices?.[0]?.delta?.content ||
              parsed.delta?.content ||
              parsed.content ||
              parsed.message ||
              (typeof parsed === 'string' ? parsed : '')
            if (delta) {
              fullContent += delta
              currentMessages.value[msgIndex].content = fullContent
              scrollToBottom()
            }
          } catch {
            // If not JSON, treat raw data as content
            if (dataStr && dataStr !== '[DONE]') {
              fullContent += dataStr
              currentMessages.value[msgIndex].content = fullContent
              scrollToBottom()
            }
          }
        }
      }
    }

    currentMessages.value[msgIndex].isTyping = false
    if (!fullContent) {
      currentMessages.value[msgIndex].content = '收到您的消息，正在处理中...'
    }
    const session = sessions.value.find(s => s.sessionId === currentSessionId.value)
    if (session && session.messages[session.messages.length - 1]?.role === 'ai') {
      session.messages[session.messages.length - 1].content = fullContent || '收到您的消息'
    }
  } catch (e) {
    currentMessages.value[msgIndex].content = '抱歉，流式对话连接失败，请切换到普通模式或稍后重试。'
    currentMessages.value[msgIndex].isTyping = false
  } finally {
    chatLoading.value = false
    scrollToBottom()
  }
}

const fetchRecommendations = async () => {
  if (!recommendForm.category) {
    ElMessage.warning('请输入商品类别')
    return
  }
  recommendLoading.value = true
  recommendResult.value = ''
  try {
    const params = {
      category: recommendForm.category,
      budget: recommendForm.budget,
      preference: recommendForm.preference
    }
    const res = await getRecommendations(params)
    recommendResult.value = res.data?.content || res.data?.message || res.data || '暂无推荐结果'
    if (typeof recommendResult.value !== 'string') {
      recommendResult.value = JSON.stringify(recommendResult.value, null, 2)
    }
  } catch {
    recommendResult.value = '获取推荐失败，请稍后重试。'
  } finally {
    recommendLoading.value = false
  }
}

onMounted(() => {
  createNewSession()
})
</script>

<style scoped>
.ai-chat-page {
  height: calc(100vh - 96px);
  display: flex;
  overflow: hidden;
  background: #f0f2f5;
}

/* Sessions Panel */
.sessions-panel {
  width: 260px;
  background: #fff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e8e8e8;
  flex-shrink: 0;
}
.sessions-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.recommend-btn {
  margin: 12px;
  width: calc(100% - 24px);
  background: linear-gradient(135deg, #f39c12, #e67e22);
  border-color: #f39c12;
  color: #fff;
  font-weight: 600;
}
.recommend-btn:hover {
  background: linear-gradient(135deg, #e67e22, #d35400);
  border-color: #e67e22;
}
.sessions-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}
.session-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
  position: relative;
}
.session-item:hover {
  background: #f5f5f5;
}
.session-item.active {
  background: #fff0f0;
}
.session-icon {
  color: #c0392b;
  flex-shrink: 0;
}
.session-info {
  flex: 1;
  min-width: 0;
}
.session-title {
  font-size: 13px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.session-time {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}
.delete-icon {
  color: #bbb;
  font-size: 13px;
  opacity: 0;
  transition: opacity 0.15s;
}
.session-item:hover .delete-icon {
  opacity: 1;
}
.delete-icon:hover {
  color: #c0392b;
}
.sessions-empty {
  text-align: center;
  color: #bbb;
  font-size: 13px;
  padding: 40px 16px;
}

/* Chat Panel */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.chat-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}
.stream-label {
  font-size: 13px;
  color: #666;
}

/* Messages */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.welcome-msg {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  text-align: center;
  color: #666;
  gap: 12px;
  padding: 40px;
}
.welcome-msg h3 {
  font-size: 22px;
  color: #333;
  font-weight: 700;
}
.welcome-msg p {
  font-size: 14px;
  color: #888;
}
.quick-prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  margin-top: 8px;
}
.quick-prompts .el-button {
  border-color: #ddd;
  color: #555;
  font-size: 13px;
}
.quick-prompts .el-button:hover {
  border-color: #c0392b;
  color: #c0392b;
}
.message-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}
.user-row {
  flex-direction: row-reverse;
}
.loading-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ai-avatar {
  background: linear-gradient(135deg, #c0392b, #e74c3c);
}
.user-avatar {
  background: linear-gradient(135deg, #2c3e50, #3498db);
}
.bubble-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 70%;
}
.user-row .bubble-wrap {
  align-items: flex-end;
}
.bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.7;
  position: relative;
  word-break: break-word;
}
.user-bubble {
  background: #c0392b;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-bubble {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.msg-content :deep(strong) {
  font-weight: 700;
  color: inherit;
}
.msg-content :deep(em) {
  font-style: italic;
}
.msg-content :deep(code) {
  background: rgba(0,0,0,0.08);
  padding: 1px 4px;
  border-radius: 3px;
  font-family: monospace;
  font-size: 13px;
}
.ai-bubble .msg-content :deep(span) {
  display: block;
}
.msg-time {
  font-size: 11px;
  color: #bbb;
}
.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 16px;
  background: #333;
  margin-left: 2px;
  animation: blink 0.8s step-end infinite;
  vertical-align: text-bottom;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
.loading-bubble {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 14px 20px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #c0392b;
  animation: bounce 1.2s infinite;
  opacity: 0.6;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.6; }
  40% { transform: scale(1.2); opacity: 1; }
}

/* Input Area */
.input-area {
  padding: 16px;
  background: #fff;
  border-top: 1px solid #eee;
}
.input-box {
  background: #f8f8f8;
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid #e8e8e8;
  transition: border-color 0.2s;
}
.input-box:focus-within {
  border-color: #c0392b;
}
.input-box :deep(.el-textarea__inner) {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
  font-size: 14px;
  resize: none;
}
.input-box :deep(.el-textarea__inner):focus {
  box-shadow: none;
}
.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.input-hint {
  font-size: 12px;
  color: #bbb;
}

/* Recommend Dialog */
.recommend-result {
  margin-top: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}
.result-title {
  padding: 10px 14px;
  background: #f5f5f5;
  font-size: 13px;
  font-weight: 700;
  color: #555;
  border-bottom: 1px solid #e8e8e8;
}
.result-content {
  padding: 14px;
  font-size: 14px;
  line-height: 1.7;
  color: #333;
  max-height: 300px;
  overflow-y: auto;
}
.result-content :deep(strong) { font-weight: 700; }
.recommend-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 14px;
  margin-top: 12px;
  justify-content: center;
}
.loading-icon {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
