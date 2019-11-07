package skybiz.com.posoffline.ui_Service;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewCustomer.DialogCustomer;
import skybiz.com.posoffline.m_NewSupplier.DialogSupplier;
import skybiz.com.posoffline.m_NewTerm.DialogTerm;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_Service.m_List.DialogListJS;
import skybiz.com.posoffline.ui_Service.m_List.RetImgService;
import skybiz.com.posoffline.ui_Service.m_Save.SaveService;
import skybiz.com.posoffline.ui_Service.m_Save.UpdateService;
import skybiz.com.posoffline.ui_Service.m_Service.DialogService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Service_Information extends Fragment {

    View view, view1;
    Button btnCustomer, btnCaseType, btnRepairType,
            btnServiceStatus, btnTerm, btnVendor,
            btnTakePicture, btnSave;
    EditText txtSendToVendorYN, txtBackFromVendorYN, txtReturnBackEndUserYN,
            txtEntryDate, txtOutputDate, txtWarrantyExpDate,
            txtCusCode, txtCusName, txtContact,
            txtContactTel, txtEmail, txtAddress,
            txtRepairType, txtReceiveMode, txtReceiveNo,
            txtCaseType, txtServiceStatus, txtReceiveDate,
            txtEntryID, txtOutputID, txtProductModel,
            txtPartNumber, txtSupplierSerialNo, txtAccessories,
            txtProblemDesc, txtTermCode, txtTermDesc,
            txtWarrantyDesc, txtCollectedBy, txtVendorCode,
            txtVendorName, txtVendorTel, txtServiceNoteRemark,
            txtReturnBackBy, txtDoc1No, txtSerialNo,
            txtActionTimeStart, txtActionTimeEnd;
    CheckBox chkSendToVendorYN, chkBackFromVendorYN, chkReturnBackEndUserYN;
    DatePickerDialog datePickerDialog;
    TimePickerDialog mTimePicker;
    Spinner spWarrantyStatus, spVenWarrantyStatus,
            spPriority,spServiceStatus;
    private static final int CAMERA_REQUEST = 1888;
    ImageView imgService, imgSignature, imgService2;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Button btnShowSign, mClear, mGetSign,
            mCancel, btnPrint, btnOutstandingJS,
            btnClosedJS, btnSavePicture;

    TextView txtImgService,txtImgService2;

    File file, finalFile;
    Dialog dialog;
    LinearLayout mContent;
    signature mSignature;
    Bitmap bitmap;
    String vImgService="", vImgSignature, vImgService2="";
    String IPPrinter, TypePrinter, NamePrinter, vPort;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mservice, container, false);
        btnCustomer = (Button) view.findViewById(R.id.btnCustomer);
        btnPrint = (Button) view.findViewById(R.id.btnPrint);
        btnCaseType = (Button) view.findViewById(R.id.btnCaseType);
        btnRepairType = (Button) view.findViewById(R.id.btnRepairType);
       // btnServiceStatus = (Button) view.findViewById(R.id.btnServiceStatus);
        btnTerm = (Button) view.findViewById(R.id.btnTerm);
        btnVendor = (Button) view.findViewById(R.id.btnVendor);
        btnTakePicture = (Button) view.findViewById(R.id.btnTakePicture);
        btnSavePicture = (Button) view.findViewById(R.id.btnSavePicture);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnClosedJS = (Button) view.findViewById(R.id.btnClosedJS);
        btnOutstandingJS = (Button) view.findViewById(R.id.btnOutstandingJS);
        txtDoc1No = (EditText) view.findViewById(R.id.txtDoc1No);
        txtBackFromVendorYN = (EditText) view.findViewById(R.id.txtBackFromVendorYN);
        txtSendToVendorYN = (EditText) view.findViewById(R.id.txtSendToVendorYN);
        txtReturnBackEndUserYN = (EditText) view.findViewById(R.id.txtReturnBackEndUserYN);
        txtEntryDate = (EditText) view.findViewById(R.id.txtEntryDate);
        txtOutputDate = (EditText) view.findViewById(R.id.txtOutputDate);
        txtWarrantyExpDate = (EditText) view.findViewById(R.id.txtWarrantyExpDate);
        txtCusCode = (EditText) view.findViewById(R.id.txtCusCode);
        txtCusName = (EditText) view.findViewById(R.id.txtCusName);
        txtContact = (EditText) view.findViewById(R.id.txtContact);
        txtContactTel = (EditText) view.findViewById(R.id.txtContactTel);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtAddress = (EditText) view.findViewById(R.id.txtAddress);
        txtRepairType = (EditText) view.findViewById(R.id.txtRepairType);
        txtReceiveMode = (EditText) view.findViewById(R.id.txtReceiveMode);
        txtReceiveNo = (EditText) view.findViewById(R.id.txtReceiveNo);
        txtCaseType = (EditText) view.findViewById(R.id.txtCaseType);
       // txtServiceStatus = (EditText) view.findViewById(R.id.txtServiceStatus);
        txtReceiveDate = (EditText) view.findViewById(R.id.txtReceiveDate);
        txtEntryID = (EditText) view.findViewById(R.id.txtEntryID);
        txtOutputID = (EditText) view.findViewById(R.id.txtOutputID);
        txtProductModel = (EditText) view.findViewById(R.id.txtProductModel);
        txtPartNumber = (EditText) view.findViewById(R.id.txtPartNumber);
        txtSupplierSerialNo = (EditText) view.findViewById(R.id.txtSupplierSerialNo);
        txtAccessories = (EditText) view.findViewById(R.id.txtAccessories);
        txtProblemDesc = (EditText) view.findViewById(R.id.txtProblemDesc);
        txtTermCode = (EditText) view.findViewById(R.id.txtTermCode);
        txtTermDesc = (EditText) view.findViewById(R.id.txtTermDesc);
        txtWarrantyDesc = (EditText) view.findViewById(R.id.txtWarrantyDesc);
        txtCollectedBy = (EditText) view.findViewById(R.id.txtCollectedBy);
        txtVendorCode = (EditText) view.findViewById(R.id.txtVendorCode);
        txtVendorName = (EditText) view.findViewById(R.id.txtVendorName);
        txtVendorTel = (EditText) view.findViewById(R.id.txtVendorTel);
        txtServiceNoteRemark = (EditText) view.findViewById(R.id.txtServiceNoteRemark);
        txtReturnBackBy = (EditText) view.findViewById(R.id.txtReturnBackBy);
        txtSerialNo = (EditText) view.findViewById(R.id.txtSerialNo);
        txtActionTimeStart = (EditText) view.findViewById(R.id.txtActionTimeStart);
        txtActionTimeEnd = (EditText) view.findViewById(R.id.txtActionTimeEnd);
        txtImgService = (TextView) view.findViewById(R.id.txtImgService);
        txtImgService2 = (TextView) view.findViewById(R.id.txtImgService2);
        spWarrantyStatus = (Spinner) view.findViewById(R.id.spWarrantyStatus);
        spVenWarrantyStatus = (Spinner) view.findViewById(R.id.spVenWarrantyStatus);
        spPriority = (Spinner) view.findViewById(R.id.spPriority);
        spServiceStatus = (Spinner) view.findViewById(R.id.spServiceStatus);
        imgService = (ImageView) view.findViewById(R.id.imgService);
        imgService2 = (ImageView) view.findViewById(R.id.imgService2);
        imgSignature = (ImageView) view.findViewById(R.id.imgSignature);
        btnShowSign = (Button) view.findViewById(R.id.btnShowSign);
        chkSendToVendorYN = (CheckBox) view.findViewById(R.id.chkSendToVendorYN);
        chkBackFromVendorYN = (CheckBox) view.findViewById(R.id.chkBackFromVendorYN);
        chkReturnBackEndUserYN = (CheckBox) view.findViewById(R.id.chkReturnBackEndUserYN);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Are you sure to save changes?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                update();
                            }
                        });
                alertDialog.show();
            }
        });
        btnCaseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showService("CaseType");
            }
        });

        btnRepairType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showService("RepairType");
            }
        });

        btnOutstandingJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList("");
            }
        });

        btnClosedJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList("Closed");
            }
        });

