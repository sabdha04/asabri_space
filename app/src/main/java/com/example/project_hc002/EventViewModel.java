package com.example.project_hc002;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class EventViewModel extends ViewModel {

    private MutableLiveData<List<Event>> eventList;

    public EventViewModel() {
        eventList = new MutableLiveData<>();
    }

    public LiveData<List<Event>> getEvents() {
        return eventList;
    }

    public void setEvents(List<Event> events) {
        eventList.setValue(events);
    }
}