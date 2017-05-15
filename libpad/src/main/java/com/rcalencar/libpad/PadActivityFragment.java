/*
 * Copyright (C) 2017 Rodrigo Costa de Alencar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rcalencar.libpad;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class PadActivityFragment extends Fragment implements Contract.View {
    public static final String TAG_NUMBER = "NUMBER";
    public static final int CURSOR_PADDING = 2;
    public static final int END_POS = -1;
    @BindView(R2.id.scrollView_custom_pad) HorizontalScrollView mScrollView;
    @BindView(R2.id.custom_pad) LinearLayout mDialPad;
    @BindView(R2.id.frame_layout_delete) FrameLayout mDeleteFrame;
    private OnFragmentInteractionListener mListener;
    private View mCursor;
    private Contract.Presenter mPresenter;

    public static PadActivityFragment newInstance() {
        return new PadActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dial, container, false);
        ButterKnife.bind(this, rootView);

        mCursor = inflater.inflate(R.layout.cursor, container, false);
        mDialPad.addView(mCursor);
        mDeleteFrame.setOnDragListener(new OnRemoveDragListener());

        return rootView;
    }

    @OnClick(R2.id.button_del)
    public void onDeleteLastDigit() {
        if (mDialPad.getChildCount() - CURSOR_PADDING > 0) {
            removeNumber(mDialPad.getChildCount() - CURSOR_PADDING);
        }
    }

    @OnClick(R2.id.button_send)
    public void onResultOK() {
        mPresenter.resultOK();
    }

    @OnClick({R2.id.digit1, R2.id.digit2, R2.id.digit3, R2.id.digit4, R2.id.digit5, R2.id.digit6, R2.id.digit7, R2.id.digit8, R2.id.digit9, R2.id.digit0})
    public void onClickDigit(View view) {
        addNumber(((Button) view).getText().toString(), END_POS);
    }

    @OnLongClick({R2.id.digit1, R2.id.digit2, R2.id.digit3, R2.id.digit4, R2.id.digit5, R2.id.digit6, R2.id.digit7, R2.id.digit8, R2.id.digit9, R2.id.digit0})
    public boolean onLongClick(View view) {
        startDragAndDrop(view);
        return true;
    }

    private void addNumber(final String value, int index) {
        boolean scroll = false;
        if (index == END_POS) {
            scroll = true;
            index = mDialPad.getChildCount() - 1;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        FrameLayout dragAndDropPlaceHolder = (FrameLayout) layoutInflater.inflate(R.layout.placeholder, null);
        dragAndDropPlaceHolder.setOnDragListener(new OnAddDragListener());
        dragAndDropPlaceHolder.addView(layoutInflater.inflate(R.layout.placeholder_inner_not_selected, null));

        TextView number = (TextView) layoutInflater.inflate(R.layout.number, null);
        number.setText(value);
        number.setTag(TAG_NUMBER);

        number.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startDragAndDrop(view);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        mDialPad.addView(number, index);
        mDialPad.addView(dragAndDropPlaceHolder, index);

        if (scroll) {
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });
        }
    }

    private void removeNumber(final int i) {
        removeDigit(i);
        removePlaceHolder(i);
    }

    private void removeDigit(final int i) {
        mDialPad.removeViewAt(i);
    }

    private void removePlaceHolder(final int i) {
        mDialPad.removeViewAt(i - 1);
    }

    private void startDragAndDrop(final View view) {
        ClipData data = ClipData.newPlainText("", "");
        Drawable background = view.getBackground();
        view.setBackground(null);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDragAndDrop(data, shadowBuilder, view, 0);
        view.setBackground(background);
    }

    private boolean isViewNumberInDisplay(final View sourceView) {
        return TAG_NUMBER.equals(sourceView.getTag());
    }

    // MVP view
    @Override
    public void showNumber(final String number) {
        if (number != null) {
            for (char c : number.toCharArray()) {
                addNumber(String.valueOf(c), -1);
            }
        }
    }

    @Override
    public String getNumber() {
        String result = null;
        View child;
        for (int i = 0; i < mDialPad.getChildCount(); i++) {
            child = mDialPad.getChildAt(i);
            if (child instanceof TextView && isViewNumberInDisplay(child)) {
                if (result == null) result = "";
                result += ((TextView) child).getText().toString();
            }
        }

        return result;
    }

    @Override
    public void returnOK(Intent intent) {
        mListener.onResultOk(intent);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        mPresenter = presenter;
    }
    // MVP view end

    // fragment lifecycle
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onResultOk(Intent intent);
    }
    // fragment lifecycle end

    // Drag and drop
    private class OnRemoveDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            View sourceView = (View) event.getLocalState();
            FrameLayout targetView = (FrameLayout) view;

            if (!isViewNumberInDisplay(sourceView)) {
                return false;
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mCursor.setVisibility(View.INVISIBLE);
                    deleteFrameSelected(targetView);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // nothing
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    // nothing
                    break;
                case DragEvent.ACTION_DROP:
                    int i = getChildPosition(sourceView);
                    removeNumber(i);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mCursor.setVisibility(View.VISIBLE);
                    sourceView.setVisibility(View.VISIBLE);
                    deleteFrameDefault(targetView);
                default:
                    break;
            }
            return true;
        }
    }

    private class OnAddDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            TextView sourceView = (TextView) event.getLocalState();
            FrameLayout targetView = (FrameLayout) view;

            if (isViewNumberInDisplay(sourceView)) {
                return false;
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mCursor.setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    placeHolderSelected(targetView);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    placeHolderDefault(targetView);
                    break;
                case DragEvent.ACTION_DROP:
                    int i = getChildPosition(targetView);
                    addNumber(sourceView.getText().toString(), i);
                    placeHolderDefault(targetView);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mCursor.setVisibility(View.VISIBLE);
                default:
                    break;
            }
            return true;
        }
    }

    private int getChildPosition(final View sourceView) {
        int i;
        for (i = 0; i < mDialPad.getChildCount(); i++) {
            if (mDialPad.getChildAt(i) == sourceView) {
                break;
            }
        }
        return i;
    }

    private void placeHolderSelected(final FrameLayout placeholder) {
        LayoutInflater li = LayoutInflater.from(getContext());
        TextView placeholderInnerSelected = (TextView) li.inflate(R.layout.placeholder_inner_selected, null);
        placeholder.removeViewAt(0);
        placeholder.addView(placeholderInnerSelected);
    }

    private void placeHolderDefault(final FrameLayout placeholder) {
        LayoutInflater li = LayoutInflater.from(getContext());
        TextView placeholderInner = (TextView) li.inflate(R.layout.placeholder_inner_not_selected, null);
        placeholder.removeViewAt(0);
        placeholder.addView(placeholderInner);
    }

    private void deleteFrameDefault(final FrameLayout targetView) {
        targetView.removeViewAt(0);
    }

    private void deleteFrameSelected(final FrameLayout targetView) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View placeholderInnerSelected = li.inflate(R.layout.delete_layout, null);
        targetView.addView(placeholderInnerSelected);
    }
    // Drag and drop end
}