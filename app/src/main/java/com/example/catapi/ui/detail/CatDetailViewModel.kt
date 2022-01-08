package com.example.catapi.ui.detail

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.catapi.R
import com.example.catapi.network.Cat
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CatDetailViewModel(cat: Cat) : ViewModel() {
    private val _selectedCat = MutableLiveData<Cat>()
    val selectedCat: LiveData<Cat>
        get() = _selectedCat

    init {
        _selectedCat.value = cat
    }

    fun saveImage(context: Context) {
        viewModelScope.launch {
            Glide.with(context)
                .asBitmap()
                .load(selectedCat.value?.url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveMediaToStorage(resource, context)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // nothing
                    }
                })
        }
    }

    private fun saveMediaToStorage(bitmap: Bitmap, context: Context) {
        val filename = "${System.currentTimeMillis()}.jpg"

        var outputStream: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                outputStream = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // deprecated :/
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            outputStream = FileOutputStream(image)
        }

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it)
            Toast.makeText(
                context,
                context.getString(R.string.saved_to_gallery),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val JPEG_QUALITY = 100
    }
}
