package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.User
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // ── Trạng thái đăng nhập ────────────────────────────────────────────────────────
    val isLoggedIn: StateFlow<Boolean> = repository.getCurrentUserFlow()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // ── User hiện tại (domain model cho UI) ─────────────────────────────────────────
    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCurrentUserFlow().collect { entity ->
                if (entity != null) {
                    _currentUser.value = entity.toUserDomain()
                } else {
                    _currentUser.value = User() // reset khi logout hoặc chưa đăng nhập
                }
            }
        }
    }

    // ── Trạng thái chung: loading & error ───────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ── Login fields ────────────────────────────────────────────────────────────────
    private val _emailOrUsername = MutableStateFlow("")
    val emailOrUsername: StateFlow<String> = _emailOrUsername.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // ── Register fields ─────────────────────────────────────────────────────────────
    private val _registerUsername = MutableStateFlow("")
    val registerUsername: StateFlow<String> = _registerUsername.asStateFlow()

    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail.asStateFlow()

    private val _registerNienKhoa = MutableStateFlow("")
    val registerNienKhoa: StateFlow<String> = _registerNienKhoa.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword: StateFlow<String> = _registerConfirmPassword.asStateFlow()

    // ── Change password fields ──────────────────────────────────────────────────────
    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmNewPassword = MutableStateFlow("")
    val confirmNewPassword: StateFlow<String> = _confirmNewPassword.asStateFlow()

    // ── Các hàm xử lý onChange ──────────────────────────────────────────────────────
    fun onEmailOrUsernameChange(value: String) {
        _emailOrUsername.value = value
        _error.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _error.value = null
    }

    fun onRegisterUsernameChange(value: String) {
        _registerUsername.value = value
    }

    fun onRegisterEmailChange(value: String) {
        _registerEmail.value = value
    }

    fun onRegisterNienKhoaChange(value: String) {
        _registerNienKhoa.value = value
    }

    fun onRegisterPasswordChange(value: String) {
        _registerPassword.value = value
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerConfirmPassword.value = value
    }

    fun onOldPasswordChange(value: String) {
        _oldPassword.value = value
    }

    fun onNewPasswordChange(value: String) {
        _newPassword.value = value
    }

    fun onConfirmNewPasswordChange(value: String) {
        _confirmNewPassword.value = value
    }

    // ── Login ───────────────────────────────────────────────────────────────────────
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val identifier = _emailOrUsername.value.trim()
            val pass = _password.value

            if (identifier.isBlank() || pass.isBlank()) {
                _error.value = "Vui lòng nhập đầy đủ thông tin"
                _isLoading.value = false
                return@launch
            }

            try {
                val userEntity = repository.findUserByUsernameOrEmail(identifier)
                if (userEntity != null && userEntity.matKhau == pass) { // TODO: so sánh hash thực tế
                    // Room đã lưu → Flow tự động cập nhật isLoggedIn
                    _currentUser.value = userEntity.toUserDomain()
                    onSuccess()
                } else {
                    _error.value = "Tên đăng nhập / email hoặc mật khẩu không đúng"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message ?: "Không thể đăng nhập"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Register ────────────────────────────────────────────────────────────────────
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val errors = mutableListOf<String>()
            val username = _registerUsername.value.trim()
            val email = _registerEmail.value.trim()
            val nienKhoaStr = _registerNienKhoa.value.trim()
            val pass = _registerPassword.value
            val confirmPass = _registerConfirmPassword.value

            if (username.length < 4) errors.add("Tên đăng nhập phải ≥ 4 ký tự")
            if (!email.contains("@") || !email.contains(".")) errors.add("Email không hợp lệ")
            if (nienKhoaStr.length != 4 || nienKhoaStr.toIntOrNull() == null) {
                errors.add("Niên khóa phải là 4 số (VD: 2024)")
            }
            if (pass.length < 8) errors.add("Mật khẩu phải ≥ 8 ký tự")
            if (pass != confirmPass) errors.add("Xác nhận mật khẩu không khớp")

            if (errors.isNotEmpty()) {
                _error.value = errors.joinToString("\n")
                _isLoading.value = false
                return@launch
            }

            try {
                // Kiểm tra trùng username / email (tuỳ chọn)
                if (repository.findUserByUsernameOrEmail(username) != null) {
                    _error.value = "Tên đăng nhập đã tồn tại"
                    _isLoading.value = false
                    return@launch
                }
                if (repository.findUserByUsernameOrEmail(email) != null) {
                    _error.value = "Email đã được sử dụng"
                    _isLoading.value = false
                    return@launch
                }

                val newUserEntity = UserEntity(
                    tenDangNhap = username,
                    email = email,
                    matKhau = pass, // TODO: hash password trước khi lưu
                    nienKhoa = nienKhoaStr.toIntOrNull(),
                    vaiTro = "sinh_vien",
                    trangThai = "hoat_dong",
                    anhDaiDien = null
                )

                repository.saveUser(newUserEntity)
                // Flow sẽ tự cập nhật → isLoggedIn = true
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Đăng ký thất bại: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Đổi mật khẩu ────────────────────────────────────────────────────────────────
    fun changePassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val oldPass = _oldPassword.value
            val newPass = _newPassword.value
            val confirmNew = _confirmNewPassword.value

            val errors = mutableListOf<String>()
            if (oldPass.isBlank()) errors.add("Vui lòng nhập mật khẩu cũ")
            if (newPass.length < 8) errors.add("Mật khẩu mới phải ≥ 8 ký tự")
            if (newPass != confirmNew) errors.add("Xác nhận mật khẩu mới không khớp")

            if (errors.isNotEmpty()) {
                _error.value = errors.joinToString("\n")
                _isLoading.value = false
                return@launch
            }

            try {
                val currentEntity = repository.getCurrentUserFlow().first()
                if (currentEntity == null) {
                    _error.value = "Không tìm thấy thông tin người dùng"
                    return@launch
                }

                if (currentEntity.matKhau != oldPass) { // TODO: so sánh hash
                    _error.value = "Mật khẩu cũ không đúng"
                    return@launch
                }

                val updatedUser = currentEntity.copy(
                    matKhau = newPass // TODO: hash trước khi lưu
                )

                repository.updateUser(updatedUser)
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Đổi mật khẩu thất bại: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Đăng xuất ───────────────────────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            repository.clearUserData()
            // Flow tự reset → isLoggedIn = false
            clearAllFields()
        }
    }

    private fun clearAllFields() {
        _emailOrUsername.value = ""
        _password.value = ""
        _registerUsername.value = ""
        _registerEmail.value = ""
        _registerNienKhoa.value = ""
        _registerPassword.value = ""
        _registerConfirmPassword.value = ""
        _oldPassword.value = ""
        _newPassword.value = ""
        _confirmNewPassword.value = ""
        _error.value = null
    }

    // Reset error khi người dùng tương tác lại (tuỳ chọn gọi từ UI)
    fun clearError() {
        _error.value = null
    }

    // ui/viewmodel/AuthViewModel.kt
    fun updateProfile(
        newUsername: String,
        newEmail: String,
        newNienKhoa: Int?,
        newAvatarUri: String? // tạm là uri, sau này xử lý file thật
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentEntity = repository.getCurrentUserFlow().first() ?: run {
                    _error.value = "Không tìm thấy thông tin người dùng"
                    return@launch
                }

                // Validate cơ bản
                if (newUsername.isBlank() || newUsername.length < 4) {
                    _error.value = "Tên đăng nhập phải từ 4 ký tự trở lên"
                    return@launch
                }
                if (newEmail.isBlank() || !newEmail.contains("@") || !newEmail.contains(".")) {
                    _error.value = "Email không hợp lệ"
                    return@launch
                }
                if (newNienKhoa != null && (newNienKhoa < 1900 || newNienKhoa > 2100)) {
                    _error.value = "Niên khóa không hợp lệ"
                    return@launch
                }

                // Trong updateProfile, thay phần kiểm tra trùng bằng cách này:
                val existingByUsername = repository.findUserByUsernameOrEmail(newUsername)
                if (existingByUsername != null && existingByUsername.id != currentEntity.id) {
                    if (existingByUsername.tenDangNhap == newUsername) {
                        _error.value = "Tên đăng nhập đã được sử dụng"
                        return@launch
                    }
                }

                val existingByEmail = repository.findUserByUsernameOrEmail(newEmail)
                if (existingByEmail != null && existingByEmail.id != currentEntity.id) {
                    if (existingByEmail.email == newEmail) {
                        _error.value = "Email đã được sử dụng"
                        return@launch
                    }
                }

                // Xử lý ảnh (demo đơn giản)
                val finalAvatar = when {
                    newAvatarUri == null -> currentEntity.anhDaiDien
                    else -> newAvatarUri // sau này nên copy file và lưu path
                }

                // Cập nhật entity
                val updatedEntity = currentEntity.copy(
                    tenDangNhap = newUsername.trim(),
                    email = newEmail.trim(),
                    nienKhoa = newNienKhoa,
                    anhDaiDien = finalAvatar
                )

                repository.updateUser(updatedEntity)
                // Flow sẽ tự cập nhật currentUser → UI refresh

            } catch (e: Exception) {
                _error.value = "Cập nhật thất bại: ${e.localizedMessage ?: "Lỗi không xác định"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Extension mapping Entity → Domain model ─────────────────────────────────────
    private fun UserEntity.toUserDomain(): User = User(
        id = this.id,
        tenDangNhap = this.tenDangNhap,
        email = this.email,
        nienKhoa = this.nienKhoa,
        vaiTro = this.vaiTro,
        trangThai = this.trangThai,
        anhDaiDien = this.anhDaiDien
    )
}