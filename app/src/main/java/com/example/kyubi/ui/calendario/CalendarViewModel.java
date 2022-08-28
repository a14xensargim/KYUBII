package com.example.kyubi.ui.calendario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public CalendarViewModel() {

    }

    public LiveData<String> getText() {
        return mText;
    }
}