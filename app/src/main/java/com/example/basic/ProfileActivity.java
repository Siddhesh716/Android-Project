package com.example.basic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName;
    private TextView profileMobile;
    private TextView profileAge;
    private TextView profileEmail;
    private TextView profileWeight;

    private ImageView profileImage;

    private Button addImageButton;
    private Button deleteImageButton;

    private Button logoutButton;
    private Button signOutButton;

    private static final int PICK_IMAGE = 1;

    private File profileImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileMobile = findViewById(R.id.profileMobile);
        profileAge = findViewById(R.id.profileAge);
        profileEmail = findViewById(R.id.profileEmail);
        profileWeight = findViewById(R.id.profileWeight);

        profileImage =
                findViewById(R.id.profileImage);

        addImageButton =
                findViewById(R.id.addImageButton);

        deleteImageButton =
                findViewById(R.id.deleteImageButton);

        logoutButton = findViewById(R.id.logoutButton);
        signOutButton = findViewById(R.id.signOutButton);

        logoutButton.setOnClickListener(v -> logout());
        signOutButton.setOnClickListener(v -> showSignOutConfirmation());

        loadUserData();

        loadProfileImage();
    }

    private void loadUserData() {

        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "UserData",
                        MODE_PRIVATE
                );

        profileName.setText(
                sharedPreferences.getString(
                        "USER_NAME",
                        "N/A"
                )
        );

        profileMobile.setText(
                sharedPreferences.getString(
                        "USER_MOBILE",
                        "N/A"
                )
        );

        profileAge.setText(
                sharedPreferences.getString(
                        "USER_AGE",
                        "N/A"
                )
        );

        profileEmail.setText(
                sharedPreferences.getString(
                        "USER_EMAIL",
                        "N/A"
                )
        );

        profileWeight.setText(
                sharedPreferences.getString(
                        "USER_WEIGHT",
                        "N/A"
                )
        );
    }

    private void loadProfileImage() {

        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "UserData",
                        MODE_PRIVATE
                );

        String imagePath =
                sharedPreferences.getString(
                        "PROFILE_IMAGE_PATH",
                        null
                );

        if (imagePath != null) {

            profileImageFile =
                    new File(imagePath);

            if (profileImageFile.exists()) {

                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                profileImageFile.getAbsolutePath()
                        );

                if (bitmap != null) {

                    /*
                     * Display the image normally.
                     * No circular cropping.
                     */
                    profileImage.setImageBitmap(bitmap);

                    deleteImageButton.setVisibility(
                            View.VISIBLE
                    );
                }
            }
        }
    }

    public void onProfileImageClick(View view) {

        if (profileImage.getDrawable() != null) {

            showImageDialog();
        }
    }

    private void showImageDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        View dialogView =
                getLayoutInflater().inflate(
                        R.layout.dialog_image_view,
                        null
                );

        ImageView dialogImageView =
                dialogView.findViewById(
                        R.id.dialogImageView
                );

        dialogImageView.setImageDrawable(
                profileImage.getDrawable()
        );

        builder.setView(dialogView);

        AlertDialog dialog =
                builder.create();

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }

    public void onAddImage(View view) {

        Intent intent =
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

        startActivityForResult(
                intent,
                PICK_IMAGE
        );
    }

    public void onDeleteImage(View view) {

        if (profileImageFile != null &&
                profileImageFile.exists()) {

            profileImageFile.delete();
        }

        profileImageFile = null;

        profileImage.setImageResource(
                R.drawable.ic_profile
        );

        deleteImageButton.setVisibility(
                View.GONE
        );

        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "UserData",
                        MODE_PRIVATE
                );

        sharedPreferences.edit()
                .remove("PROFILE_IMAGE_PATH")
                .apply();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK ||
                data == null) {

            return;
        }

        Uri selectedImage =
                data.getData();

        if (requestCode == PICK_IMAGE &&
                selectedImage != null) {

            try {

                /*
                 * Read the selected image and correct
                 * its EXIF orientation.
                 */
                Bitmap bitmap =
                        getCorrectlyOrientedBitmap(
                                selectedImage
                        );

                if (bitmap != null) {

                    /*
                     * Display the full image.
                     * No circular processing.
                     */
                    profileImage.setImageBitmap(bitmap);

                    /*
                     * Save the correctly oriented image.
                     */
                    saveProfileImage(bitmap);

                    deleteImageButton.setVisibility(
                            View.VISIBLE
                    );
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private Bitmap getCorrectlyOrientedBitmap(
            Uri imageUri
    ) throws IOException {

        Bitmap bitmap =
                MediaStore.Images.Media.getBitmap(
                        getContentResolver(),
                        imageUri
                );

        if (bitmap == null) {

            return null;
        }

        InputStream inputStream =
                getContentResolver()
                        .openInputStream(imageUri);

        if (inputStream == null) {

            return bitmap;
        }

        ExifInterface exifInterface;

        try {

            exifInterface =
                    new ExifInterface(inputStream);

        } finally {

            inputStream.close();
        }

        int orientation =
                exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                );

        Matrix matrix =
                new Matrix();

        switch (orientation) {

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:

                matrix.setScale(
                        -1,
                        1
                );

                break;

            case ExifInterface.ORIENTATION_ROTATE_180:

                matrix.setRotate(180);

                break;

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:

                matrix.setRotate(180);

                matrix.postScale(
                        -1,
                        1
                );

                break;

            case ExifInterface.ORIENTATION_TRANSPOSE:

                matrix.setRotate(90);

                matrix.postScale(
                        -1,
                        1
                );

                break;

            case ExifInterface.ORIENTATION_ROTATE_90:

                matrix.setRotate(90);

                break;

            case ExifInterface.ORIENTATION_TRANSVERSE:

                matrix.setRotate(-90);

                matrix.postScale(
                        -1,
                        1
                );

                break;

            case ExifInterface.ORIENTATION_ROTATE_270:

                matrix.setRotate(-90);

                break;

            case ExifInterface.ORIENTATION_NORMAL:

            default:

                return bitmap;
        }

        Bitmap rotatedBitmap =
                Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        matrix,
                        true
                );

        if (rotatedBitmap != bitmap) {

            bitmap.recycle();
        }

        return rotatedBitmap;
    }

    private void saveProfileImage(
            Bitmap bitmap
    ) {

        File directory =
                new File(
                        getFilesDir(),
                        "profile_images"
                );

        if (!directory.exists()) {

            directory.mkdirs();
        }

        profileImageFile =
                new File(
                        directory,
                        "profile_image.jpg"
                );

        try (
                FileOutputStream out =
                        new FileOutputStream(
                                profileImageFile
                        )
        ) {

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    out
            );

            SharedPreferences sharedPreferences =
                    getSharedPreferences(
                            "UserData",
                            MODE_PRIVATE
                    );

            sharedPreferences.edit()
                    .putString(
                            "PROFILE_IMAGE_PATH",
                            profileImageFile.getAbsolutePath()
                    )
                    .apply();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void logout() {
        // End the login session but preserve the account and saved data.
        getSharedPreferences("LoginSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        openLoginScreen();
    }

    private void showSignOutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage(
                        "This will permanently delete your account, profile photo, " +
                                "and saved fitness data from this installation. Continue?"
                )
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Sign Out", (dialog, which) -> signOut())
                .show();
    }

    private void signOut() {
        SharedPreferences userData =
                getSharedPreferences("UserData", MODE_PRIVATE);

        String name = userData.getString("USER_NAME", null);
        String mobile = userData.getString("USER_MOBILE", null);

        if (name == null || mobile == null) {
            Toast.makeText(this, "Unable to identify the current account.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseHelper helper = new DatabaseHelper(this);
        int deletedRows;

        try {
            deletedRows = helper.deleteUser(name, mobile);
        } catch (Exception e) {
            Toast.makeText(this, "Could not delete the account.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (deletedRows == 0) {
            Toast.makeText(this, "Account not found. No account was deleted.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Delete the saved profile image.
        if (profileImageFile != null && profileImageFile.exists()) {
            profileImageFile.delete();
        }

        File imageDirectory = new File(getFilesDir(), "profile_images");
        if (imageDirectory.exists()) {
            File[] files = imageDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }

        // Clear profile and saved fitness data for this single-user installation.
        userData.edit().clear().apply();

        getSharedPreferences("LoginSession", MODE_PRIVATE)
                .edit().clear().apply();

        getSharedPreferences("WorkoutData", MODE_PRIVATE)
                .edit().clear().apply();

        getSharedPreferences("WorkoutHistory", MODE_PRIVATE)
                .edit().clear().apply();

        openLoginScreen();
    }

    private void openLoginScreen() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}