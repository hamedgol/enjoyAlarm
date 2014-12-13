package com.enjoyalarm.alarmliststate;

import java.util.List;

import com.enjoyalarm.drawcomponent.AlarmItemComponent;
import com.enjoyalarm.view.ViewControlInterface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;


public class AnimToDeleteState extends State {

	private List<AlarmItemComponent> mItems;
	private AlarmItemComponent mDeleteItem;
	private int mDeleteIndex;
	private float mPositionY;
	private float mConstFactor;
	private float mTempConstFactor;
	private float mGapPlusWidthFactor;
	private float fLimit1;
	private float fLimit2;
	private float mViewHeight;
	private int mDirection;//0.to top 1.to bottom
	private boolean mThreadFlag;
	private int mAnimState;//0.first 1.second 2.last
	
	
	
	public AnimToDeleteState(ViewControlInterface controlInterface,
			AlarmItemComponent deleteItem, List<AlarmItemComponent> items,
			int deleteIndex, float constFactor, float positionY, int direction) {
		super(controlInterface);

		mItems = items;
		mDeleteItem = deleteItem;
		mDeleteIndex = deleteIndex;
		mConstFactor = constFactor;
		mGapPlusWidthFactor = ListState.mAlarmListDrawer.getGapPlusWidthFactor();
		mViewHeight = mControlInterface.getViewHeight();
		mPositionY = positionY;
		mDirection = direction;
		
		mThreadFlag = true;
		new AnimThread().start();
	}

	@Override
	public void handleTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN && mAnimState == 0) {
			mThreadFlag = false;
			mControlInterface
					.changeState(new DeleteState(mControlInterface,
							mDeleteItem, mItems, mDeleteIndex, mConstFactor,
							mPositionY));
		}
	}

	@Override
	public void handleDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		switch(mAnimState) {
		case 0: {
			for (AlarmItemComponent item: mItems) {
				item.draw(canvas, mConstFactor);
			}
			mDeleteItem.draw(canvas, mPositionY / mViewHeight);
			break;
		}
		
		case 1: {
			for (int i = 0; i < mItems.size(); i++) {
				if (i < mDeleteIndex) {//left
					mItems.get(i).draw(canvas, mConstFactor);
					
				} else {//right
					if (mTempConstFactor > fLimit1) {
						mTempConstFactor = fLimit1;
						deleteAlarm();
						mAnimState = 2;
					}
					mItems.get(i).draw(canvas, mTempConstFactor);
				}
			}
			break;
		}
		
		case 2: {
			if (mConstFactor < fLimit2) {
				mConstFactor = fLimit2;
				changeToListState();
			}
			for (int i = 0; i < mItems.size(); i++) {
				if (i < mDeleteIndex) {//left
					mItems.get(i).draw(canvas, mConstFactor);
					
				} else {//right
					mItems.get(i).draw(canvas, mConstFactor + mGapPlusWidthFactor);
				}
			}
			break;
		}
		}
	}
	
	
	private void changeToListState() {
		mControlInterface.changeState(new ListState(mControlInterface, mConstFactor));
	}
	
	private void deleteAlarm() {
		mControlInterface.handleDeleteAlarmItem(mDeleteIndex);
	}
	
	
	private class AnimThread extends Thread {
		@Override
		public void run() {
			float yVelocity = Math.abs(mPositionY) / 32;
			float yGap = yVelocity * 0.4f;
			float yLimit = mViewHeight * 0.5f;
			
			mTempConstFactor = mConstFactor;
			float fVelocity = mGapPlusWidthFactor / 20;
			fLimit1 = mTempConstFactor + mGapPlusWidthFactor;
			fLimit2 = mConstFactor - mGapPlusWidthFactor / 2;
			
			Loop:while(mThreadFlag) {
				System.out.println(mAnimState);
				switch(mAnimState) {
				case 0: {
					if (mDirection == 0) {//to top
						mPositionY -= yVelocity;
					} else {//to bottom
						mPositionY += yVelocity;
					}
					
					yVelocity += yGap;
					
					if (Math.abs(mPositionY) > yLimit) {
						mAnimState = 1;
					}
					break;
				}
				
				case 1: {
					mTempConstFactor += fVelocity;
					break;
				}
				
				case 2: {
					mConstFactor -= fVelocity;
					if (mConstFactor < fLimit2) {
						mControlInterface.refreshDraw();
						break Loop;
					}
					break;
				}
				}
				
				mControlInterface.refreshDraw();
				
				try {
					sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
