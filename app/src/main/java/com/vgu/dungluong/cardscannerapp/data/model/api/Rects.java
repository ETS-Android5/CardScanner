package com.vgu.dungluong.cardscannerapp.data.model.api;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.vgu.dungluong.cardscannerapp.data.model.local.Corners;
import com.vgu.dungluong.cardscannerapp.data.model.local.OnTouchZone;
import com.vgu.dungluong.cardscannerapp.utils.AppLogger;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Dung Luong on 25/07/2019
 */
@AutoValue.CopyAnnotations
@AutoValue
public abstract class Rects implements Parcelable {

    @AutoValue.CopyAnnotations
    @SerializedName("rect")
    public abstract List<List<Integer>> rects();

    public static Rects create(List<List<Integer>> rects){
        return builder().rects(rects).build();
    }

    public static Builder builder(){
        return new AutoValue_Rects.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder{

        public abstract Builder rects(List<List<Integer>> rects);

        public abstract Rects build();
    }

    public static TypeAdapter<Rects> typeAdapter(Gson gson){
        return new AutoValue_Rects.GsonTypeAdapter(gson);
    }

    public List<Corners> getCorners(){
        List<Corners> rectCorners = new ArrayList<>();
        rects().stream()
                .sorted((rect1, rect2) -> Integer.compare(rect1.get(0), rect2.get(0)))
                .sorted((rect1, rect2) -> Integer.compare(rect1.get(1), rect2.get(1)))
                .forEach(rect -> {
            List<Point> coordinates = new ArrayList<>();
            int height1 = (rect.get(7) - rect.get(1));
                    int height2 = (rect.get(5) - rect.get(3));
            for(int i=0; i < rect.size(); i+=2){
                int x = rect.get(i);
                int y = rect.get(i+1);

                if (i == 0) y -= height1 / 4;
                else if (i == 2) y -= height2 / 5;
                else if(i == 4) y += height2 / 4;
                else if (i == 6) y += height1 / 5;

                coordinates.add(new Point(x, y));
            }
            Corners corners = new Corners(coordinates);
            rectCorners.add(corners);
        });
        return rectCorners;
    }

}
