package com.ywauran.storyapp.ui.story.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ywauran.storyapp.ui.main.MainActivity
import com.ywauran.storyapp.R
import com.ywauran.storyapp.databinding.ActivityAddStoryBinding
import com.ywauran.storyapp.databinding.BottomDialogBinding
import com.ywauran.storyapp.helper.reduceFileImage
import com.ywauran.storyapp.helper.uriToFile
import com.ywauran.storyapp.ui.auth.login.LoginActivity
import com.ywauran.storyapp.ui.camera.CameraXActivity
import com.ywauran.storyapp.ui.story.StoryViewModel
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSION = 101
    }

    private lateinit var binding: ActivityAddStoryBinding
    private var imgFile: File? = null
    private val storyViewModel: StoryViewModel by viewModels()

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ activityResult ->
        if (activityResult.resultCode == CAMERA_X_RESULT) {
            val myFile = activityResult?.data?.getSerializableExtra("CAMERA_X_FILE") as? File
            val isCameraBack = activityResult?.data?.getBooleanExtra("IS_CAMERA_BACK", true) ?: true

            if (myFile != null) {
                imgFile = reduceFileImage(myFile)
                binding.ivAddStory.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
            }
        }
    }

    private val launcherImageGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val myUri = activityResult?.data?.data as Uri
            val createFile = uriToFile(myUri, this@AddStoryActivity)

            imgFile = reduceFileImage(createFile)

            binding.ivAddStory.setImageURI(myUri)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupPermissions()
        setupClickListeners()
        setupTextChangedListener()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Story"
    }

    private fun setupPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            btnUpload.setOnClickListener {
                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(
                        this@AddStoryActivity, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION
                    )
                } else {
                    showDialogSelectAddImage()
                }
            }

            btnAdd.setOnClickListener {
                validation()
            }
        }
    }

    private fun setupTextChangedListener() {
        binding.edDescription.addTextChangedListener(watcher())
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun watcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.edDescription.error = null
                    binding.btnUpload.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnAdd.isEnabled = binding.edDescription.text.toString().trim().isNotEmpty()
            }
        }
    }


    private fun validation() {
        val description = binding.edDescription.text.toString().trim()

        if (description.isEmpty()) {
            binding.edDescription.error = "Description cannot be empty"
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (imgFile == null || !imgFile!!.exists()) {
            Toast.makeText(this, "Photo cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        storyViewModel.addStory(description, imgFile as File, ).observe(this) { responseHandlingResult ->
            when (responseHandlingResult) {
                is Result.Loading -> setLoading(true)
                is Result.Success -> {
                    setLoading(false)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                else -> {
                    setLoading(false)
                    if (responseHandlingResult.code == 401) {
                        Toast.makeText(this@AddStoryActivity, "Your token expired, please relogin!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AddStoryActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    Toast.makeText(this@AddStoryActivity, responseHandlingResult.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startCameraX(){
        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startImageGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherImageGallery.launch(chooser)
    }

    private fun showDialogSelectAddImage() {
        val binding: BottomDialogBinding = BottomDialogBinding.inflate(
            LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomDialogStyle)
        val view = binding.root

        with (binding) {

            llCamera.setOnClickListener {
                startCameraX()
                bottomSheetDialog.dismiss()
            }

            llGallery.setOnClickListener {
                startImageGallery()
                bottomSheetDialog.dismiss()
            }

            ivClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak mendapat permission!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}