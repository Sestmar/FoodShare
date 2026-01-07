package com.example.ecorescueapp.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrCodeGenerator {
    fun generateQrBitmap(content: String): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            // Generamos una matriz de bits (QR) de 400x400 píxeles
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400)
            val encoder = BarcodeEncoder()
            encoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}