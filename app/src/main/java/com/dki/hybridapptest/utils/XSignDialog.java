package com.dki.hybridapptest.utils;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.tester.XSignCertPolicy;
import com.dki.hybridapptest.tester.XSignTester;

public class XSignDialog {


    public static AlertDialog processSignData(Context context, DialogInterface.OnClickListener listener) {
        Log.d(TAG, "processSignData:==");
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.dialog_input);

        ((TextView) dialogView.findViewById(R.id.txt_input1_desc)).setText(R.string.dialog_input_plainText);
        ((TextView) dialogView.findViewById(R.id.txt_input2_desc)).setText(R.string.dialog_input_cert_password);

        signDialog.setTitle("인증서 서명 검증");
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(R.string.dialog_confirm_ok, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    // 20.04.13 : XML 서명 Dialog 추가
    public static AlertDialog processXMLSignData(Context context, String signedInfoVal, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.dialog_xml_input);
        TextView signedInfoField = dialogView.findViewById(R.id.txt_signedInfoVal);
        signedInfoField.setText(signedInfoVal + "\n");

        signDialog.setTitle("XML 서명 데이터 생성");
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(R.string.dialog_confirm_ok, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    public static AlertDialog processShowCert(Context context, XSignTester.CertDetailInfo paramCert, int nTitleResID, int nPositiveBtnTxt, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.activity_cert_detail);

        ((TextView) dialogView.findViewById(R.id.commonTitle)).setVisibility(View.GONE);

        ((TextView) dialogView.findViewById(R.id.txt_cert_info_name)).setText(XSignCertPolicy.parserUserName(paramCert.getSubjectDN()));
        ((TextView) dialogView.findViewById(R.id.txt_cert_info_issur)).setText(XSignCertPolicy.parseISSUER(paramCert.getIssur()));
        ((TextView) dialogView.findViewById(R.id.txt_cert_info_objective)).setText(XSignCertPolicy.parseOID(paramCert.getOID()));
        ((TextView) dialogView.findViewById(R.id.txt_cert_info_dn)).setText(paramCert.getSubjectDN());
        ((TextView) dialogView.findViewById(R.id.txt_cert_info_from)).setText(paramCert.getExpirationFrom());
        ((TextView) dialogView.findViewById(R.id.txt_cert_info_to)).setText(paramCert.getExpirationTo());


        signDialog.setTitle(nTitleResID);
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(nPositiveBtnTxt, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    public static AlertDialog processInputPlainText(Context context, int nTitleResID, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.dialog_input);

        ((TextView) dialogView.findViewById(R.id.txt_input1_desc)).setText(R.string.dialog_input_plainText);

        ((TextView) dialogView.findViewById(R.id.txt_input2_desc)).setVisibility(View.GONE);
        ((TextView) dialogView.findViewById(R.id.edit_input2)).setVisibility(View.GONE);

        signDialog.setTitle(nTitleResID);
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(R.string.dialog_confirm_ok, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    public static AlertDialog processInputPassword(Context context, int nTitleResID, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.dialog_input);

        ((TextView) dialogView.findViewById(R.id.txt_input1_desc)).setText(R.string.dialog_input_cert_password);

        ((TextView) dialogView.findViewById(R.id.txt_input2_desc)).setVisibility(View.GONE);
        ((TextView) dialogView.findViewById(R.id.edit_input2)).setVisibility(View.GONE);

        signDialog.setTitle(nTitleResID);
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(R.string.dialog_confirm_ok, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    public static AlertDialog processInputTwoEdit(Context context, int nTitleResID, int nDesc1, int nDesc2, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder signDialog = new AlertDialog.Builder(context);
        View dialogView = getDialogView(context, R.layout.dialog_input);

        ((TextView) dialogView.findViewById(R.id.txt_input1_desc)).setText(nDesc1);
        ((TextView) dialogView.findViewById(R.id.txt_input2_desc)).setText(nDesc2);

        signDialog.setTitle(nTitleResID);
        signDialog.setCancelable(false);
        signDialog.setView(dialogView);
        signDialog.setPositiveButton(R.string.dialog_confirm_ok, listener);
        signDialog.setNegativeButton("취소", null);

        return signDialog.create();
    }

    private static View getDialogView(Context context, int nResourceID) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(nResourceID, null);

        return view;
    }
}
