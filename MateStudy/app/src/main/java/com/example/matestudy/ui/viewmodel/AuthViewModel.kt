package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.User
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // ────────────────────────────────────────────────────────────────
    // 1. Trạng thái cốt lõi (authentication state)
    // ────────────────────────────────────────────────────────────────
    val isLoggedIn: StateFlow<Boolean> = repository.getCurrentUserFlow()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCurrentUserFlow().collect { entity ->
                _currentUser.value = entity?.toUserDomain() ?: User()
            }
        }
    }

    // ────────────────────────────────────────────────────────────────
    // 2. Trạng thái UI chung (loading, error)
    // ────────────────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ────────────────────────────────────────────────────────────────
    // 3. Các field input cho các màn hình (Login / Register / Change Password)
    // ────────────────────────────────────────────────────────────────
    // Login fields
    private val _emailOrUsername = MutableStateFlow("")
    val emailOrUsername: StateFlow<String> = _emailOrUsername.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Register fields
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

    // Change password fields
    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmNewPassword = MutableStateFlow("")
    val confirmNewPassword: StateFlow<String> = _confirmNewPassword.asStateFlow()

    // ────────────────────────────────────────────────────────────────
    // 4. Các hàm onChange (cập nhật state khi người dùng nhập)
    // ────────────────────────────────────────────────────────────────
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

    // ────────────────────────────────────────────────────────────────
    // 5. Các hàm hành động chính (business logic)
    // ────────────────────────────────────────────────────────────────
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val identifier = _emailOrUsername.value.trim()
                val pass = _password.value

                val userEntity = repository.findUserByUsernameOrEmail(identifier)
                if (userEntity != null && userEntity.matKhau == pass) {
                    repository.loginToSession(userEntity)
                    onSuccess()
                } else {
                    _error.value = "Thông tin không chính xác"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                if (repository.findUserByUsernameOrEmail(username) != null) {
                    _error.value = "Tên đăng nhập đã tồn tại"
                    return@launch
                }
                if (repository.findUserByUsernameOrEmail(email) != null) {
                    _error.value = "Email đã được sử dụng"
                    return@launch
                }

                val newUser = UserEntity(
                    id = System.currentTimeMillis(),
                    tenDangNhap = username,
                    email = email,
                    matKhau = pass,
                    nienKhoa = nienKhoaStr.toIntOrNull(),
                    vaiTro = "sinh_vien",
                    trangThai = "hoat_dong",
                    anhDaiDien = null
                )

                repository.saveUser(newUser)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Đăng ký thất bại: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

//    fun registerWithRole(role: String, onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//
//            val errors = mutableListOf<String>()
//            val username = _registerUsername.value.trim()
//            val email = _registerEmail.value.trim()
//            val nienKhoaStr = _registerNienKhoa.value.trim()
//            val pass = _registerPassword.value
//            val confirmPass = _registerConfirmPassword.value
//
//            if (username.length < 4) errors.add("Tên đăng nhập phải ≥ 4 ký tự")
//            if (!email.contains("@") || !email.contains(".")) errors.add("Email không hợp lệ")
//            if (nienKhoaStr.length != 4 || nienKhoaStr.toIntOrNull() == null) {
//                errors.add("Niên khóa phải là 4 số (VD: 2024)")
//            }
//            if (pass.length < 8) errors.add("Mật khẩu phải ≥ 8 ký tự")
//            if (pass != confirmPass) errors.add("Xác nhận mật khẩu không khớp")
//
//            if (errors.isNotEmpty()) {
//                _error.value = errors.joinToString("\n")
//                _isLoading.value = false
//                return@launch
//            }
//
//            try {
//                if (repository.findUserByUsernameOrEmail(username) != null) {
//                    _error.value = "Tên đăng nhập đã tồn tại"
//                    return@launch
//                }
//                if (repository.findUserByUsernameOrEmail(email) != null) {
//                    _error.value = "Email đã được sử dụng"
//                    return@launch
//                }
//
//                val newUser = UserEntity(
//                    id = System.currentTimeMillis(),
//                    tenDangNhap = username,
//                    email = email,
//                    matKhau = pass,
//                    nienKhoa = nienKhoaStr.toIntOrNull(),
//                    vaiTro = role,
//                    trangThai = "hoat_dong",
//                    anhDaiDien = null
//                )
//
//                repository.saveUser(newUser)
//                onSuccess()
//            } catch (e: Exception) {
//                _error.value = "Đăng ký thất bại: ${e.message}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

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
                val current = repository.getCurrentUserFlow().first() ?: run {
                    _error.value = "Không tìm thấy thông tin người dùng"
                    return@launch
                }

                if (current.matKhau != oldPass) {
                    _error.value = "Mật khẩu cũ không đúng"
                    return@launch
                }

                val updated = current.copy(matKhau = newPass)
                repository.updateUser(updated)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Đổi mật khẩu thất bại: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Trong AuthViewModel.kt
    fun updateProfile(
        newUsername: String,
        newEmail: String,
        newNienKhoa: Int?,
        newAvatarUrl: String?          // ← Đổi thành newAvatarUrl cho rõ ràng
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentEntity = repository.getCurrentUserFlow().first()
                    ?: run {
                        _error.value = "Không tìm thấy thông tin người dùng hiện tại"
                        return@launch
                    }

                // Validation
                if (newUsername.trim().length < 4) {
                    _error.value = "Tên đăng nhập phải từ 4 ký tự trở lên"
                    return@launch
                }
                if (newEmail.isNotBlank() && (!newEmail.contains("@") || !newEmail.contains("."))) {
                    _error.value = "Email không hợp lệ"
                    return@launch
                }

                val updatedEntity = currentEntity.copy(
                    tenDangNhap = newUsername.trim(),
                    email = newEmail.trim(),
                    nienKhoa = newNienKhoa,
                    anhDaiDien = newAvatarUrl ?: currentEntity.anhDaiDien
                )

                repository.updateUser(updatedEntity)

                // Cập nhật lại session để UI refresh ngay lập tức
                repository.loginToSession(updatedEntity)

                _error.value = null

            } catch (e: Exception) {
                _error.value = "Cập nhật thất bại: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            clearAllFields()
        }
    }

    // ────────────────────────────────────────────────────────────────
    // 6. Hàm hỗ trợ (private helpers)
    // ────────────────────────────────────────────────────────────────
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

    private fun UserEntity.toUserDomain(): User = User(
        id = id,
        tenDangNhap = tenDangNhap,
        email = email,
        nienKhoa = nienKhoa,
        vaiTro = vaiTro,
        trangThai = trangThai,
        anhDaiDien = anhDaiDien
    )
}