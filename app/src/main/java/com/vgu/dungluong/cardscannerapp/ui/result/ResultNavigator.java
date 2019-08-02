package com.vgu.dungluong.cardscannerapp.ui.result;

import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by Dung Luong on 02/07/2019
 */
public interface ResultNavigator {

    ImageView getCardImageView();

    void handleError(String error);

    void showMessage(String mess);

    TessBaseAPI getTesseractApi();

    TessBaseAPI getTesseractApi2();

    File getFileForCropImage();
}
