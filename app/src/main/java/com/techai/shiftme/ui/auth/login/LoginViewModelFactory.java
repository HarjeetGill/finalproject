package com.techai.shiftme.ui.auth.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.techai.shiftme.ui.agency.home.RequestsViewModel;
import com.techai.shiftme.ui.auth.changepassword.ChangePasswordViewModel;
import com.techai.shiftme.ui.auth.signup.SignupViewModel;
import com.techai.shiftme.ui.customer.home.CustomerRequestsViewModel;
import com.techai.shiftme.ui.customer.home.tabs.sendrequest.SendRequestViewModel;
import com.techai.shiftme.ui.userprofile.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application application;

    public LoginViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass == LoginViewModel.class) {
            try {
                return (T) new LoginViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (modelClass == SignupViewModel.class) {
            try {
                return (T) new SignupViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (modelClass == ChangePasswordViewModel.class) {
            try {
                return (T) new ChangePasswordViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (modelClass == UserProfileViewModel.class) {
            try {
                return (T) new UserProfileViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (modelClass == RequestsViewModel.class) {
            try {
                return (T) new RequestsViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (modelClass == CustomerRequestsViewModel.class) {
            try {
                return (T) new CustomerRequestsViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (modelClass == SendRequestViewModel.class) {
            try {
                return (T) new SendRequestViewModel(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
