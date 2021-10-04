package com.example.catapi.ui.detail

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.catapi.R
import com.example.catapi.databinding.CatDetailFragmentBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CatDetailFragment : Fragment(R.layout.cat_detail_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = CatDetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val cat = CatDetailFragmentArgs.fromBundle(requireArguments()).cat
        val viewModelFactory = CatViewModelFactory(cat)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CatDetailViewModel::class.java)

        binding.viewModel = viewModel

        binding.downloadButton.setOnClickListener {
            lifecycleScope.launch {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(viewModel.selectedCat.value?.url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            saveMediaToStorage(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // nothing
                        }
                    })
            }
        }
        return binding.root
    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"

        var outputStream: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->

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
            Toast.makeText(context, getString(R.string.saved_to_gallery), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val JPEG_QUALITY = 100
    }
}
