package com.dengtuo.android.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

class RenderView extends View {
   public RenderView(Context context) {
      super(context);
   }

   public RenderView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public RenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public RenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
   }
}
