package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hoanghai.fashionstoreapplication.Adapter.CategoryAdapter;
import com.hoanghai.fashionstoreapplication.model.Category;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CreateProductActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivProduct;
    private RecyclerView rcvCategory;
    private EditText edtNameProduct;
    private EditText edtPrice;
    private EditText edtAmount;
    private EditText edtDescription;
    private LinearLayout lnAddProduct;
    private String nameProduct,price,amount
            ,description,saveCurrentDate,downloadImageUrl,saveCurrentTime;
    private String productRandomKey;

    private Chip chipClothes,chipTrouser,chipbag,chipShoes, chipSandal;

    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    private String category = "";
    private final static  int SELECT_PICTURE = 200;
    private Uri pathImage;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        productImageRef = FirebaseStorage.getInstance()
                .getReference().child("Product").child("images");
        productRef = FirebaseDatabase
                .getInstance("https://fashionstoreapplication-default-rtdb" +
                ".asia-southeast1.firebasedatabase.app/")
                .getReference().child("Products");
        setUp();
    }

    private void setUp() {
        ivProduct = findViewById(R.id.ivUploadImageProduct);
        rcvCategory = findViewById(R.id.rcvCategory);
        edtNameProduct = findViewById(R.id.edtNameProduct);
        edtPrice = findViewById(R.id.edtPrice);
        edtAmount = findViewById(R.id.edtAmount);
        edtDescription = findViewById(R.id.edtDescription);
        lnAddProduct = findViewById(R.id.lnAddProduct);

        chipClothes = findViewById(R.id.chipClothes);
        chipTrouser = findViewById(R.id.chipTrouser);
        chipbag = findViewById(R.id.chipBag);
        chipShoes = findViewById(R.id.chipShoes);
        chipSandal = findViewById(R.id.chipSandal);

        chipClothes.setOnClickListener(this);
        chipTrouser.setOnClickListener(this);
        chipbag.setOnClickListener(this);
        chipShoes.setOnClickListener(this);
        chipSandal.setOnClickListener(this);
        ivProduct.setOnClickListener(this);
        lnAddProduct.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chipClothes:
                if(chipClothes.isCheckable()) {
                    category = "c";
                }
                break;
            case R.id.chipBag:
                if(chipbag.isCheckable()) {
                    category = "b";
                }
                break;
            case R.id.chipShoes:
                if(chipShoes.isCheckable()) {
                    category = "s";
                }
                break;
            case R.id.chipTrouser:
                if(chipTrouser.isCheckable()) {
                    category = "t";
                }
                break;
            case R.id.chipSandal:
                if(chipSandal.isCheckable()) {
                    category = "sd";
                }
                break;
            case  R.id.ivUploadImageProduct:
                imageChooser();
                break;
            case  R.id.lnAddProduct:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Bạn có muốn thêm sản phẩm này không");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createNewProduct();
                    }
                });
               builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               });
               builder.show();
                break;
        }
        Log.d("HDH","catergory = "+category);
    }

    private void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn ảnh"),SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == SELECT_PICTURE
                && resultCode == RESULT_OK
                && data != null){
            pathImage = data.getData();
            ivProduct.setImageURI(pathImage);
            Log.d("HDH","path = "+pathImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void valueProductData(){
        ProgressDialog progressDialog =
                ProgressDialog.show(CreateProductActivity.this,
                        null, null
                        , true);
        progressDialog.setContentView(R.layout.design_progress);
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog.show();
        nameProduct = edtNameProduct.getText().toString();
        price = edtPrice.getText().toString();
        amount = edtAmount.getText().toString();
        description = edtDescription.getText().toString();

        if(pathImage == null){
            Toast.makeText(this,"Thêm ảnh của sản phẩm",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else {
            StoreProductInformation();
            progressDialog.dismiss();
        }

    }

    private void StoreProductInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        String path = pathImage.getLastPathSegment() + productRandomKey + ".jpg";
        StorageReference filePath =
                productImageRef.child(path);
        final UploadTask uploadTask = filePath.putFile(pathImage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(CreateProductActivity.this,
                        "Error " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        downloadImageUrl = filePath.getDownloadUrl().toString();

                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            SaveProductInfoDatabase();
                            
                        }
                    }
                });
            }
        });


    }

    private void SaveProductInfoDatabase() {
        int amount = Integer.parseInt(this.amount);
        Product product =
                new Product(productRandomKey
                        ,downloadImageUrl
                        ,nameProduct
                        ,price
                        ,amount
                        ,description
                        ,category);

        productRef.child(productRandomKey).setValue(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreateProductActivity.this, "Thêm sản phẩm thành công"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createNewProduct() {
        valueProductData();
    }
}