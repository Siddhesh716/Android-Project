package com.example.basic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileMobile, profileAge, profileEmail, profileWeight;
    private ImageView profileImage;
    private Button addImageButton, deleteImageButton;
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
        profileImage = findViewById(R.id.profileImage);
        addImageButton = findViewById(R.id.addImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        profileName.setText(sharedPreferences.getString("USER_NAME", "N/A"));
        profileMobile.setText("Mobile: " + sharedPreferences.getString("USER_MOBILE", "N/A"));
        profileAge.setText("Age: " + sharedPreferences.getString("USER_AGE", "N/A"));
        profileEmail.setText("Email: " + sharedPreferences.getString("USER_EMAIL", "N/A"));
        profileWeight.setText("Weight: " + sharedPreferences.getString("USER_WEIGHT", "N/A"));

        loadProfileImage();
    }

    private void loadProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("PROFILE_IMAGE_PATH", null);

        if (imagePath != null) {
            profileImageFile = new File(imagePath);
            if (profileImageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
                profileImage.setImageBitmap(getCircularBitmap(bitmap));
                deleteImageButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onProfileImageClick(View view) {
        showImageDialog();
    }
    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_view, null);
        ImageView dialogImageView = dialogView.findViewById(R.id.dialogImageView);
        dialogImageView.setImageDrawable(profileImage.getDrawable());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onAddImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    public void onDeleteImage(View view) {
        if (profileImageFile != null && profileImageFile.exists()) {
            profileImageFile.delete();
            profileImage.setImageResource(R.drawable.ic_profile);
            deleteImageButton.setVisibility(View.GONE);
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("PROFILE_IMAGE_PATH");
            editor.apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (requestCode == PICK_IMAGE) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    bitmap = getCircularBitmap(bitmap);
                    profileImage.setImageBitmap(bitmap);
                    saveProfileImage(bitmap);
                    deleteImageButton.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveProfileImage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "profile_images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        profileImageFile = new File(directory, "profile_image.jpg");
        try (FileOutputStream out = new FileOutputStream(profileImageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PROFILE_IMAGE_PATH", profileImageFile.getAbsolutePath());
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, width);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(width / 2f, width / 2f, width / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}