package com.example.bluromatic.data

/**
 * Repository interface cho Blur-O-Matic app.
 * Định nghĩa các hàm mà repository phải thực hiện.
 */
interface BluromaticRepository {

    /**
     * Áp dụng hiệu ứng blur lên hình ảnh với mức độ được chọn.
     * @param blurLevel Mức độ blur (1 = A little blurred, 2 = More blurred, 3 = The most blurred)
     */
    fun applyBlur(blurLevel: Int)
}