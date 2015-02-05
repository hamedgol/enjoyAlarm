package com.android.enjoyalarm.alarmliststate;

import com.android.enjoyalarm.view.ViewControlInterface;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class State {

	protected ViewControlInterface mControlInterface;
	
	public State(ViewControlInterface controlInterface) {
		mControlInterface = controlInterface;
	}
	
	abstract public void handleTouchEvent(MotionEvent event);
	
	abstract public void handleDraw(Canvas canvas);
}
