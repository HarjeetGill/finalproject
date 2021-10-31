package com.techai.shiftme.ui.agency.home.tabs;

import com.techai.shiftme.data.model.Request;

public interface IApproveRejectListener {

    void updateStatus(Boolean isApproved, int position);

    void sendToTracking(Request request);

}
