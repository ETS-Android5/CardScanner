package com.vgu.dungluong.cardscannerapp.di;

import android.hardware.Camera;

import com.vgu.dungluong.cardscannerapp.data.permission.PermissionHelper;

import io.reactivex.Observable;

/**
 * Created by Dung Luong on 17/06/2019
 */
public interface DataManager extends PermissionHelper {

    Observable<Boolean> handleTakenPictureByte(byte[] bytes, Camera camera);


}