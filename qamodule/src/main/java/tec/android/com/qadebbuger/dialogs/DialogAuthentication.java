package tec.android.com.qadebbuger.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.widget.TextView;

import library.android.com.qamodule.R;
import tec.android.com.qadebbuger.configuration.models.AuthenticationRequest;
import tec.android.com.qadebbuger.interfaces.QAAuthenticationCallback;
import tec.android.com.qadebbuger.managers.FirebaseManager;

public class DialogAuthentication extends DialogFragment implements FirebaseManager.FirebaseManagerCallback {

    private EditText etAuthenticationEmail;
    private EditText etAuthenticationPassword;
    private TextView btnAccept;
    private TextView btnCancel;
    private LinearLayoutCompat loadingContainer;
    private String packageName;
    private FirebaseManager firebaseManager;
    private QAAuthenticationCallback mListener;
    private LinearLayoutCompat layoutForm;

    /**
     * Required empty private constructor
     */
    @SuppressLint("ValidFragment")
    private DialogAuthentication() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialogTheme);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_authentication, null);
        dialog.setContentView(dialogView);
        setupFirebaseManager();
        bindResources(dialogView);
        setCancelable(false);
    }

    private void bindResources(View view) {
        etAuthenticationEmail = view.findViewById(R.id.authentication_email);
        etAuthenticationPassword = view.findViewById(R.id.authentication_password);
        btnAccept = view.findViewById(R.id.authentication_accept);
        btnCancel = view.findViewById(R.id.authentication_cancel);
        loadingContainer = view.findViewById(R.id.authentication_loading);
        layoutForm = view.findViewById(R.id.layout_form);

        btnAccept.setOnClickListener(onAccept);
        btnCancel.setOnClickListener(onCancel);
    }

    private void setupFirebaseManager() {
        firebaseManager = new FirebaseManager(getActivity());
        firebaseManager.setAuthenticationListener(this);
    }

    private void authenticate() {
        AuthenticationRequest request = new AuthenticationRequest(
                etAuthenticationEmail.getText().toString(),
                etAuthenticationPassword.getText().toString(),
                packageName
        );

        firebaseManager.authenticate(request);
    }

    View.OnClickListener onAccept = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            authenticate();
        }
    };

    View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    @Override
    public void onAuthenticationSucceeded() {
        dismiss();
        mListener.onSuccessfullLogin();
    }

    @Override
    public void onAuthenticationFailed() {
        dismiss();
        mListener.onLoginFailed();
    }

    @Override
    public void showLoading() {

        loadingContainer.setVisibility(View.VISIBLE);
        layoutForm.setVisibility(View.GONE);

    }

    @Override
    public void hideLoading() {

        loadingContainer.setVisibility(View.GONE);
        layoutForm.setVisibility(View.VISIBLE);

    }

    /**
     * Custom builder design pattern
     */
    public static class Builder {
        private QAAuthenticationCallback authenticationListener;
        private String packageName;

        /**
         * Sets the listener required for authenticating
         * @param authenticationListener
         * @return
         */
        public Builder setAuthenticationListener(@NonNull QAAuthenticationCallback authenticationListener) {
            this.authenticationListener = authenticationListener;
            return this;
        }

        /**
         * Sets the package name of the app to be authenticated
         * @param packageName
         * @return
         */
        public Builder setPackageName(@NonNull String packageName) {
            this.packageName = packageName;
            return this;
        }

        /**
         * Method to build the dialog with custom parameters specified before.
         * If any of the methods weren't called, it will raise an exception.
         * @return
         */
        public DialogAuthentication create() {
            checkNotNull();
            DialogAuthentication dialogAuthentication = new DialogAuthentication();
            dialogAuthentication.packageName = this.packageName;
            dialogAuthentication.mListener = this.authenticationListener;
            return dialogAuthentication;
        }

        private void checkNotNull() {
            if (packageName == null) {
                throw new IllegalArgumentException("You must set package name to initialize this dialog.");
            }

            if (authenticationListener == null) {
                throw new IllegalArgumentException("You must set authentication listener to initialize this dialog.");
            }
        }
    }
}