/*        btnServiceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showService("ServiceStatus");
            }
        });*/
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomer();
            }
        });
        btnTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTerm();
            }
        });
        btnVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVendor();
            }
        });

        txtSendToVendorYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtSendToVendorYN.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        txtBackFromVendorYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtBackFromVendorYN.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        txtReturnBackEndUserYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtReturnBackEndUserYN.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        txtEntryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtEntryDate.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        txtOutputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtOutputDate.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        txtWarrantyExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtWarrantyExpDate.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        txtReceiveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat = new DecimalFormat("00");
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth = (monthOfYear + 1) * 1.00;
                        final Double dDay = dayOfMonth * 1.00;
                        txtReceiveDate.setText(year + "-" + mFormat.format(dMonth) + "-" + mFormat.format(dDay));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        txtActionTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                final DecimalFormat mFormat = new DecimalFormat("00");
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final Double dMin = selectedMinute * 1.00;
                        txtActionTimeStart.setText(selectedHour + ":" + mFormat.format(dMin));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        txtActionTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                final DecimalFormat mFormat = new DecimalFormat("00");
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final Double dMin = selectedMinute * 1.00;
                        txtActionTimeEnd.setText(selectedHour + ":" + mFormat.format(dMin));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        btnSavePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
               /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, 100);*/
            }
        });

        imgService2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retImgService();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.warranty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVenWarrantyStatus.setAdapter(adapter);
        spWarrantyStatus.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getActivity(), R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter2);
        loadStatus();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String dated = sdf.format(date);
        String timed = sdf2.format(date);
        txtBackFromVendorYN.setText(dated);
        txtSendToVendorYN.setText(dated);
        txtReturnBackEndUserYN.setText(dated);
        txtEntryDate.setText(dated);
        txtOutputDate.setText(dated);
        txtWarrantyExpDate.setText(dated);
        txtReceiveDate.setText(dated);

        txtActionTimeStart.setText(timed);
        txtActionTimeEnd.setText(timed);
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        // Dialog Function
        dialog = new Dialog(getActivity());
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        btnShowSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Function call for Digital Signature
                dialog_action();

            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnprintsign();
            }
        });
        retLastNo();
        readyPrinter();
        return view;
    }

    private void retImgService(){
        String Doc1No=txtDoc1No.getText().toString();
        RetImgService retImg=new RetImgService(getActivity(),Doc1No);
        retImg.execute();
    }
    private void update() {
        String Doc1No = txtDoc1No.getText().toString();
        String ActionTimeStart = txtActionTimeStart.getText().toString();
        String ActionTimeEnd = txtActionTimeEnd.getText().toString();
        String servicenoteremark = txtServiceNoteRemark.getText().toString();
        String servicestatus = spServiceStatus.getSelectedItem().toString();
        Bitmap bmp=((BitmapDrawable)imgService.getDrawable()).getBitmap();
        String PhotoFile = encodeImage(bmp);
        Uri tempUri = getImageUri(getContext(), bmp);
        String PhotoFile2 = getRealPathFromURI(tempUri);
            UpdateService update = new UpdateService(getActivity(), Doc1No, ActionTimeStart,
                    ActionTimeEnd, servicenoteremark, servicestatus,
                    PhotoFile, PhotoFile2);
        update.execute();
    }

    private void fnsave() {
        try {
            DBAdapter db = new DBAdapter(getContext());
            db.openDB();
            String Doc1No = txtDoc1No.getText().toString();
            String CusCode = txtCusCode.getText().toString();
            String CusName = txtCusName.getText().toString();
            String Contact = txtContact.getText().toString();
            String ContactTel = txtContactTel.getText().toString();
            String Email = txtEmail.getText().toString();
            String Address = txtAddress.getText().toString();
            String RepairType = txtRepairType.getText().toString();
            String ReceiveMode = txtReceiveMode.getText().toString();
            String ReceiveNo = txtReceiveNo.getText().toString();
            String CaseType = txtCaseType.getText().toString();
            String ServiceStatus = txtServiceStatus.getText().toString();
            String ReceiveDate = txtReceiveDate.getText().toString();
            String EntryID = txtEntryID.getText().toString();
            String EntryDate = txtEntryDate.getText().toString();
            String OutputID = txtOutputID.getText().toString();
            String OutputDate = txtOutputDate.getText().toString();
            String ProductModel = txtProductModel.getText().toString();
            String PartNumber = txtPartNumber.getText().toString();
            String SerialNo = txtSerialNo.getText().toString();
            String SupplierSerialNo = txtSupplierSerialNo.getText().toString();
            String Accessories = txtAccessories.getText().toString();
            String ProblemDesc = txtProblemDesc.getText().toString();
            String TermCode = txtTermCode.getText().toString();
            String TermDesc = txtTermDesc.getText().toString();
            String WarrantyStatus = spWarrantyStatus.getSelectedItem().toString();
            if (WarrantyStatus.equals("IN WARRANTY")) {
                WarrantyStatus = "1";
            } else {
                WarrantyStatus = "0";
            }
            String WarrantyExpDate = txtWarrantyExpDate.getText().toString();
            String WarrantyDesc = txtWarrantyDesc.getText().toString();
            String CollectedBy = txtCollectedBy.getText().toString();
            String VendorCode = txtVendorCode.getText().toString();
            String VendorName = txtVendorName.getText().toString();
            String VendorTel = txtVendorTel.getText().toString();
            String VenWarrantyStatus = spVenWarrantyStatus.getSelectedItem().toString();
            if (VenWarrantyStatus.equals("IN WARRANTY")) {
                VenWarrantyStatus = "1";
            } else {
                VenWarrantyStatus = "0";
            }
            String ServiceNoteRemark = txtServiceNoteRemark.getText().toString();
            String SendToVendorDate = txtSendToVendorYN.getText().toString();
            String SendToVendorYN = "0";
            if (chkSendToVendorYN.isChecked()) {
                SendToVendorYN = "1";
            }
            String BackFromVendorDate = txtBackFromVendorYN.getText().toString();
            String BackFromVendorYN = "0";
            if (chkBackFromVendorYN.isChecked()) {
                BackFromVendorYN = "1";
            }
            String ReturnBackEndUserDate = txtReturnBackEndUserYN.getText().toString();
            String ReturnBackEndUserYN = "0";
            if (chkReturnBackEndUserYN.isChecked()) {
                ReturnBackEndUserYN = "1";
            }
            String ReturnBackBy = txtReturnBackBy.getText().toString();
            Bitmap bitmap = ((BitmapDrawable) imgSignature.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Bitmap bitmap2 = ((BitmapDrawable) imgService.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
            String encodedImg2 = Base64.encodeToString(byteArray2, Base64.DEFAULT);

            String insertHd = "insert into stk_service_hd_temp(Doc1No, cuscode, " +
                    " cusname, Contact, receiptno, " +
                    " receiptdate, repairtype, casetype, " +
                    " entryid, d_ate, outputid, " +
                    " outputdate, receivemode, termcode, " +
                    " productmodel, partno, serialno, " +
                    " supplierserialno, warrantystatus, warrantydesc, " +
                    " warrantyexpirydate, accessories, problemdesc, " +
                    " collectedby, collecteddate, sendtovendorYN, " +
                    " sendtovendordate, vendorwarrantystatus, vendorcode, " +
                    " vendorname, vendortelno, backfromvendorYN, " +
                    " backfromvendordate, returnbackenduserYN, returnbackenduserdate, " +
                    " returnbackenduserby, servicenoteremark, L_ink, " +
                    " Address, ContactTel, Email, " +
                    " servicestatus,  ImgService, Signature," +
                    " Technician)values('" + Doc1No + "', '" + CusCode + "', " +
                    " '" + CusName + "', '" + Contact + "', '" + ReceiveNo + "', " +
                    " '" + ReceiveDate + "', '" + RepairType + "', '" + CaseType + "'," +
                    " '" + EntryID + "', '" + EntryDate + "', '" + OutputID + "'," +
                    " '" + OutputDate + "', '" + ReceiveMode + "', '" + TermCode + "'," +
                    " '" + ProductModel + "', '" + PartNumber + "', '" + SerialNo + "', " +
                    " '" + SupplierSerialNo + "', '" + WarrantyStatus + "', '" + WarrantyDesc + "'," +
                    " '" + WarrantyExpDate + "', '" + Accessories + "', '" + ProblemDesc + "'," +
                    " '" + CollectedBy + "', '" + EntryDate + "', '" + SendToVendorYN + "'," +
                    " '" + SendToVendorDate + "', '" + VenWarrantyStatus + "', '" + VendorCode + "'," +
                    " '" + VendorName + "', '" + VendorTel + "', '" + BackFromVendorYN + "'," +
                    " '" + BackFromVendorDate + "', '" + ReturnBackEndUserYN + "', '" + ReturnBackEndUserDate + "'," +
                    " '" + ReturnBackBy + "', '" + ServiceNoteRemark + "', '1'," +
                    " '" + Address + "', '" + ContactTel + "', '" + Email + "'," +
                    " '" + ServiceStatus + "', '" + vImgService + "', '" + encodedImg + "'," +
                    " 'Android Service' )";
            Log.d("INSERT TEMP", insertHd);
            if (CusCode.isEmpty()) {
                Toast.makeText(this.getContext(), "Customer Code cannot empty", Toast.LENGTH_SHORT).show();
            } else if (ProductModel.isEmpty()) {
                Toast.makeText(this.getContext(), "Product Model cannot empty", Toast.LENGTH_SHORT).show();
            } else {
                long addnew = db.addQuery(insertHd);
                if (addnew > 0) {
                    SaveService save = new SaveService(getActivity(), Doc1No);
                    save.execute();
                }
            }
            db.closeDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void fnprintsign() {
        // Bitmap bmp=((BitmapDrawable)imgSignature.getDrawable()).getBitmap();
        imgSignature.setDrawingCacheEnabled(true);
        Bitmap bmap = imgSignature.getDrawingCache();
        BluetoothPrinter fncheck2 = new BluetoothPrinter();
        Boolean isBT = fncheck2.fnBluetooth2(NamePrinter, bmap);
        if (bmap != null) {
            if (isBT) {
                Toast.makeText(this.getContext(), "success print", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this.getContext(), "bitmap is null", Toast.LENGTH_SHORT).show();
        }

    }

    private void readyPrinter() {
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            Cursor cPrint = db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter = cPrint.getString(3);
                vPort = cPrint.getString(5);
            }
            db.closeDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void showList(String Status) {
        Bundle b = new Bundle();
        b.putString("STATUS_KEY", Status);
        DialogListJS dialogListJS = new DialogListJS();
        dialogListJS.setArguments(b);
        dialogListJS.show(getFragmentManager(), "List Service");
    }

    private void showCustomer() {
        Bundle b = new Bundle();
        b.putString("DOCTYPE_KEY", "Service");
        DialogCustomer dialogCustomer = new DialogCustomer();
        dialogCustomer.setArguments(b);
        dialogCustomer.show(getFragmentManager(), "List Customer");
    }

    private void showVendor() {
        Bundle b = new Bundle();
        b.putString("DOCTYPE_KEY", "Service");
        DialogSupplier dialogSupplier = new DialogSupplier();
        dialogSupplier.setArguments(b);
        dialogSupplier.show(getFragmentManager(), "List Supplier");
    }

    private void showTerm() {
        Bundle b = new Bundle();
        b.putString("DOCTYPE_KEY", "Service");
        DialogTerm dialogTerm = new DialogTerm();
        dialogTerm.setArguments(b);
        dialogTerm.show(getFragmentManager(), "List Term");
    }

    private void loadStatus(){
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getActivity(), R.array.srstatus_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServiceStatus.setAdapter(adapter2);
    }
    private void showService(String DocType) {

        Bundle b = new Bundle();
        b.putString("DOCTYPE_KEY", DocType);
        DialogService dialogService = new DialogService();
        dialogService.setArguments(b);
        dialogService.show(getFragmentManager(), "List Service");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Drawable image = new BitmapDrawable(Bitmap.createScaledBitmap(photo, 580, 450, true));
            imgService.setImageDrawable(image);
            Uri tempUri = getImageUri(getContext(), photo);
            vImgService2 = getRealPathFromURI(tempUri);
            txtImgService2.setText(vImgService2);
           // Log.d("PATH FILE ", vImgService2);
            //imgService.setImageBitmap(photo);
           /* try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
               // vImgService = encodeImage(selectedImage);
                //txtImgService.setText(vImgService);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void insertSignature(String vImgSignature) {
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String Doc1No = txtDoc1No.getText().toString();
            String check = "select count(*)as numrows from signature_temp where Doc1No='" + Doc1No + "' ";
            Cursor rsCheck = db.getQuery(check);
            int numrows = 0;
            while (rsCheck.moveToNext()) {
                numrows = rsCheck.getInt(0);
            }
            if (numrows == 0) {
                String qInsert = "insert into signature_temp(Doc1No, Signature)values('" + Doc1No + "', '" + vImgSignature + "')";
                Log.d("Insert", qInsert);
                db.addQuery(qInsert);
            } else {
                String qInsert = "update signature_temp set Signature='" + vImgSignature + "' where Doc1No='" + Doc1No + "' ";
                Log.d("Update", qInsert);
                db.addQuery(qInsert);
            }
            loadSignature();
            db.closeDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void loadSignature() {
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String Doc1No = txtDoc1No.getText().toString();
            String check = "select Signature from signature_temp where Doc1No='" + Doc1No + "' ";
            Cursor rsCheck = db.getQuery(check);
            while (rsCheck.moveToNext()) {
                String signature = rsCheck.getString(0);
                final String pureBase64Encoded = signature.substring(signature.indexOf(",") + 1);
                // Log.d("String SIGN",pureBase64Encoded);
                final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imgSignature.setImageBitmap(bmp);
            }
            db.closeDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    // Function for Digital Signature
    public void dialog_action() {
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view1 = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.v("log_tag", "Panel Saved");
                view1.setDrawingCacheEnabled(true);
                mSignature.save(view1, StoredPath);
                dialog.dismiss();
                Toast.makeText(getContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                //mSignature = new signature(getContext(), null);
                // Calling the same class
                getActivity().recreate();
                retLastNo();
                ///

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                //mSignature = new signature(getContext(), null);
                // Calling the same class
                getActivity().recreate();
                retLastNo();
            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                vImgSignature = encodeImage(bitmap);
                insertSignature(vImgSignature);
                //setSignature(bitmap);
                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public void setSignature(Bitmap bmpSign) {
        imgSignature.setImageBitmap(bmpSign);
    }

    private void retLastNo() {
        try {
            DBAdapter db = new DBAdapter(getContext());
            db.openDB();
            String Doc1No = "";
            String vQuery = "select Prefix,LastNo from sys_runno_dt where RunNoCode='Service' ";
            Cursor cRunNo = db.getQuery(vQuery);
            while (cRunNo.moveToNext()) {
                String Prefix = cRunNo.getString(0);
                String LastNo = cRunNo.getString(1);
                Doc1No = Prefix + LastNo;

            }
            txtDoc1No.setText(Doc1No);
            String vDel = "Delete from stk_service_hd_temp ";
            db.addQuery(vDel);
            loadSignature();
            readyPrinter();
            db.closeDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
